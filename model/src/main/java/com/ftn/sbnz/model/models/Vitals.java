package com.ftn.sbnz.model.models;

public class Vitals {
  private double spo2;
  private String crewMemberID;
  private double heartRate;
  private double bloodPressureSystolic;
  private double bloodPressureDiastolic;
  private double respiratoryRate;
  private double bodyTemperature;

  public Vitals() {
  }

  public Vitals(double spo2) {
    this.spo2 = spo2;
  }

  public Vitals(String crewMemberID, double spo2, double heartRate,
      double bloodPressureSystolic, double bloodPressureDiastolic,
      double respiratoryRate, double bodyTemperature) {
    this.crewMemberID = crewMemberID;
    this.spo2 = spo2;
    this.heartRate = heartRate;
    this.bloodPressureSystolic = bloodPressureSystolic;
    this.bloodPressureDiastolic = bloodPressureDiastolic;
    this.respiratoryRate = respiratoryRate;
    this.bodyTemperature = bodyTemperature;
  }

  public double getSpo2() {
    return spo2;
  }

  public void setSpo2(double spo2) {
    this.spo2 = spo2;
  }

  public String getCrewMemberID() {
    return crewMemberID;
  }

  public void setCrewMemberID(String crewMemberID) {
    this.crewMemberID = crewMemberID;
  }

  public double getHeartRate() {
    return heartRate;
  }

  public void setHeartRate(double heartRate) {
    this.heartRate = heartRate;
  }

  public double getBloodPressureSystolic() {
    return bloodPressureSystolic;
  }

  public void setBloodPressureSystolic(double bloodPressureSystolic) {
    this.bloodPressureSystolic = bloodPressureSystolic;
  }

  public double getBloodPressureDiastolic() {
    return bloodPressureDiastolic;
  }

  public void setBloodPressureDiastolic(double bloodPressureDiastolic) {
    this.bloodPressureDiastolic = bloodPressureDiastolic;
  }

  public double getRespiratoryRate() {
    return respiratoryRate;
  }

  public void setRespiratoryRate(double respiratoryRate) {
    this.respiratoryRate = respiratoryRate;
  }

  public double getBodyTemperature() {
    return bodyTemperature;
  }

  public void setBodyTemperature(double bodyTemperature) {
    this.bodyTemperature = bodyTemperature;
  }
}
