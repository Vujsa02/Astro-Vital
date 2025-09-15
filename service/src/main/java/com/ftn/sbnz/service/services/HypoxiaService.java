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
public class HypoxiaService {

  private final KieContainer kieContainer;

  @Autowired
  public HypoxiaService(KieContainer kieContainer) {
    this.kieContainer = kieContainer;
  }

  public List<Finding> checkHypoxia(Environment env, Vitals vitals,
      CrewSymptoms symptoms, VentilationStatus ventilation) {
    KieSession kieSession = kieContainer.newKieSession();

    // Collect Findings into a list so we can return them
    List<Finding> findings = new ArrayList<>();
    kieSession.setGlobal("findings", findings);

    // Insert facts
    kieSession.insert(env);
    kieSession.insert(vitals);
    kieSession.insert(symptoms);
    kieSession.insert(ventilation);

    kieSession.fireAllRules();

    // Gather findings inserted by rules
    Collection<?> inserted = kieSession.getObjects(o -> o instanceof Finding);
    for (Object obj : inserted) {
      findings.add((Finding) obj);
    }

    kieSession.dispose();
    return findings;
  }
}