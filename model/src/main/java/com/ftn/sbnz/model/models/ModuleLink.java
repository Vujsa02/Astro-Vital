package com.ftn.sbnz.model.models;

import org.kie.api.definition.type.Position;

/**
 * Connectivity fact between modules used for recursive backward chaining.
 * Provides positional arguments so DRL can match by order: ModuleLink("CMD",
 * $next)
 */
public class ModuleLink {
  @Position(0)
  private String from;
  @Position(1)
  private String to;

  public ModuleLink() {
  }

  public ModuleLink(String from, String to) {
    this.from = from;
    this.to = to;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  @Override
  public String toString() {
    return "ModuleLink{" + from + "->" + to + "}";
  }
}
