package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.models.*;
import com.ftn.sbnz.model.dtos.*;
import com.ftn.sbnz.service.services.HealthMetricsService;
import com.ftn.sbnz.service.services.FindingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health-metrics")
public class HealthMetricsController {

  private final HealthMetricsService healthMetricsService;
  private final FindingsService findingsService;

  @Autowired
  public HealthMetricsController(HealthMetricsService healthMetricsService, FindingsService findingsService) {
    this.healthMetricsService = healthMetricsService;
    this.findingsService = findingsService;
  }

  @PostMapping("/check")
  public List<Finding> checkHealthMetrics(@RequestBody HealthMetricsRequest request) {
    return healthMetricsService.checkHealthMetrics(
        request.getEnvironment(),
        request.getVitals(),
        request.getCrewSymptoms(),
        request.getVentilationStatus());
  }

  @GetMapping("/findings")
  public Map<String, List<Finding>> getAllFindings() {
    return findingsService.getAllFindings();
  }

  @GetMapping("/findings/{moduleId}")
  public List<Finding> getModuleFindings(@PathVariable String moduleId) {
    return findingsService.getFindings(moduleId);
  }
}