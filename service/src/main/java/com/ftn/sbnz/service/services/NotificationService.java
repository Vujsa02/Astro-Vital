package com.ftn.sbnz.service.services;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public void sendNotification(String type, String source, String details, String priority) {
    String timestamp = LocalDateTime.now().format(FORMATTER);
    String message = String.format("[%s] [%s] [%s] %s - %s",
        timestamp, priority, source, type, details);

    // For now, just print to console
    // Later you can extend this to send to message queues, databases, etc.
    System.out.println("NOTIFICATION: " + message);
  }

  public void sendNotification(String type, String source, String details) {
    sendNotification(type, source, details, "INFO");
  }
}