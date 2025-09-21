package com.ftn.sbnz.model.dtos;

import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.model.models.CondensationData;
import com.ftn.sbnz.model.events.HumidityEvent;
import java.util.List;

public class EnvironmentalMonitoringRequest {
  private List<Environment> environments;
  private List<CondensationData> condensationDataList;
  private List<HumidityEvent> humidityEvents;

  public EnvironmentalMonitoringRequest() {
  }

  public EnvironmentalMonitoringRequest(List<Environment> environments, List<CondensationData> condensationDataList,
      List<HumidityEvent> humidityEvents) {
    this.environments = environments;
    this.condensationDataList = condensationDataList;
    this.humidityEvents = humidityEvents;
  }

  public List<Environment> getEnvironments() {
    return environments;
  }

  public void setEnvironments(List<Environment> environments) {
    this.environments = environments;
  }

  public List<CondensationData> getCondensationDataList() {
    return condensationDataList;
  }

  public void setCondensationDataList(List<CondensationData> condensationDataList) {
    this.condensationDataList = condensationDataList;
  }

  public List<HumidityEvent> getHumidityEvents() {
    return humidityEvents;
  }

  public void setHumidityEvents(List<HumidityEvent> humidityEvents) {
    this.humidityEvents = humidityEvents;
  }
}