package com.ftn.sbnz.service.services;

import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.ObjectDataCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.model.models.EnvironmentalThresholdTemplateModel;
import com.ftn.sbnz.model.models.Finding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

@Service
public class EnvironmentalThresholdTemplateService {

    private final FindingsService findingsService;

    @Autowired
    public EnvironmentalThresholdTemplateService(FindingsService findingsService) {
        this.findingsService = findingsService;
    }

    /**
     * Generates DRL rules from environmental threshold template using CSV data.
     * 
     * @return Generated DRL string
     */
    public String generateDrlFromCsv() {
        try {
            InputStream template = this.getClass()
                    .getResourceAsStream("/templates/environmental-thresholds.drt");

            if (template == null) {
                throw new RuntimeException("Template file not found: /templates/environmental-thresholds.drt");
            }

            // Read CSV data and convert to array format
            List<String[]> csvData = readCsvData();
            String[][] arrayData = csvData.toArray(new String[csvData.size()][]);

            DataProvider dataProvider = new ArrayDataProvider(arrayData);
            DataProviderCompiler converter = new DataProviderCompiler();

            return converter.compile(dataProvider, template);

        } catch (Exception e) {
            throw new RuntimeException("Error generating DRL from template", e);
        }
    }

    /**
     * Generates DRL rules from environmental threshold template using object data.
     * 
     * @param thresholds List of threshold configurations
     * @return Generated DRL string
     */
    public String generateDrlFromObjects(List<EnvironmentalThresholdTemplateModel> thresholds) {
        try {
            InputStream template = this.getClass()
                    .getResourceAsStream("/templates/environmental-thresholds.drt");

            if (template == null) {
                throw new RuntimeException("Template file not found: /templates/environmental-thresholds.drt");
            }

            ObjectDataCompiler converter = new ObjectDataCompiler();
            return converter.compile(thresholds, template);

        } catch (Exception e) {
            throw new RuntimeException("Error generating DRL from template", e);
        }
    }

