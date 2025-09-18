package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.models.*;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EquipmentMaintenanceService {

  private final KieContainer kieContainer;

  @Autowired
  public EquipmentMaintenanceService(KieContainer kieContainer) {
    this.kieContainer = kieContainer;
  }

  public List<String> checkMaintenanceNeeds(Environment env, VentilationStatus ventilation, AirFilter airFilter) {
    KieSession kieSession = kieContainer.newKieSession();

    // Collect maintenance actions
    List<String> maintenanceActions = new ArrayList<>();

    // Insert facts for equipment maintenance rules
    kieSession.insert(env);
    kieSession.insert(ventilation);
    kieSession.insert(airFilter);

    // Fire equipment maintenance rules
    int rulesFired = kieSession.fireAllRules();
    System.out.println("Equipment Maintenance: Fired " + rulesFired + " rules");

    // Since equipment rules use System.out.println, we'll return a summary
    // In a real implementation, you might want to create specific maintenance
    // findings
    if (ventilation.isDegraded() && env.getCo2Level() > 1000) {
      maintenanceActions.add("Ventilation service required due to degraded ventilation and high CO2 levels");
    }

    if (airFilter.isDirty() && env.getCo2Level() > 1000) {
      maintenanceActions.add("Air filter replacement required due to dirty filter and high CO2 levels");
    }

    kieSession.dispose();
    return maintenanceActions;
  }
}