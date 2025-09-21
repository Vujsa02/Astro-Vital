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
  private final NotificationService notificationService;
  private final FindingsService findingsService;

  @Autowired
  public HealthMetricsService(KieContainer kieContainer, NotificationService notificationService,
      FindingsService findingsService) {
    this.kieContainer = kieContainer;
    this.notificationService = notificationService;
    this.findingsService = findingsService;
  }

  public synchronized List<Finding> checkHealthMetrics(List<Environment> environments, List<Vitals> vitalsList,
      List<CrewSymptoms> symptomsList, List<VentilationStatus> ventilationList) {
    KieSession kieSession = kieContainer.newKieSession("health-session");

    // Clean up expired findings for all modules before processing
    findingsService.cleanupAllExpiredFindings();

    // Set globals
    kieSession.setGlobal("notificationService", notificationService);
    kieSession.setGlobal("findingsService", findingsService);

    // Insert all environment facts
    environments.forEach(kieSession::insert);
    vitalsList.forEach(kieSession::insert);
    symptomsList.forEach(kieSession::insert);
    ventilationList.forEach(kieSession::insert);

    // Fire all rules (both hypoxia and chemical air quality)
    int rulesFired = kieSession.fireAllRules();
    System.out.println("Health Metrics: Fired " + rulesFired + " rules");

    // Gather findings inserted by rules
    List<Finding> findings = new ArrayList<>();
    Collection<?> inserted = kieSession.getObjects(o -> o instanceof Finding);
    for (Object obj : inserted) {
      findings.add((Finding) obj);
    }

    kieSession.dispose();
    return findings;
  }
}