package com.ftn.sbnz.model.models;

import java.time.LocalDateTime;

public class Finding {
  private String type;
  private String moduleId;
  private String details;
  private String priority;
  private LocalDateTime timestamp;
  private LocalDateTime expiresAt;
  private boolean notified;

  public Finding() {
    this.timestamp = LocalDateTime.now();
    this.expiresAt = LocalDateTime.now().plusMinutes(30); // Default 30m expiration
    this.notified = false;
  }

  public Finding(String type) {
    this();
    this.type = type;
  }

  public Finding(String type, String details, String priority) {
    this();
    this.type = type;
    this.details = details;
    this.priority = priority;
  }

  public Finding(String type, String moduleId, String details, String priority) {
    this();
    this.type = type;
    this.moduleId = moduleId;
    this.details = details;
    this.priority = priority;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getModuleId() {
    return moduleId;
  }

  public void setModuleId(String moduleId) {
    this.moduleId = moduleId;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public boolean isNotified() {
    return notified;
  }

  public void setNotified(boolean notified) {
    this.notified = notified;
  }

  @Override
  public String toString() {
    return "Finding{" +
        "type='" + type + '\'' +
        ", moduleId='" + moduleId + '\'' +
        ", details='" + details + '\'' +
        ", priority='" + priority + '\'' +
        ", timestamp=" + timestamp +
        ", notified=" + notified +
        '}';
  }
}
