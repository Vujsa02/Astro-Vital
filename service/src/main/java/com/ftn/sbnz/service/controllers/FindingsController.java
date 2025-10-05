package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.models.Finding;
import com.ftn.sbnz.service.services.FindingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/findings")
public class FindingsController {

  private final FindingsService findingsService;

  @Autowired
  public FindingsController(FindingsService findingsService) {
    this.findingsService = findingsService;
  }

  @GetMapping
  public Map<String, List<Finding>> getAllFindings() {
    return findingsService.getAllFindings();
  }

  @GetMapping("/{moduleId}")
  public List<Finding> getModuleFindings(@PathVariable String moduleId) {
    return findingsService.getFindings(moduleId);
  }

  @DeleteMapping("/single")
  public Map<String, Object> deleteFinding(@RequestParam String type, @RequestParam String moduleId) {
    boolean deleted = findingsService.deleteFindingsByTypeAndModule(type, moduleId);
    Map<String, Object> response = Map.of(
        "success", deleted,
        "message", deleted ? "Finding deleted successfully" : "Finding not found",
        "deletedType", type,
        "moduleId", moduleId);
    return response;
  }

  @DeleteMapping("/multiple")
  public Map<String, Object> deleteMultipleFindings(@RequestBody List<FindingsService.FindingIdentifier> identifiers) {
    int deletedCount = findingsService.deleteMultipleFindings(identifiers);
    Map<String, Object> response = Map.of(
        "success", deletedCount > 0,
        "message", deletedCount + " finding(s) deleted successfully",
        "deletedCount", deletedCount);
    return response;
  }

  @DeleteMapping("/{moduleId}")
  public Map<String, Object> clearModuleFindings(@PathVariable String moduleId) {
    findingsService.clearModuleFindings(moduleId);
    Map<String, Object> response = Map.of(
        "success", true,
        "message", "All findings cleared for module " + moduleId,
        "moduleId", moduleId);
    return response;
  }

  @DeleteMapping
  public Map<String, Object> clearAllFindings() {
    findingsService.clearAll();
    Map<String, Object> response = Map.of(
        "success", true,
        "message", "All findings cleared");
    return response;
  }
}
