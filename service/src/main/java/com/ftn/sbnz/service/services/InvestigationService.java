package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.models.MoistureInvestigation;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;

@Service
public class InvestigationService {

  private final KieContainer kieContainer;
  private KieSession investigationSession;

  public InvestigationService(KieContainer kieContainer) {
    this.kieContainer = kieContainer;
  }

  @PostConstruct
  public void init() {
    // Use unified session for investigation - allows cross-domain rules
    this.investigationSession = kieContainer.newKieSession("unified-session");
    System.out.println("InvestigationService: Initialized with unified-session");
  }

  @PreDestroy
  public void destroy() {
    if (investigationSession != null) {
      investigationSession.dispose();
      System.out.println("InvestigationService: Session disposed");
    }
  }

  public synchronized void startInvestigation(String moduleId) {
    Optional<MoistureInvestigation> existing = investigationSession.getObjects()
        .stream()
        .filter(o -> o instanceof MoistureInvestigation)
        .map(MoistureInvestigation.class::cast)
        .findFirst();

    if (existing.isEmpty()) {
      // No investigation exists, create new one
      MoistureInvestigation newInvestigation = new MoistureInvestigation(moduleId);
      investigationSession.insert(newInvestigation);
      System.out.println("InvestigationService: Started new investigation for module " + moduleId);
    } else {
      MoistureInvestigation investigation = existing.get();
      if (investigation.isInvestigationComplete()) {
        // Replace completed investigation with new one
        FactHandle oldHandle = investigationSession.getFactHandle(investigation);
        investigationSession.delete(oldHandle);

        MoistureInvestigation newInvestigation = new MoistureInvestigation(moduleId);
        investigationSession.insert(newInvestigation);
        System.out
            .println("InvestigationService: Replaced completed investigation with new one for module " + moduleId);
      }
    }

    System.out.println("InvestigationService: Investigation ready for module " + moduleId + " - waiting for facts");
  }

  public synchronized void insertFactsForInvestigation(Object... facts) {
    // Method to insert additional facts needed for investigation rules
    for (Object fact : facts) {
      investigationSession.insert(fact);
    }
    System.out.println("InvestigationService: Inserted " + facts.length + " facts for investigation");
  }

  public synchronized void processInvestigation() {
    // Fire rules after all facts have been inserted
    int rulesFired = investigationSession.fireAllRules();
    System.out.println("InvestigationService: Fired " + rulesFired + " investigation rules");
  }

  public synchronized MoistureInvestigation getCurrentInvestigation() {
    return (MoistureInvestigation) investigationSession.getObjects()
        .stream()
        .filter(o -> o instanceof MoistureInvestigation)
        .findFirst()
        .orElse(null);
  }

  public synchronized boolean hasActiveInvestigation() {
    return investigationSession.getObjects()
        .stream()
        .anyMatch(o -> o instanceof MoistureInvestigation && !((MoistureInvestigation) o).isInvestigationComplete());
  }

  public synchronized void resetInvestigation() {
    investigationSession.getObjects()
        .stream()
        .filter(o -> o instanceof MoistureInvestigation)
        .forEach(investigation -> {
          FactHandle handle = investigationSession.getFactHandle(investigation);
          investigationSession.delete(handle);
        });
    System.out.println("InvestigationService: Reset - all investigations cleared");
  }
}