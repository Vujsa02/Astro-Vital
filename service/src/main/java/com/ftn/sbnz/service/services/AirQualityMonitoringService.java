package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.model.models.Finding;
import com.ftn.sbnz.model.events.AirQualityEvent;
import com.ftn.sbnz.model.dto.AirQualityAnalysisResult;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collections;

@Service
public class AirQualityMonitoringService {

    private final KieContainer kieContainer;
    private final FindingsService findingsService;
    private KieSession cepSession;

    @Autowired
    public AirQualityMonitoringService(KieContainer kieContainer,
            FindingsService findingsService) {
        this.kieContainer = kieContainer;
        this.findingsService = findingsService;
    }

    @PreDestroy
    public void destroy() {
        if (cepSession != null) {
            cepSession.dispose();
            System.out.println("AirQualityMonitoringService: CEP session disposed");
        }
    }

    private synchronized boolean ensureSession() {
        if (this.cepSession != null) {
            return true;
        }

        if (kieContainer == null) {
            return false;
        }

        try {
            this.cepSession = kieContainer.newKieSession();
            if (this.cepSession != null) {
                this.cepSession.setGlobal("findingsService", findingsService);
                System.out.println("AirQualityMonitoringService: Created lazy default KieSession");
                return true;
            } else {
                System.out.println("AirQualityMonitoringService: KieContainer.newKieSession() returned null");
                return false;
            }
        } catch (Throwable t) {
            // Catch Throwable to avoid NoClassDefFoundError / ClassNotFoundException
            // from Drools/MVEL causing application startup to fail.
            System.out
                    .println(
                            "AirQualityMonitoringService: Unable to create KieSession (Drools missing or incompatible): "
                                    + t.toString());
            this.cepSession = null;
            return false;
        }
    }

    public synchronized AirQualityAnalysisResult processAirQualityData(List<Environment> environments,
            List<AirQualityEvent> airQualityEvents) {
        List<Finding> allFindings = new ArrayList<>();

        // Clean up expired findings for all modules before processing
        findingsService.cleanupAllExpiredFindings();

        // Try to ensure a CEP session is available. If not, skip CEP processing
        // and return findings collected so far (the system can still function in
        // environments without Drools available).
        int rulesFired = 0;
        if (ensureSession()) {
            // Clean out previous input facts from the persistent CEP session so
            // repeated API calls don't accumulate facts across runs. This removes
            // Environment, AirQualityEvent and Finding facts.
            try {
                // Remove Findings
                Collection<?> findingsToRemove = cepSession.getObjects(o -> o instanceof Finding);
                for (Object f : findingsToRemove) {
                    FactHandle fh = cepSession.getFactHandle(f);
                    if (fh != null)
                        cepSession.delete(fh);
                }

                // Remove Environment facts
                Collection<?> envs = cepSession.getObjects(o -> o instanceof Environment);
                for (Object e : envs) {
                    FactHandle fh = cepSession.getFactHandle(e);
                    if (fh != null)
                        cepSession.delete(fh);
                }

                // Remove AirQualityEvent facts
                Collection<?> airEvents = cepSession.getObjects(o -> o instanceof AirQualityEvent);
                for (Object ae : airEvents) {
                    FactHandle fh = cepSession.getFactHandle(ae);
                    if (fh != null)
                        cepSession.delete(fh);
                }
            } catch (Exception e) {
                System.out.println("Warning: failed to fully clear previous CEP facts: " + e.getMessage());
            }

            // Insert all facts for this run into the single CEP session
            environments.forEach(cepSession::insert);
            airQualityEvents.forEach(cepSession::insert);

            // Run CEP agenda group
            cepSession.getAgenda().getAgendaGroup("cep.air-quality").setFocus();
            rulesFired = cepSession.fireAllRules();
            System.out.println("Air Quality CEP: Fired " + rulesFired + " rules across all modules");
        } else {
            System.out.println("AirQualityMonitoringService: No CEP session available; skipping CEP processing");
        }

        // AFTER rules fired, collect all findings
        Collection<?> findingObjects = ensureSession() ? cepSession.getObjects(o -> o instanceof Finding) : List.of();
        for (Object obj : findingObjects) {
            Finding finding = (Finding) obj;
            if (!finding.isExpired()) {
                allFindings.add(finding);
            }
        }

        // Persist findings into FindingsService if not already present
        for (Finding f : allFindings) {
            try {
                if (!findingsService.hasActiveFinding(f.getModuleId(), f.getType())) {
                    findingsService.addFindings(f.getModuleId(), Collections.singletonList(f));
                }
            } catch (Exception ex) {
                System.out.println("AirQualityMonitoringService: Failed to persist finding: " + ex.getMessage());
            }
        }

        if (ensureSession()) {
            // Remove all Finding facts currently in working memory
            Collection<?> findingsInSession = cepSession.getObjects(o -> o instanceof Finding);
            for (Object fObj : findingsInSession) {
                FactHandle fh = cepSession.getFactHandle(fObj);
                if (fh != null) {
                    cepSession.delete(fh);
                }
            }
        }

        // Count total episodes for analysis result
        int totalEpisodes = countTotalEpisodes(airQualityEvents);
        String status = allFindings.isEmpty() ? "OK" : "EPISODIC_CONTAMINATION_DETECTED";

        return new AirQualityAnalysisResult(allFindings, totalEpisodes, rulesFired, status);
    }

    private int countTotalEpisodes(List<AirQualityEvent> events) {
        if (events == null || events.isEmpty())
            return 0;

        int count = 0;
        for (AirQualityEvent event : events) {
            if (event.getVocLevel() >= 50.0 || event.getPmLevel() >= 35.0) {
                count++;
            }
        }
        return count;
    }

    public List<Finding> getEpisodicContaminationFindings(String moduleId) {
        if (cepSession == null) {
            return new ArrayList<>();
        }

        List<Finding> contaminations = new ArrayList<>();
        Collection<?> objects = cepSession.getObjects(o -> o instanceof Finding &&
                "Episodic Air Contamination".equals(((Finding) o).getType()) &&
                ((Finding) o).getModuleId().equals(moduleId));

        for (Object obj : objects) {
            Finding finding = (Finding) obj;
            if (!finding.isExpired()) {
                contaminations.add(finding);
            }
        }

        return contaminations;
    }

    public void resetSession() {
        try {
            if (cepSession != null) {
                cepSession.dispose();
            }
            if (kieContainer != null) {
                cepSession = kieContainer.newKieSession();
                if (cepSession != null) {
                    cepSession.setGlobal("findingsService", findingsService);
                    System.out.println("AirQualityMonitoringService: CEP session reset (default session)");
                } else {
                    System.out
                            .println(
                                    "AirQualityMonitoringService: Default KieSession creation returned null during reset");
                }
            } else {
                System.out.println("AirQualityMonitoringService: KieContainer is null; cannot reset CEP session");
            }
        } catch (Exception e) {
            System.out.println("AirQualityMonitoringService: Failed to reset CEP session: " + e.getMessage());
            cepSession = null;
        }
    }
}