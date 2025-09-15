package com.ftn.sbnz.model.dtos;

import com.ftn.sbnz.model.models.*;

public class HypoxiaRequest {
  private Environment environment;
  private Vitals vitals;
  private CrewSymptoms crewSymptoms;
  private VentilationStatus ventilationStatus;

  public HypoxiaRequest() {
  }

  public HypoxiaRequest(Environment environment, Vitals vitals,
      CrewSymptoms crewSymptoms, VentilationStatus ventilationStatus) {
    this.environment = environment;
    this.vitals = vitals;
    this.crewSymptoms = crewSymptoms;
    this.ventilationStatus = ventilationStatus;
  }

  public Environment getEnvironment() {
    return environment;
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public Vitals getVitals() {
    return vitals;
  }

  public void setVitals(Vitals vitals) {
    this.vitals = vitals;
  }

  public CrewSymptoms getCrewSymptoms() {
    return crewSymptoms;
  }

  public void setCrewSymptoms(CrewSymptoms crewSymptoms) {
    this.crewSymptoms = crewSymptoms;
  }

  public VentilationStatus getVentilationStatus() {
    return ventilationStatus;
  }

  public void setVentilationStatus(VentilationStatus ventilationStatus) {
    this.ventilationStatus = ventilationStatus;
  }
}
