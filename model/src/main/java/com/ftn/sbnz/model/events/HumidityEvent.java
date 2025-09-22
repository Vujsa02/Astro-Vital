package com.ftn.sbnz.model.events;

import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.api.definition.type.Expires;

@Role(Role.Type.EVENT)
@Timestamp("timestamp") // property name must exist on the class
@Expires("7h")
public class HumidityEvent {

  private long timestamp;
  private double humidity;
  private String moduleId;

  public HumidityEvent() {
    this.timestamp = System.currentTimeMillis();
  }

  public HumidityEvent(double humidity, String moduleId) {
    this.humidity = humidity;
    this.moduleId = moduleId;
    this.timestamp = System.currentTimeMillis();
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public double getHumidity() {
    return humidity;
  }

  public void setHumidity(double humidity) {
    this.humidity = humidity;
  }

  public String getModuleId() {
    return moduleId;
  }

  public void setModuleId(String moduleId) {
    this.moduleId = moduleId;
  }
}