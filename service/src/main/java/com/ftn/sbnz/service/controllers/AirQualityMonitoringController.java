package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.dto.AirQualityAnalysisResult;
import com.ftn.sbnz.model.dtos.AirQualityMonitoringRequest;
import com.ftn.sbnz.service.services.AirQualityMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/air-quality-monitoring")
public class AirQualityMonitoringController {

    private final AirQualityMonitoringService airQualityMonitoringService;

    @Autowired
    public AirQualityMonitoringController(AirQualityMonitoringService airQualityMonitoringService) {
        this.airQualityMonitoringService = airQualityMonitoringService;
    }

    @PostMapping("/process")
    public AirQualityAnalysisResult processAirQualityData(@RequestBody AirQualityMonitoringRequest request) {
        System.out.println("Received air quality monitoring request: " + request);
        return airQualityMonitoringService.processAirQualityData(
                request.getEnvironments(),
                request.getAirQualityEvents());
    }
}