    /**
     * Creates a KieSession from generated DRL.
     * 
     * @param drl Generated DRL string
     * @return KieSession ready for rule execution
     */
    public KieSession createKieSessionFromDrl(String drl) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);

        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            StringBuilder errorMsg = new StringBuilder("DRL compilation errors:\n");
            for (Message message : messages) {
                errorMsg.append("- ").append(message.getText()).append("\n");
            }
            throw new IllegalStateException(errorMsg.toString());
        }

        return kieHelper.build().newKieSession();
    }

    /**
     * Evaluates environment against threshold rules.
     * 
     * @param environments List of environment data to evaluate
     * @return List of findings (threshold violations)
     */
    public List<Finding> evaluateEnvironmentalThresholds(List<Environment> environments) {
        String drl = generateDrlFromCsv();
        KieSession kieSession = createKieSessionFromDrl(drl);

        // Insert all environments
        for (Environment env : environments) {
            kieSession.insert(env);
        }

        // Fire rules
        kieSession.fireAllRules();

        // Collect findings
        List<Finding> findings = new ArrayList<>();
        for (Object obj : kieSession.getObjects()) {
            if (obj instanceof Finding) {
                findings.add((Finding) obj);
            }
        }

        kieSession.dispose();
        // Persist findings into FindingsService if not already present (dedupe)
        try {
            for (Finding f : findings) {
                try {
                    if (!findingsService.hasActiveFinding(f.getModuleId(), f.getType())) {
                        findingsService.addFindings(f.getModuleId(), Collections.singletonList(f));
                    }
                } catch (Exception ex) {
                    System.out.println(
                            "EnvironmentalThresholdTemplateService: Failed to persist finding: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(
                    "EnvironmentalThresholdTemplateService: Error while persisting findings: " + e.getMessage());
        }

        return findings;
    }

    /**
     * Evaluates environment against custom threshold rules.
     * 
     * @param environments List of environment data to evaluate
     * @param thresholds   Custom threshold configurations
     * @return List of findings (threshold violations)
     */
    public List<Finding> evaluateCustomThresholds(List<Environment> environments,
            List<EnvironmentalThresholdTemplateModel> thresholds) {
        String drl = generateDrlFromObjects(thresholds);
        KieSession kieSession = createKieSessionFromDrl(drl);

        // Insert all environments
        for (Environment env : environments) {
            kieSession.insert(env);
        }

        // Fire rules
        kieSession.fireAllRules();

        // Collect findings
        List<Finding> findings = new ArrayList<>();
        for (Object obj : kieSession.getObjects()) {
            if (obj instanceof Finding) {
                findings.add((Finding) obj);
            }
        }

        kieSession.dispose();
        // Persist findings into FindingsService if not already present (dedupe)
        try {
            for (Finding f : findings) {
                try {
                    if (!findingsService.hasActiveFinding(f.getModuleId(), f.getType())) {
                        findingsService.addFindings(f.getModuleId(), Collections.singletonList(f));
                    }
                } catch (Exception ex) {
                    System.out.println(
                            "EnvironmentalThresholdTemplateService: Failed to persist finding: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(
                    "EnvironmentalThresholdTemplateService: Error while persisting findings: " + e.getMessage());
        }

        return findings;
    }

    /**
     * Reads CSV data from resources and converts to String array format.
     */
    private List<String[]> readCsvData() throws IOException {
        InputStream csvStream = this.getClass()
                .getResourceAsStream("/templates/data/environmental-thresholds.csv");

        if (csvStream == null) {
            throw new RuntimeException("CSV file not found: /templates/data/environmental-thresholds.csv");
        }

        List<String[]> csvData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip header line
                    continue;
                }

                // Simple CSV parsing (assumes no commas within quoted fields)
                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }
                csvData.add(values);
            }
        }

        return csvData;
    }

    /**
     * Gets default environmental threshold configurations for Astro-Vital.
     */
    public List<EnvironmentalThresholdTemplateModel> getDefaultAstroVitalThresholds() {
        List<EnvironmentalThresholdTemplateModel> thresholds = new ArrayList<>();

        try {
            List<String[]> rows = readCsvData();
            for (String[] cols : rows) {
                // Expecting CSV columns:
                // parameter,operator,threshold,duration,moduleId,alarmType,priority,description
                if (cols.length < 8)
                    continue;
                String parameter = cols[0];
                String operator = cols[1];
                String thresholdStr = cols[2];
                String duration = cols[3];
                String moduleId = cols[4];
                String alarmType = cols[5];
                String priority = cols[6];
                String description = cols[7];

                double thr = 0.0;
                try {
                    thr = Double.parseDouble(thresholdStr);
                } catch (NumberFormatException nfe) {
                    // keep as 0.0 if parse fails
                }

                thresholds.add(new EnvironmentalThresholdTemplateModel(
                        parameter, operator, thr, duration, moduleId, alarmType, priority, description));
            }
        } catch (IOException e) {
            // Fall back to hard-coded defaults if CSV cannot be read
            System.out.println("EnvironmentalThresholdTemplateService: Failed to read CSV for default thresholds: "
                    + e.getMessage());

            // LAB module thresholds
            thresholds.add(new EnvironmentalThresholdTemplateModel(
                    "o2Level", "<", 19.5, "2m", "LAB", "Low O2", "HIGH", "Oxygen level critically low"));
            thresholds.add(new EnvironmentalThresholdTemplateModel(
                    "co2Level", ">", 1000.0, "5m", "LAB", "High CO2", "HIGH", "Carbon dioxide level too high"));
            thresholds.add(new EnvironmentalThresholdTemplateModel(
                    "temperature", ">", 28.0, "10m", "LAB", "High Temperature", "MEDIUM",
                    "Temperature exceeds safe limits"));
            thresholds.add(new EnvironmentalThresholdTemplateModel(
                    "pressure", "<", 95.0, "30s", "LAB", "Low Pressure", "CRITICAL", "Pressure drop detected"));

            // HABITAT module thresholds
            thresholds.add(new EnvironmentalThresholdTemplateModel(
                    "o2Level", "<", 19.5, "2m", "HABITAT", "Low O2", "HIGH", "Oxygen level critically low in habitat"));
            thresholds.add(new EnvironmentalThresholdTemplateModel(
                    "vocLevel", ">", 50.0, "1h", "HABITAT", "High VOC", "MEDIUM",
                    "Volatile compounds elevated in habitat"));
            thresholds.add(new EnvironmentalThresholdTemplateModel(
                    "coLevel", ">", 10.0, "1m", "HABITAT", "CO Detected", "CRITICAL", "Carbon monoxide detected"));

            // COMMAND module thresholds
            thresholds.add(new EnvironmentalThresholdTemplateModel(
                    "pressure", "<", 98.0, "1m", "COMMAND", "Low Pressure", "HIGH", "Low pressure in command module"));
        }

        return thresholds;
    }
}