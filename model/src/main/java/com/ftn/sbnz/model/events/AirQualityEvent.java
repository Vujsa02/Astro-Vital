package com.ftn.sbnz.model.events;

import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.api.definition.type.Expires;

@Role(Role.Type.EVENT)
@Timestamp("timestamp") // property name must exist on the class
@Expires("25h") // Keep events slightly longer than 24h window
public class AirQualityEvent {

    private long timestamp;
    private double vocLevel; // Volatile Organic Compounds (ppm)
    private double pmLevel; // Particulate Matter (μg/m³)
    private String moduleId;

    public AirQualityEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public AirQualityEvent(double vocLevel, double pmLevel, String moduleId) {
        this.vocLevel = vocLevel;
        this.pmLevel = pmLevel;
        this.moduleId = moduleId;
        this.timestamp = System.currentTimeMillis();
    }

    public AirQualityEvent(double vocLevel, double pmLevel, String moduleId, long timestamp) {
        this.vocLevel = vocLevel;
        this.pmLevel = pmLevel;
        this.moduleId = moduleId;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getVocLevel() {
        return vocLevel;
    }

    public void setVocLevel(double vocLevel) {
        this.vocLevel = vocLevel;
    }

    public double getPmLevel() {
        return pmLevel;
    }

    public void setPmLevel(double pmLevel) {
        this.pmLevel = pmLevel;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public String toString() {
        return "AirQualityEvent{" +
                "timestamp=" + timestamp +
                ", vocLevel=" + vocLevel +
                ", pmLevel=" + pmLevel +
                ", moduleId='" + moduleId + '\'' +
                '}';
    }
}
