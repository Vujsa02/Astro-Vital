package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.models.*;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class HealthMetricsService {

  private final KieContainer kieContainer;
  private final FindingsService findingsService;

  @Autowired
  public HealthMetricsService(KieContainer kieContainer, FindingsService findingsService) {
    this.kieContainer = kieContainer;
    this.findingsService = findingsService;
  }

  public synchronized List<Finding> checkHealthMetrics(Environment environment, Vitals vitals,
      CrewSymptoms symptoms, VentilationStatus ventilationStatus) {
    KieSession kieSession = kieContainer.newKieSession();

    // Clean up expired findings for all modules before processing
    findingsService.cleanupAllExpiredFindings();

    // Set globals
    kieSession.setGlobal("findingsService", findingsService);

    // Insert provided facts (null-safe)
    if (environment != null)
      kieSession.insert(environment);
    if (vitals != null)
      kieSession.insert(vitals);
    if (symptoms != null)
      kieSession.insert(symptoms);
    if (ventilationStatus != null)
      kieSession.insert(ventilationStatus);

    // Run pipeline by agenda groups: detect -> diagnose -> persist
    kieSession.getAgenda().getAgendaGroup("detect.health").setFocus();
    kieSession.fireAllRules();

    kieSession.getAgenda().getAgendaGroup("diagnose.health").setFocus();
    kieSession.fireAllRules();

    // Print findings from memory (for debugging/temporary simple output)
    List<Finding> findings = new ArrayList<>();
    Collection<?> inserted = kieSession.getObjects(o -> o instanceof Finding);
    for (Object obj : inserted) {
      Finding f = (Finding) obj;
      findings.add(f);
      System.out.println("Found: " + f.getType() + " | module=" + f.getModuleId() + " | priority=" + f.getPriority()
          + " | details=" + f.getDetails());
    }

    // Persist actions
    kieSession.getAgenda().getAgendaGroup("persist.actions").setFocus();
    kieSession.fireAllRules();

    kieSession.dispose();
    return findings;
  }
}