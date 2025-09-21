package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.model.models.CondensationData;
import com.ftn.sbnz.model.models.MoistureInvestigation;
import com.ftn.sbnz.model.models.Finding;
import com.ftn.sbnz.model.events.HumidityEvent;
import com.ftn.sbnz.model.dto.EnvironmentalAnalysisResult;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class EnvironmentalMonitoringService {

  private final KieContainer kieContainer;
  private final NotificationService notificationService;
  private final InvestigationService investigationService;
  private final FindingsService findingsService;
  private KieSession cepSession;

  @Autowired
  public EnvironmentalMonitoringService(KieContainer kieContainer, NotificationService notificationService,
      InvestigationService investigationService, FindingsService findingsService) {
    this.kieContainer = kieContainer;
    this.notificationService = notificationService;
    this.investigationService = investigationService;
    this.findingsService = findingsService;
  }

  @PostConstruct
  public void init() {
    // Use single CEP session for all modules - eventProcessingMode="stream"
    this.cepSession = kieContainer.newKieSession("environmental-session");
    this.cepSession.setGlobal("notificationService", notificationService);
    this.cepSession.setGlobal("investigationService", investigationService);
    this.cepSession.setGlobal("findingsService", findingsService);
    System.out.println("EnvironmentalMonitoringService: Initialized single CEP session");
  }

  @PreDestroy
  public void destroy() {
    if (cepSession != null) {
      cepSession.dispose();
      System.out.println("EnvironmentalMonitoringService: CEP session disposed");
    }
  }

  public synchronized EnvironmentalAnalysisResult processEnvironmentalData(List<Environment> environments,
      List<CondensationData> condensationDataList, List<HumidityEvent> humidityEvents) {
    List<Finding> allFindings = new ArrayList<>();
    MoistureInvestigation investigation = null;

    // Clean up expired findings for all modules before processing
    findingsService.cleanupAllExpiredFindings();

    // Insert all facts for all modules into the single CEP session
    environments.forEach(cepSession::insert);
    condensationDataList.forEach(cepSession::insert);
    humidityEvents.forEach(cepSession::insert);

    // Fire CEP rules for all modules in the single session
    int rulesFired = cepSession.fireAllRules();
    System.out.println("Environmental CEP: Fired " + rulesFired + " rules across all modules");

    // AFTER rules fired, check for "Investigation Required" findings and handle
    // investigation
    Collection<?> findingObjects = cepSession.getObjects(o -> o instanceof Finding);
    for (Object obj : findingObjects) {
      Finding finding = (Finding) obj;
      if (!finding.isExpired()) {
        allFindings.add(finding);

        // Check if this is an investigation request finding
        if ("Investigation Required".equals(finding.getType()) && !investigationService.hasActiveInvestigation()) {
          // Use moduleId directly from Finding object
          String moduleId = finding.getModuleId();

          // Start investigation for the module
          investigationService.startInvestigation(moduleId);

          // Collect ALL updated condensation data from this session for ALL modules
          Collection<?> allCondensationObjects = cepSession.getObjects(o -> o instanceof CondensationData);
          List<CondensationData> allCondensation = new ArrayList<>();
          for (Object condensationObj : allCondensationObjects) {
            allCondensation.add((CondensationData) condensationObj);
          }

          // Collect ALL environment data for ALL modules
          Collection<?> allEnvironmentObjects = cepSession.getObjects(o -> o instanceof Environment);
          List<Environment> allEnvironments = new ArrayList<>();
          for (Object envObj : allEnvironmentObjects) {
            allEnvironments.add((Environment) envObj);
          }

          // Transfer complete current state to investigation session
          investigationService.insertFactsForInvestigation(allCondensation.toArray());
          investigationService.insertFactsForInvestigation(allEnvironments.toArray());

          // Now fire investigation rules after all facts are inserted
          investigationService.processInvestigation();

          System.out.println("INVESTIGATION STARTED: Based on finding for module " + moduleId +
              " - Transferred " + allCondensation.size() + " condensation points and " +
              allEnvironments.size() + " environments");
        }
      }
    }

    // Get current investigation status
    investigation = investigationService.getCurrentInvestigation();

    return new EnvironmentalAnalysisResult(investigation, allFindings, rulesFired);
  }

  public List<CondensationData> getCondensationData(String moduleId) {
    if (cepSession == null) {
      return new ArrayList<>();
    }

    List<CondensationData> condensationData = new ArrayList<>();
    Collection<?> objects = cepSession.getObjects(o -> o instanceof CondensationData &&
        ((CondensationData) o).getModuleID().equals(moduleId));
    for (Object obj : objects) {
      condensationData.add((CondensationData) obj);
    }

    return condensationData;
  }

  public void resetSession() {
    if (cepSession != null) {
      cepSession.dispose();
      cepSession = kieContainer.newKieSession("environmental-session");
      cepSession.setGlobal("notificationService", notificationService);
      cepSession.setGlobal("investigationService", investigationService);
      cepSession.setGlobal("findingsService", findingsService);
      System.out.println("EnvironmentalMonitoringService: CEP session reset");
    }
  }
}