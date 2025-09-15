package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.models.*;
import com.ftn.sbnz.model.dtos.*;
import com.ftn.sbnz.service.services.HypoxiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hypoxia")
public class HypoxiaController {

  private final HypoxiaService hypoxiaService;

  @Autowired
  public HypoxiaController(HypoxiaService hypoxiaService) {
    this.hypoxiaService = hypoxiaService;
  }

  @PostMapping("/check")
  public List<Finding> checkHypoxia(@RequestBody HypoxiaRequest request) {
    return hypoxiaService.checkHypoxia(
        request.getEnvironment(),
        request.getVitals(),
        request.getCrewSymptoms(),
        request.getVentilationStatus());
  }
}