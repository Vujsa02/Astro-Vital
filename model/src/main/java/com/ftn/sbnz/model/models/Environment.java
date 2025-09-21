package com.ftn.sbnz.model.models;

public class Environment {
  private double o2Level;
  private double co2Level;
  private double coLevel;
  private String moduleID;
  private double temperature;
  private double humidity;
  private double pressure;
  private double vocLevel;
  private double pmLevel;
  private double dewPoint;

  public Environment() {
  }

  public Environment(double o2Level, double co2Level) {
    this.o2Level = o2Level;
    this.co2Level = co2Level;
  }

  public Environment(double o2Level, double co2Level, double coLevel) {
    this.o2Level = o2Level;
    this.co2Level = co2Level;
    this.coLevel = coLevel;
  }

  public Environment(String moduleID, double o2Level, double co2Level, double coLevel,
      double temperature, double humidity, double pressure, double vocLevel, double pmLevel) {
    this.moduleID = moduleID;
    this.o2Level = o2Level;
    this.co2Level = co2Level;
    this.coLevel = coLevel;
    this.temperature = temperature;
    this.humidity = humidity;
    this.pressure = pressure;
    this.vocLevel = vocLevel;
    this.pmLevel = pmLevel;
    this.calculateDewPoint();
  }

  public double getO2Level() {
    return o2Level;
  }

  public void setO2Level(double o2Level) {
    this.o2Level = o2Level;
  }

  public double getCo2Level() {
    return co2Level;
  }

  public void setCo2Level(double co2Level) {
    this.co2Level = co2Level;
  }

  public double getCoLevel() {
    return coLevel;
  }

  public void setCoLevel(double coLevel) {
    this.coLevel = coLevel;
  }

  public String getModuleID() {
    return moduleID;
  }

  public void setModuleID(String moduleID) {
    this.moduleID = moduleID;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public double getHumidity() {
    return humidity;
  }

  public void setHumidity(double humidity) {
    this.humidity = humidity;
  }

  public double getPressure() {
    return pressure;
  }

  public void setPressure(double pressure) {
    this.pressure = pressure;
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

  public double getDewPoint() {
    return dewPoint;
  }

  public void setDewPoint(double dewPoint) {
    this.dewPoint = dewPoint;
  }

  public void calculateDewPoint() {
    // Simplified dew point calculation: Dew Point ≈ Temperature - ((100 - Humidity)
    // / 5)
    this.dewPoint = temperature - ((100 - humidity) / 5);
  }
}