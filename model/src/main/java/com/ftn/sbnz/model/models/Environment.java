package com.ftn.sbnz.model.models;

public class Environment {
  private double o2Level;
  private double co2Level;
  private double coLevel;

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
}