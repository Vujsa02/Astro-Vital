package com.ftn.sbnz.model.models;

public class Finding {
  private String type;

  public Finding() {
  }

  public Finding(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "Finding{" + type + "}";
  }
}
