package com.ftn.sbnz.model.models;

public class CrewSymptoms {
  private boolean shortnessOfBreath;
  private boolean dizziness;
  private boolean eyeIrritation;

  public CrewSymptoms() {
  }

  public CrewSymptoms(boolean shortnessOfBreath, boolean dizziness) {
    this.shortnessOfBreath = shortnessOfBreath;
    this.dizziness = dizziness;
  }

  public CrewSymptoms(boolean shortnessOfBreath, boolean dizziness, boolean eyeIrritation) {
    this.shortnessOfBreath = shortnessOfBreath;
    this.dizziness = dizziness;
    this.eyeIrritation = eyeIrritation;
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

  public boolean isEyeIrritation() {
    return eyeIrritation;
  }

  public void setEyeIrritation(boolean eyeIrritation) {
    this.eyeIrritation = eyeIrritation;
  }
}
