package com.ftn.sbnz.model.dtos;

import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.model.events.AirQualityEvent;
import java.util.List;

public class AirQualityMonitoringRequest {
    private List<Environment> environments;
    private List<AirQualityEvent> airQualityEvents;

    public AirQualityMonitoringRequest() {
    }

    public AirQualityMonitoringRequest(List<Environment> environments, List<AirQualityEvent> airQualityEvents) {
        this.environments = environments;
        this.airQualityEvents = airQualityEvents;
    }

    public List<Environment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }

    public List<AirQualityEvent> getAirQualityEvents() {
        return airQualityEvents;
    }

    public void setAirQualityEvents(List<AirQualityEvent> airQualityEvents) {
        this.airQualityEvents = airQualityEvents;
    }

    @Override
    public String toString() {
        return "AirQualityMonitoringRequest{" +
                "environments=" + environments +
                ", airQualityEvents=" + airQualityEvents +
                '}';
    }
}