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
  private final FindingsService findingsService;
  private KieSession cepSession;

  @Autowired
  public EnvironmentalMonitoringService(KieContainer kieContainer,
      FindingsService findingsService) {
    this.kieContainer = kieContainer;
    this.findingsService = findingsService;
  }

  @PostConstruct
  public void init() {
    // Use single CEP session for all modules - eventProcessingMode="stream"
    this.cepSession = kieContainer.newKieSession("environmental-session");
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

    // Run CEP agenda group then investigation agenda in same session
    cepSession.getAgenda().getAgendaGroup("cep.environment").setFocus();
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
        if ("Investigation Required".equals(finding.getType())) {
          // Check if there's already an active investigation in the CEP session
          boolean hasActiveInvestigation = cepSession.getObjects()
              .stream()
              .anyMatch(o -> o instanceof MoistureInvestigation &&
                  !((MoistureInvestigation) o).isInvestigationComplete());

          if (!hasActiveInvestigation) {
            // Use moduleId directly from Finding object
            String moduleId = finding.getModuleId();
            // Start investigation for the module by inserting a MoistureInvestigation into
            // CEP session
            MoistureInvestigation newInvestigation = new MoistureInvestigation(moduleId);
            cepSession.insert(newInvestigation);

            // Run investigation agenda in the same CEP session with guarded loop
            cepSession.getAgenda().getAgendaGroup("investigate.moisture").setFocus();

            // Guarded loop to ensure rules re-evaluate after modify(...) actions
            int maxIterations = 20;
            int iter = 0;
            while (!newInvestigation.isInvestigationComplete() && iter < maxIterations) {
              int fired = cepSession.fireAllRules();
              if (fired == 0)
                break; // nothing new to do
              iter++;
            }

            if (!newInvestigation.isInvestigationComplete()) {
              System.out
                  .println("Investigation did not finish within " + maxIterations + " cycles for module " + moduleId);
            }

            System.out.println("INVESTIGATION STARTED (in CEP session): Based on finding for module " + moduleId);
          }
        }
      }
    }

    // Get current investigation status from CEP session (if one was inserted there)
    investigation = (MoistureInvestigation) cepSession.getObjects()
        .stream()
        .filter(o -> o instanceof MoistureInvestigation)
        .findFirst()
        .orElse(null);

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
      cepSession.setGlobal("findingsService", findingsService);
      System.out.println("EnvironmentalMonitoringService: CEP session reset");
    }
  }
}