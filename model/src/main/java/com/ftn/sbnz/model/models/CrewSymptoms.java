package com.ftn.sbnz.model.models;

public class CrewSymptoms {
  private boolean shortnessOfBreath;
  private boolean dizziness;

  public CrewSymptoms() {
  }

  public CrewSymptoms(boolean shortnessOfBreath, boolean dizziness) {
    this.shortnessOfBreath = shortnessOfBreath;
    this.dizziness = dizziness;
  }

  public boolean isShortnessOfBreath() {
    return shortnessOfBreath;
  }

  public void setShortnessOfBreath(boolean shortnessOfBreath) {
    this.shortnessOfBreath = shortnessOfBreath;
  }

  public boolean isDizziness() {
    return dizziness;
  }

  public void setDizziness(boolean dizziness) {
    this.dizziness = dizziness;
  }
}
