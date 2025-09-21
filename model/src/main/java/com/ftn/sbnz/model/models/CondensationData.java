package com.ftn.sbnz.model.models;

import com.ftn.sbnz.model.enums.CondensationLocation;

public class CondensationData {
    private String moduleID;
    private boolean condensationActive;
    private CondensationLocation location;
    private double surfaceTemperature;

    public CondensationData() {
    }

    public CondensationData(String moduleID, String location, double surfaceTemperature) {
        this.moduleID = moduleID;
        this.condensationActive = false;
        this.location = CondensationLocation.fromString(location);
        this.surfaceTemperature = surfaceTemperature;
    }

    public CondensationData(String moduleID, CondensationLocation location,
            double surfaceTemperature) {
        this.moduleID = moduleID;
        this.condensationActive = false;
        this.location = location;
        this.surfaceTemperature = surfaceTemperature;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }

    public boolean isCondensationActive() {
        return condensationActive;
    }

    public void setCondensationActive(boolean condensationActive) {
        this.condensationActive = condensationActive;
    }

    public String getLocation() {
        return location != null ? location.getValue() : null;
    }

    public CondensationLocation getLocationEnum() {
        return location;
    }

    public void setLocation(String location) {
        this.location = CondensationLocation.fromString(location);
    }

    public void setLocation(CondensationLocation location) {
        this.location = location;
    }

    public double getSurfaceTemperature() {
        return surfaceTemperature;
    }

    public void setSurfaceTemperature(double surfaceTemperature) {
        this.surfaceTemperature = surfaceTemperature;
    }

    @Override
    public String toString() {
        return "CondensationData{" +
                "moduleID='" + moduleID + '\'' +
                ", condensationActive=" + condensationActive +
                ", location='" + location + '\'' +
                ", surfaceTemperature=" + surfaceTemperature +
                '}';
    }
}