package com.ftn.sbnz.model.models;

public class AirFilter {
  private boolean dirty;
  private double efficiency;

  public AirFilter() {
  }

  public AirFilter(boolean dirty, double efficiency) {
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
}