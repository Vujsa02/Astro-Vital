package com.ftn.sbnz.model.models;

public class WaterRecycling {
    private String moduleID;
    private boolean degraded;
    private double efficiency;
    private boolean leakageDetected;

    public WaterRecycling() {
    }

    public WaterRecycling(String moduleID, boolean degraded, double efficiency) {
        this.moduleID = moduleID;
        this.degraded = degraded;
        this.efficiency = efficiency;
    }

    public WaterRecycling(String moduleID, boolean degraded, double efficiency,
            boolean leakageDetected) {
        this.moduleID = moduleID;
        this.degraded = degraded;
        this.efficiency = efficiency;
        this.leakageDetected = leakageDetected;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }

    public boolean isDegraded() {
        return degraded;
    }

    public void setDegraded(boolean degraded) {
        this.degraded = degraded;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    public boolean isLeakageDetected() {
        return leakageDetected;
    }

    public void setLeakageDetected(boolean leakageDetected) {
        this.leakageDetected = leakageDetected;
    }

    @Override
    public String toString() {
        return "WaterRecycling{" +
                "moduleID='" + moduleID + '\'' +
                ", degraded=" + degraded +
                ", efficiency=" + efficiency +
                ", leakageDetected=" + leakageDetected +
                '}';
    }
}