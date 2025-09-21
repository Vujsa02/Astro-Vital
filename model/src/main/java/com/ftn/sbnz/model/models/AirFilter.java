package com.ftn.sbnz.model.models;

public class AirFilter {
  private boolean dirty;
  private double efficiency;
  private String moduleID;

  public AirFilter() {
  }

  public AirFilter(String moduleID, boolean dirty, double efficiency) {
    this.moduleID = moduleID;
    this.dirty = dirty;
    this.efficiency = efficiency;
  }

  public boolean isDirty() {
    return dirty;
  }

  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  public double getEfficiency() {
    return efficiency;
  }

  public void setEfficiency(double efficiency) {
    this.efficiency = efficiency;
  }

  public String getModuleID() {
    return moduleID;
  }

  public void setModuleID(String moduleID) {
    this.moduleID = moduleID;
  }
}