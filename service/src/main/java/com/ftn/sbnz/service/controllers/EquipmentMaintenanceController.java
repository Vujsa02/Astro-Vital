package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.dtos.*;
import com.ftn.sbnz.model.models.Finding;
import com.ftn.sbnz.service.services.EquipmentMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment-maintenance")
public class EquipmentMaintenanceController {

  private final EquipmentMaintenanceService equipmentMaintenanceService;

  @Autowired
  public EquipmentMaintenanceController(EquipmentMaintenanceService equipmentMaintenanceService) {
    this.equipmentMaintenanceService = equipmentMaintenanceService;
  }

  @PostMapping("/check")
  public List<Finding> checkMaintenanceNeeds(@RequestBody EquipmentMaintenanceRequest request) {
    return equipmentMaintenanceService.checkMaintenanceNeeds(
        request.getEnvironment(),
        request.getVentilationStatus(),
        request.getAirFilter());
  }
}