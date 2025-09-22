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
  private final FindingsService findingsService;

  @Autowired
  public EquipmentMaintenanceService(KieContainer kieContainer, FindingsService findingsService) {
    this.kieContainer = kieContainer;
    this.findingsService = findingsService;
  }

  public synchronized List<Finding> checkMaintenanceNeeds(Environment environment,
      VentilationStatus ventilationStatus, AirFilter airFilter) {

    // Clean up expired findings from global store first
    findingsService.cleanupAllExpiredFindings();

    KieSession kieSession = kieContainer.newKieSession();

    // Set the global findings service so rules can persist findings
    kieSession.setGlobal("findingsService", findingsService);

    // Insert provided facts (null-safe)
    if (environment != null)
      kieSession.insert(environment);
    if (ventilationStatus != null)
      kieSession.insert(ventilationStatus);
    if (airFilter != null)
      kieSession.insert(airFilter);

    // Run pipeline by agenda groups: detect -> persist
    kieSession.getAgenda().getAgendaGroup("detect.equipment").setFocus();
    kieSession.fireAllRules();

    // Print findings from memory
    List<Finding> findings = new ArrayList<>();
    Collection<?> objects = kieSession.getObjects(o -> o instanceof Finding && !((Finding) o).isExpired());
    for (Object obj : objects) {
      Finding f = (Finding) obj;
      findings.add(f);
      System.out.println(
          "Equipment Finding: " + f.getType() + " | module=" + f.getModuleId() + " | priority=" + f.getPriority());
    }

    // Persist actions
    kieSession.getAgenda().getAgendaGroup("persist.actions").setFocus();
    kieSession.fireAllRules();

    kieSession.dispose();
    return findings;
  }
}