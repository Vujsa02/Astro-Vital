package com.ftn.sbnz.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.model.models.EnvironmentalThresholdTemplateModel;
import com.ftn.sbnz.model.models.Finding;
import com.ftn.sbnz.service.services.EnvironmentalThresholdTemplateService;

import java.util.List;

@RestController
@RequestMapping("/api/environmental-templates")
@CrossOrigin(origins = "http://localhost:3000")
public class EnvironmentalThresholdTemplateController {

    @Autowired
    private EnvironmentalThresholdTemplateService templateService;

    /**
     * Generate DRL rules from CSV template data.
     */
    @GetMapping("/generate-drl-csv")
    public ResponseEntity<String> generateDrlFromCsv() {
        try {
            String drl = templateService.generateDrlFromCsv();
            return ResponseEntity.ok(drl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error generating DRL: " + e.getMessage());
        }
    }

    /**
     * Generate DRL rules from custom threshold objects.
     */
    @PostMapping("/generate-drl-objects")
    public ResponseEntity<String> generateDrlFromObjects(
            @RequestBody List<EnvironmentalThresholdTemplateModel> thresholds) {
        try {
            String drl = templateService.generateDrlFromObjects(thresholds);
            return ResponseEntity.ok(drl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error generating DRL: " + e.getMessage());
        }
    }

    /**
     * Evaluate environments against default CSV-based threshold rules.
     */
    @PostMapping("/evaluate-default")
    public ResponseEntity<List<Finding>> evaluateWithDefaultThresholds(
            @RequestBody List<Environment> environments) {
        try {
            List<Finding> findings = templateService.evaluateEnvironmentalThresholds(environments);
            return ResponseEntity.ok(findings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Evaluate environments against custom threshold rules.
     */
    @PostMapping("/evaluate-custom")
    public ResponseEntity<List<Finding>> evaluateWithCustomThresholds(
            @RequestBody EvaluationRequest request) {
        try {
            List<Finding> findings = templateService.evaluateCustomThresholds(
                    request.getEnvironments(),
                    request.getThresholds());
            return ResponseEntity.ok(findings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get default Astro-Vital threshold configurations.
     */
    @GetMapping("/default-thresholds")
    public ResponseEntity<List<EnvironmentalThresholdTemplateModel>> getDefaultThresholds() {
        List<EnvironmentalThresholdTemplateModel> thresholds = templateService.getDefaultAstroVitalThresholds();
        return ResponseEntity.ok(thresholds);
    }

    /**
     * Create sample environment data for testing.
     */
    @GetMapping("/sample-environments")
    public ResponseEntity<List<Environment>> getSampleEnvironments() {
        List<Environment> environments = List.of(
                new Environment("LAB", 18.0, 1200, 5.0, 29.0, 45.0, 94.0, 25.0, 15.0), // Multiple violations
                new Environment("HABITAT", 20.5, 700, 15.0, 25.0, 50.0, 101.0, 75.0, 20.0), // High VOC + CO
                new Environment("COMMAND", 20.8, 600, 2.0, 24.0, 40.0, 97.0, 20.0, 12.0), // Low pressure
                new Environment("LAB", 20.5, 600, 2.0, 24.0, 45.0, 101.3, 20.0, 15.0) // Normal - no violations
        );
        return ResponseEntity.ok(environments);
    }

    /**
     * Request object for custom evaluation endpoint.
     */
    public static class EvaluationRequest {
        private List<Environment> environments;
        private List<EnvironmentalThresholdTemplateModel> thresholds;

        public List<Environment> getEnvironments() {
            return environments;
        }

        public void setEnvironments(List<Environment> environments) {
            this.environments = environments;
        }

        public List<EnvironmentalThresholdTemplateModel> getThresholds() {
            return thresholds;
        }

        public void setThresholds(List<EnvironmentalThresholdTemplateModel> thresholds) {
            this.thresholds = thresholds;
        }
    }
}