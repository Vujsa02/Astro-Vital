package com.ftn.sbnz.model.models;

public class VentilationStatus {
  private boolean degraded;

  public VentilationStatus() {
  }

  public VentilationStatus(boolean degraded) {
    this.degraded = degraded;
  }

  public boolean isDegraded() {
    return degraded;
  }

  public void setDegraded(boolean degraded) {
    this.degraded = degraded;
  }
}