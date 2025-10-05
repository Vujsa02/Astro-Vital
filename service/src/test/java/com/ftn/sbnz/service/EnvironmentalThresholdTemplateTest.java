package com.ftn.sbnz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.ObjectDataCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.junit.jupiter.api.Test;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.model.models.EnvironmentalThresholdTemplateModel;
import com.ftn.sbnz.model.models.Finding;

public class EnvironmentalThresholdTemplateTest {

    /**
     * Tests environmental-thresholds.drt template by manually creating
     * the corresponding DRL using a bidimensional array of Strings
     * as the data source.
     */
    @Test
    public void testEnvironmentalThresholdTemplateWithArrays() {

        InputStream template = EnvironmentalThresholdTemplateTest.class
                .getResourceAsStream("/templates/environmental-thresholds.drt");

        DataProvider dataProvider = new ArrayDataProvider(new String[][] {
                new String[] { "o2Level", "<", "19.5", "2m", "LAB", "Low O2", "HIGH", "Oxygen level critically low" },
                new String[] { "co2Level", ">", "1000", "5m", "LAB", "High CO2", "HIGH",
                        "Carbon dioxide level too high" },
                new String[] { "temperature", ">", "28", "10m", "LAB", "High Temperature", "MEDIUM",
                        "Temperature exceeds safe limits" },
                new String[] { "pressure", "<", "95", "30s", "LAB", "Low Pressure", "CRITICAL",
                        "Pressure drop detected" },
        });

        DataProviderCompiler converter = new DataProviderCompiler();
        String drl = converter.compile(dataProvider, template);

        System.out.println("Generated DRL from Arrays:");
        System.out.println(drl);

        KieSession ksession = createKieSessionFromDRL(drl);

        doEnvironmentalTest(ksession);
    }

    /**
     * Tests environmental-thresholds.drt template by manually creating
     * the corresponding DRL using a collection of Objects as the data source.
     */
    @Test
    public void testEnvironmentalThresholdTemplateWithObjects() {

        InputStream template = EnvironmentalThresholdTemplateTest.class
                .getResourceAsStream("/templates/environmental-thresholds.drt");

        List<EnvironmentalThresholdTemplateModel> data = new ArrayList<>();

        data.add(new EnvironmentalThresholdTemplateModel("o2Level", "<", 19.5, "2m", "LAB",
                "Low O2", "HIGH", "Oxygen level critically low"));
        data.add(new EnvironmentalThresholdTemplateModel("co2Level", ">", 1000.0, "5m", "LAB",
                "High CO2", "HIGH", "Carbon dioxide level too high"));
        data.add(new EnvironmentalThresholdTemplateModel("temperature", ">", 28.0, "10m", "LAB",
                "High Temperature", "MEDIUM", "Temperature exceeds safe limits"));
        data.add(new EnvironmentalThresholdTemplateModel("pressure", "<", 95.0, "30s", "LAB",
                "Low Pressure", "CRITICAL", "Pressure drop detected"));
        data.add(new EnvironmentalThresholdTemplateModel("vocLevel", ">", 50.0, "1h", "HABITAT",
                "High VOC", "MEDIUM", "Volatile compounds elevated"));

        ObjectDataCompiler converter = new ObjectDataCompiler();
        String drl = converter.compile(data, template);

        System.out.println("Generated DRL from Objects:");
        System.out.println(drl);

        KieSession ksession = createKieSessionFromDRL(drl);

        doEnvironmentalTestWithMultipleModules(ksession);
    }

