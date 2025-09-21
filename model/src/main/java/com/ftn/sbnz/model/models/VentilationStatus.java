package com.ftn.sbnz.model.models;

public class VentilationStatus {
  private boolean degraded;
  private String moduleID;

  public VentilationStatus() {
  }

  public VentilationStatus(boolean degraded) {
    this.degraded = degraded;
  }

  public VentilationStatus(String moduleID, boolean degraded) {
    this.moduleID = moduleID;
    this.degraded = degraded;
  }

  public boolean isDegraded() {
    return degraded;
  }

  public void setDegraded(boolean degraded) {
    this.degraded = degraded;
  }

  public String getModuleID() {
    return moduleID;
  }

  public void setModuleID(String moduleID) {
    this.moduleID = moduleID;
  }
}