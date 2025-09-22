package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.models.*;
import com.ftn.sbnz.model.dtos.*;
import com.ftn.sbnz.service.services.HealthMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health-metrics")
public class HealthMetricsController {

  private final HealthMetricsService healthMetricsService;

  @Autowired
  public HealthMetricsController(HealthMetricsService healthMetricsService) {
    this.healthMetricsService = healthMetricsService;
  }

  @PostMapping("/check")
  public List<Finding> checkHealthMetrics(@RequestBody HealthMetricsRequest request) {
    return healthMetricsService.checkHealthMetrics(
        request.getEnvironment(),
        request.getVitals(),
        request.getCrewSymptoms(),
        request.getVentilationStatus());
  }
}