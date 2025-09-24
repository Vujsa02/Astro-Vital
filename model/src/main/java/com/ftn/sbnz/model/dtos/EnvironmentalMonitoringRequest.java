package com.ftn.sbnz.model.dtos;

import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.model.models.CondensationData;
import com.ftn.sbnz.model.events.HumidityEvent;
import com.ftn.sbnz.model.models.WaterRecycling;
import com.ftn.sbnz.model.models.VentilationStatus;
import java.util.List;

public class EnvironmentalMonitoringRequest {
  private List<Environment> environments;
  private List<CondensationData> condensationDataList;
  private List<HumidityEvent> humidityEvents;
  private List<WaterRecycling> waterRecyclings;
  private List<VentilationStatus> ventilationStatuses;

  public EnvironmentalMonitoringRequest() {
  }

  public EnvironmentalMonitoringRequest(List<Environment> environments, List<CondensationData> condensationDataList,
      List<HumidityEvent> humidityEvents, List<WaterRecycling> waterRecyclings,
      List<VentilationStatus> ventilationStatuses) {
    this.environments = environments;
    this.condensationDataList = condensationDataList;
    this.humidityEvents = humidityEvents;
    this.waterRecyclings = waterRecyclings;
    this.ventilationStatuses = ventilationStatuses;
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

  public List<WaterRecycling> getWaterRecyclings() {
    return waterRecyclings;
  }

  public void setWaterRecyclings(List<WaterRecycling> waterRecyclings) {
    this.waterRecyclings = waterRecyclings;
  }

  public List<VentilationStatus> getVentilationStatuses() {
    return ventilationStatuses;
  }

  public void setVentilationStatuses(List<VentilationStatus> ventilationStatuses) {
    this.ventilationStatuses = ventilationStatuses;
  }

  @Override
  public String toString() {
    return "EnvironmentalMonitoringRequest{" +
        "environments=" + environments +
        ", condensationDataList=" + condensationDataList +
        ", humidityEvents=" + humidityEvents +
        ", waterRecyclings=" + waterRecyclings +
        ", ventilationStatuses=" + ventilationStatuses +
        '}';
  }
}