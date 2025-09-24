package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.models.CondensationData;
import com.ftn.sbnz.model.dto.EnvironmentalAnalysisResult;
import com.ftn.sbnz.model.dtos.EnvironmentalMonitoringRequest;
import com.ftn.sbnz.service.services.EnvironmentalMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/environmental-monitoring")
public class EnvironmentalMonitoringController {

  private final EnvironmentalMonitoringService environmentalMonitoringService;

  @Autowired
  public EnvironmentalMonitoringController(EnvironmentalMonitoringService environmentalMonitoringService) {
    this.environmentalMonitoringService = environmentalMonitoringService;
  }

  @PostMapping("/process")
  public EnvironmentalAnalysisResult processEnvironmentalData(@RequestBody EnvironmentalMonitoringRequest request) {
    System.out.println("Received health metrics check request: " + request);
    return environmentalMonitoringService.processEnvironmentalData(
        request.getEnvironments(),
        request.getCondensationDataList(),
        request.getHumidityEvents(),
        request.getWaterRecyclings(),
        request.getVentilationStatuses());
  }

  @GetMapping("/condensation/{moduleId}")
  public List<CondensationData> getCondensationData(@PathVariable String moduleId) {
    return environmentalMonitoringService.getCondensationData(moduleId);
  }
}