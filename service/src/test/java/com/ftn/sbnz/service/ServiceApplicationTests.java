package com.ftn.sbnz.service;

import com.ftn.sbnz.model.dto.AirQualityAnalysisResult;
import com.ftn.sbnz.model.events.AirQualityEvent;
import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.service.services.AirQualityMonitoringService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ServiceApplicationTests {

	@Autowired
	private AirQualityMonitoringService airQualityMonitoringService;

	@Test
	void contextLoads() {
	}

	@Test
	void testAirQualityMonitoringWithEpisodicContamination() {
		System.out.println("=== Testing Air Quality Monitoring (CEP-2) ===");

		// Create test environments
		List<Environment> environments = Arrays.asList(
				new Environment("LAB", 21.0, 400.0, 0.5, 22.0, 65.0, 1013.25, 30.0, 20.0),
				new Environment("CMD", 21.5, 380.0, 0.3, 21.5, 60.0, 1013.25, 25.0, 15.0));

		// Create air quality events that should trigger episodic contamination
		// Need 3+ episodes in 24h window with VOC >= 50.0 ppm OR PM >= 35.0 μg/m³
		long currentTime = System.currentTimeMillis();
		long oneHourAgo = currentTime - (1 * 60 * 60 * 1000L); // 1 hour ago
		long twoHoursAgo = currentTime - (2 * 60 * 60 * 1000L); // 2 hours ago
		long threeHoursAgo = currentTime - (3 * 60 * 60 * 1000L); // 3 hours ago

		List<AirQualityEvent> airQualityEvents = Arrays.asList(
				// Episode 1: High VOC in LAB
				new AirQualityEvent(55.0, 20.0, "LAB", threeHoursAgo),

				// Episode 2: High PM in LAB
				new AirQualityEvent(30.0, 40.0, "LAB", twoHoursAgo),

				// Episode 3: Both high in LAB
				new AirQualityEvent(60.0, 45.0, "LAB", oneHourAgo),

				// Normal reading in CMD (should not trigger)
				new AirQualityEvent(25.0, 15.0, "CMD", currentTime));

		// Process the data
		AirQualityAnalysisResult result = airQualityMonitoringService.processAirQualityData(
				environments,
				airQualityEvents);

		// Print results
		System.out.println("Rules fired: " + result.getRulesFired());
		System.out.println("Total episodes: " + result.getTotalEpisodes());
		System.out.println("Status: " + result.getStatus());
		System.out.println("Findings count: " + result.getEpisodicContaminationFindings().size());

		if (!result.getEpisodicContaminationFindings().isEmpty()) {
			System.out.println("=== Episodic Contamination Detected ===");
			result.getEpisodicContaminationFindings().forEach(finding -> {
				System.out.println("Module: " + finding.getModuleId());
				System.out.println("Type: " + finding.getType());
				System.out.println("Details: " + finding.getDetails());
				System.out.println("Priority: " + finding.getPriority());
				System.out.println("---");
			});
		} else {
			System.out.println("No episodic contamination detected");
		}

		System.out.println("=== CEP-2 Test Completed ===");
	}

	@Test
	void testAirQualityMonitoringWithoutContamination() {
		System.out.println("=== Testing Air Quality - Normal Conditions ===");

		// Create test environments
		List<Environment> environments = Arrays.asList(
				new Environment("LAB", 21.0, 400.0, 0.5, 22.0, 65.0, 1013.25, 30.0, 20.0));

		// Create normal air quality events (below thresholds)
		long currentTime = System.currentTimeMillis();
		List<AirQualityEvent> airQualityEvents = Arrays.asList(
				new AirQualityEvent(25.0, 15.0, "LAB", currentTime - 3600000L), // 1 hour ago
				new AirQualityEvent(30.0, 20.0, "LAB", currentTime - 1800000L), // 30 min ago
				new AirQualityEvent(28.0, 18.0, "LAB", currentTime) // now
		);

		// Process the data
		AirQualityAnalysisResult result = airQualityMonitoringService.processAirQualityData(
				environments,
				airQualityEvents);

		// Print results
		System.out.println("Rules fired: " + result.getRulesFired());
		System.out.println("Total episodes: " + result.getTotalEpisodes());
		System.out.println("Status: " + result.getStatus());
		System.out.println("Should be OK (no contamination): " + result.getEpisodicContaminationFindings().isEmpty());

		System.out.println("=== Normal Conditions Test Completed ===");
	}
}