    private void doEnvironmentalTest(KieSession ksession) {
        // Create test environments that should trigger alarms
        Environment labLowO2 = new Environment("LAB", 18.0, 500, 5.0, 22.0, 45.0, 101.3, 25.0, 15.0);
        Environment labHighCO2 = new Environment("LAB", 20.5, 1200, 3.0, 25.0, 50.0, 101.0, 30.0, 20.0);
        Environment labHighTemp = new Environment("LAB", 20.8, 800, 2.0, 30.0, 40.0, 101.2, 20.0, 12.0);
        Environment labLowPressure = new Environment("LAB", 20.2, 600, 1.0, 23.0, 42.0, 90.0, 18.0, 10.0);
        Environment labNormal = new Environment("LAB", 20.5, 600, 2.0, 24.0, 45.0, 101.3, 20.0, 15.0);

        ksession.insert(labLowO2);
        ksession.insert(labHighCO2);
        ksession.insert(labHighTemp);
        ksession.insert(labLowPressure);
        ksession.insert(labNormal);

        int firedRules = ksession.fireAllRules();

        System.out.println("Number of rules fired: " + firedRules);

        // Verify that appropriate findings were created
        List<Finding> findings = new ArrayList<>();
        for (Object obj : ksession.getObjects()) {
            if (obj instanceof Finding) {
                findings.add((Finding) obj);
                System.out.println("Finding created: " + obj);
            }
        }

        // Should have 4 findings (low O2, high CO2, high temp, low pressure)
        assertEquals(4, findings.size(), "Should have 4 environmental threshold violations");

        // Check specific findings
        boolean hasO2Finding = findings.stream().anyMatch(f -> f.getType().equals("Low O2"));
        boolean hasCO2Finding = findings.stream().anyMatch(f -> f.getType().equals("High CO2"));
        boolean hasTempFinding = findings.stream().anyMatch(f -> f.getType().equals("High Temperature"));
        boolean hasPressureFinding = findings.stream().anyMatch(f -> f.getType().equals("Low Pressure"));

        assertTrue(hasO2Finding, "Should detect low O2");
        assertTrue(hasCO2Finding, "Should detect high CO2");
        assertTrue(hasTempFinding, "Should detect high temperature");
        assertTrue(hasPressureFinding, "Should detect low pressure");
    }

    private void doEnvironmentalTestWithMultipleModules(KieSession ksession) {
        // Create test environments for multiple modules
        Environment labLowO2 = new Environment("LAB", 18.0, 500, 5.0, 22.0, 45.0, 101.3, 25.0, 15.0);
        Environment habitatHighVOC = new Environment("HABITAT", 20.5, 700, 3.0, 25.0, 50.0, 101.0, 75.0, 20.0);
        Environment commandNormal = new Environment("COMMAND", 20.8, 600, 2.0, 24.0, 40.0, 101.2, 20.0, 12.0);

        ksession.insert(labLowO2);
        ksession.insert(habitatHighVOC);
        ksession.insert(commandNormal);

        int firedRules = ksession.fireAllRules();

        System.out.println("Number of rules fired: " + firedRules);

        // Verify that appropriate findings were created
        List<Finding> findings = new ArrayList<>();
        for (Object obj : ksession.getObjects()) {
            if (obj instanceof Finding) {
                findings.add((Finding) obj);
                System.out.println("Multi-module Finding created: " + obj);
            }
        }

        // Should have 2 findings (LAB low O2, HABITAT high VOC)
        assertEquals(2, findings.size(), "Should have 2 environmental threshold violations");

        // Check module-specific findings
        boolean hasLabO2Finding = findings.stream()
                .anyMatch(f -> f.getType().equals("Low O2") && f.getModuleId().equals("LAB"));
        boolean hasHabitatVOCFinding = findings.stream()
                .anyMatch(f -> f.getType().equals("High VOC") && f.getModuleId().equals("HABITAT"));

        assertTrue(hasLabO2Finding, "Should detect low O2 in LAB module");
        assertTrue(hasHabitatVOCFinding, "Should detect high VOC in HABITAT module");
    }

    private KieSession createKieSessionFromDRL(String drl) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);

        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.println("Error: " + message.getText());
            }

            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }

        return kieHelper.build().newKieSession();
    }
}