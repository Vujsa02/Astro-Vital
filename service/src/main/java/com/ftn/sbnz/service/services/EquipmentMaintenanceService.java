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
public class EquipmentMaintenanceService {

  private final KieContainer kieContainer;
  private final NotificationService notificationService;
  private final FindingsService findingsService;

  @Autowired
  public EquipmentMaintenanceService(KieContainer kieContainer, NotificationService notificationService,
      FindingsService findingsService) {
    this.kieContainer = kieContainer;
    this.notificationService = notificationService;
    this.findingsService = findingsService;
  }

  public synchronized List<Finding> checkMaintenanceNeeds(List<Environment> environments,
      List<VentilationStatus> ventilationList,
      List<AirFilter> airFilterList) {

    // Clean up expired findings from global store first
    findingsService.cleanupAllExpiredFindings();

    KieSession kieSession = kieContainer.newKieSession("equipment-session");

    // Set the global notification service
    kieSession.setGlobal("notificationService", notificationService);

    // Set the global findings service so rules can persist findings
    kieSession.setGlobal("findingsService", findingsService);

    // Insert all facts
    environments.forEach(kieSession::insert);
    ventilationList.forEach(kieSession::insert);
    airFilterList.forEach(kieSession::insert);

    // Fire all rules
    int rulesFired = kieSession.fireAllRules();
    System.out.println("Equipment Maintenance: Fired " + rulesFired + " rules");

    // Collect findings
    List<Finding> findings = new ArrayList<>();
    Collection<?> objects = kieSession.getObjects(o -> o instanceof Finding && !((Finding) o).isExpired());
    for (Object obj : objects) {
      findings.add((Finding) obj);
    }

    kieSession.dispose();
    return findings;
  }
}