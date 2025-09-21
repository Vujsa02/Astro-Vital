package com.ftn.sbnz.model.models;

public class CrewSymptoms {
  private boolean shortnessOfBreath;
  private boolean dizziness;
  private boolean eyeIrritation;
  private String crewMemberID;
  private boolean cough;
  private boolean headache;
  private boolean fatigue;

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

  public CrewSymptoms(String crewMemberID, boolean shortnessOfBreath, boolean dizziness,
      boolean eyeIrritation, boolean cough, boolean headache, boolean fatigue) {
    this.crewMemberID = crewMemberID;
    this.shortnessOfBreath = shortnessOfBreath;
    this.dizziness = dizziness;
    this.eyeIrritation = eyeIrritation;
    this.cough = cough;
    this.headache = headache;
    this.fatigue = fatigue;
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

  public String getCrewMemberID() {
    return crewMemberID;
  }

  public void setCrewMemberID(String crewMemberID) {
    this.crewMemberID = crewMemberID;
  }

  public boolean isCough() {
    return cough;
  }

  public void setCough(boolean cough) {
    this.cough = cough;
  }

  public boolean isHeadache() {
    return headache;
  }

  public void setHeadache(boolean headache) {
    this.headache = headache;
  }

  public boolean isFatigue() {
    return fatigue;
  }

  public void setFatigue(boolean fatigue) {
    this.fatigue = fatigue;
  }
}
