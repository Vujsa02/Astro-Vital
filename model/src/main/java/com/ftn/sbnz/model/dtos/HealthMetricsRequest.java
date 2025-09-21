package com.ftn.sbnz.model.dtos;

import com.ftn.sbnz.model.models.*;
import java.util.List;

public class HealthMetricsRequest {
  private List<Environment> environments;
  private List<Vitals> vitalsList;
  private List<CrewSymptoms> crewSymptomsList;
  private List<VentilationStatus> ventilationStatusList;

  public HealthMetricsRequest() {
  }

  public HealthMetricsRequest(List<Environment> environments, List<Vitals> vitalsList,
      List<CrewSymptoms> crewSymptomsList, List<VentilationStatus> ventilationStatusList) {
    this.environments = environments;
    this.vitalsList = vitalsList;
    this.crewSymptomsList = crewSymptomsList;
    this.ventilationStatusList = ventilationStatusList;
  }

  public List<Environment> getEnvironments() {
    return environments;
  }

  public void setEnvironments(List<Environment> environments) {
    this.environments = environments;
  }

  public List<Vitals> getVitalsList() {
    return vitalsList;
  }

  public void setVitalsList(List<Vitals> vitalsList) {
    this.vitalsList = vitalsList;
  }

  public List<CrewSymptoms> getCrewSymptomsList() {
    return crewSymptomsList;
  }

  public void setCrewSymptomsList(List<CrewSymptoms> crewSymptomsList) {
    this.crewSymptomsList = crewSymptomsList;
  }

  public List<VentilationStatus> getVentilationStatusList() {
    return ventilationStatusList;
  }

  public void setVentilationStatusList(List<VentilationStatus> ventilationStatusList) {
    this.ventilationStatusList = ventilationStatusList;
  }
}