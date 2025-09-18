package com.ftn.sbnz.model.dtos;

import com.ftn.sbnz.model.models.*;

public class EquipmentMaintenanceRequest {
  private Environment environment;
  private VentilationStatus ventilationStatus;
  private AirFilter airFilter;

  public EquipmentMaintenanceRequest() {
  }

  public EquipmentMaintenanceRequest(Environment environment, VentilationStatus ventilationStatus,
      AirFilter airFilter) {
    this.environment = environment;
    this.ventilationStatus = ventilationStatus;
    this.airFilter = airFilter;
  }

  public Environment getEnvironment() {
    return environment;
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public VentilationStatus getVentilationStatus() {
    return ventilationStatus;
  }

  public void setVentilationStatus(VentilationStatus ventilationStatus) {
    this.ventilationStatus = ventilationStatus;
  }

  public AirFilter getAirFilter() {
    return airFilter;
  }

  public void setAirFilter(AirFilter airFilter) {
    this.airFilter = airFilter;
  }
}