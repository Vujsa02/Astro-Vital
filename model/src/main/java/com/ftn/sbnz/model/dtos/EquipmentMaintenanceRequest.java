package com.ftn.sbnz.model.dtos;

import com.ftn.sbnz.model.models.*;
import java.util.List;

public class EquipmentMaintenanceRequest {
  private List<Environment> environments;
  private List<VentilationStatus> ventilationStatusList;
  private List<AirFilter> airFilterList;

  public EquipmentMaintenanceRequest() {
  }

  public EquipmentMaintenanceRequest(List<Environment> environments, List<VentilationStatus> ventilationStatusList,
      List<AirFilter> airFilterList) {
    this.environments = environments;
    this.ventilationStatusList = ventilationStatusList;
    this.airFilterList = airFilterList;
  }

  public List<Environment> getEnvironments() {
    return environments;
  }

  public void setEnvironments(List<Environment> environments) {
    this.environments = environments;
  }

  public List<VentilationStatus> getVentilationStatusList() {
    return ventilationStatusList;
  }

  public void setVentilationStatusList(List<VentilationStatus> ventilationStatusList) {
    this.ventilationStatusList = ventilationStatusList;
  }

  public List<AirFilter> getAirFilterList() {
    return airFilterList;
  }

  public void setAirFilterList(List<AirFilter> airFilterList) {
    this.airFilterList = airFilterList;
  }
}