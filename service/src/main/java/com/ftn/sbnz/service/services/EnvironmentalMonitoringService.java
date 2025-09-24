package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.model.models.CondensationData;
import com.ftn.sbnz.model.models.MoistureInvestigation;
import com.ftn.sbnz.model.models.WaterRecycling;
import com.ftn.sbnz.model.models.VentilationStatus;
import com.ftn.sbnz.model.models.Finding;
import com.ftn.sbnz.model.events.HumidityEvent;
import com.ftn.sbnz.model.dto.EnvironmentalAnalysisResult;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class EnvironmentalMonitoringService {

  private final KieContainer kieContainer;
  private final FindingsService findingsService;
  private KieSession cepSession;

  @Autowired
  public EnvironmentalMonitoringService(KieContainer kieContainer,
      FindingsService findingsService) {
    this.kieContainer = kieContainer;
    this.findingsService = findingsService;
  }

  @PreDestroy
  public void destroy() {
    if (cepSession != null) {
      cepSession.dispose();
      System.out.println("EnvironmentalMonitoringService: CEP session disposed");
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
        System.out.println("EnvironmentalMonitoringService: Created lazy default KieSession");
        return true;
      } else {
        System.out.println("EnvironmentalMonitoringService: KieContainer.newKieSession() returned null");
        return false;
      }
    } catch (Throwable t) {
      // Catch Throwable to avoid NoClassDefFoundError / ClassNotFoundException
      // from Drools/MVEL causing application startup to fail.
      System.out
          .println("EnvironmentalMonitoringService: Unable to create KieSession (Drools missing or incompatible): "
              + t.toString());
      this.cepSession = null;
      return false;
    }
  }

  public synchronized EnvironmentalAnalysisResult processEnvironmentalData(List<Environment> environments,
      List<CondensationData> condensationDataList, List<HumidityEvent> humidityEvents,
      List<WaterRecycling> waterRecyclings, List<VentilationStatus> ventilationStatuses) {
    List<Finding> allFindings = new ArrayList<>();
    MoistureInvestigation investigation = null;

    // Clean up expired findings for all modules before processing
    findingsService.cleanupAllExpiredFindings();

    // Try to ensure a CEP session is available. If not, skip CEP processing
    // and return findings collected so far (the system can still function in
    // environments without Drools available).
    int rulesFired = 0;
    if (ensureSession()) {
      // Clean out previous input facts from the persistent CEP session so
      // repeated API calls don't accumulate facts across runs. This removes
      // Environment, CondensationData, HumidityEvent, WaterRecycling,
      // VentilationStatus, MoistureInvestigation and Finding facts.
      try {
        // Remove MoistureInvestigation if present
        Collection<?> invs = cepSession.getObjects(o -> o instanceof MoistureInvestigation);
        for (Object inv : invs) {
          FactHandle fh = cepSession.getFactHandle(inv);
          if (fh != null)
            cepSession.delete(fh);
        }

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

        // Remove CondensationData facts
        Collection<?> conds = cepSession.getObjects(o -> o instanceof CondensationData);
        for (Object c : conds) {
          FactHandle fh = cepSession.getFactHandle(c);
          if (fh != null)
            cepSession.delete(fh);
        }

        // Remove HumidityEvent facts
        Collection<?> hums = cepSession.getObjects(o -> o instanceof HumidityEvent);
        for (Object h : hums) {
          FactHandle fh = cepSession.getFactHandle(h);
          if (fh != null)
            cepSession.delete(fh);
        }

        // Remove WaterRecycling facts
        Collection<?> wrs = cepSession.getObjects(o -> o instanceof WaterRecycling);
        for (Object w : wrs) {
          FactHandle fh = cepSession.getFactHandle(w);
          if (fh != null)
            cepSession.delete(fh);
        }

        // Remove VentilationStatus facts
        Collection<?> vs = cepSession.getObjects(o -> o instanceof VentilationStatus);
        for (Object v : vs) {
          FactHandle fh = cepSession.getFactHandle(v);
          if (fh != null)
            cepSession.delete(fh);
        }
      } catch (Exception e) {
        System.out.println("Warning: failed to fully clear previous CEP facts: " + e.getMessage());
      }

      // Insert all facts for this run into the single CEP session
      environments.forEach(cepSession::insert);
      condensationDataList.forEach(cepSession::insert);
      humidityEvents.forEach(cepSession::insert);
      waterRecyclings.forEach(cepSession::insert);
      ventilationStatuses.forEach(cepSession::insert);

      // Run CEP agenda group then investigation agenda in same session
      cepSession.getAgenda().getAgendaGroup("cep.environment").setFocus();
      rulesFired = cepSession.fireAllRules();
      System.out.println("Environmental CEP: Fired " + rulesFired + " rules across all modules");
    } else {
      System.out.println("EnvironmentalMonitoringService: No CEP session available; skipping CEP processing");
    }

    // AFTER rules fired, check for "Investigation Required" findings and handle
    // investigation
    Collection<?> findingObjects = ensureSession() ? cepSession.getObjects(o -> o instanceof Finding) : List.of();
    for (Object obj : findingObjects) {
      Finding finding = (Finding) obj;
      if (!finding.isExpired()) {
        allFindings.add(finding);

        // Check if this is an investigation request finding
        if ("Investigation Required".equals(finding.getType())) {
          // Check if there's already an active investigation in the CEP session
          boolean hasActiveInvestigation = ensureSession() && cepSession.getObjects()
              .stream()
              .anyMatch(o -> o instanceof MoistureInvestigation &&
                  !((MoistureInvestigation) o).isInvestigationComplete());

          if (!hasActiveInvestigation) {
            // Use moduleId directly from Finding object
            String moduleId = finding.getModuleId();
            // Start investigation for the module by inserting a MoistureInvestigation into
            // CEP session
            MoistureInvestigation newInvestigation = new MoistureInvestigation(moduleId);
            cepSession.insert(newInvestigation);

            // Run investigation agenda in the same CEP session with guarded loop
            cepSession.getAgenda().getAgendaGroup("investigate.moisture").setFocus();

            // Guarded loop to ensure rules re-evaluate after modify(...) actions
            int maxIterations = 20;
            int iter = 0;
            while (!newInvestigation.isInvestigationComplete() && iter < maxIterations) {
              int fired = cepSession.fireAllRules();
              if (fired == 0)
                break; // nothing new to do
              iter++;
            }

            if (!newInvestigation.isInvestigationComplete()) {
              System.out
                  .println("Investigation did not finish within " + maxIterations + " cycles for module " + moduleId);
            }

            System.out.println("INVESTIGATION STARTED (in CEP session): Based on finding for module " + moduleId);
          }
        }
      }
    }

    // Get current investigation status from CEP session (if one was inserted there)
    if (ensureSession()) {
      investigation = (MoistureInvestigation) cepSession.getObjects()
          .stream()
          .filter(o -> o instanceof MoistureInvestigation)
          .findFirst()
          .orElse(null);
    } else {
      investigation = null;
    }

    // Collect any findings that may have been inserted during the investigation
    if (ensureSession()) {
      Collection<?> findingsDuringInvest = cepSession.getObjects(o -> o instanceof Finding);
      for (Object obj : findingsDuringInvest) {
        Finding f = (Finding) obj;
        if (!f.isExpired()) {
          // avoid duplicates: check same title + moduleId
          boolean exists = allFindings.stream()
              .anyMatch(af -> af.getType().equals(f.getType()) && af.getModuleId().equals(f.getModuleId()));
          if (!exists) {
            allFindings.add(f);
          }
        }
      }
    }

    if (ensureSession()) {
      // Remove investigation fact if present (so repeated tests start clean)
      if (investigation != null) {
        FactHandle invFH = cepSession.getFactHandle(investigation);
        if (invFH != null) {
          cepSession.delete(invFH);
        }
      }

      // Remove all Finding facts currently in working memory
      Collection<?> findingsInSession = cepSession.getObjects(o -> o instanceof Finding);
      for (Object fObj : findingsInSession) {
        FactHandle fh = cepSession.getFactHandle(fObj);
        if (fh != null) {
          cepSession.delete(fh);
        }
      }
    }

    return new EnvironmentalAnalysisResult(investigation, allFindings, rulesFired);
  }

  public List<CondensationData> getCondensationData(String moduleId) {
    if (cepSession == null) {
      return new ArrayList<>();
    }

    List<CondensationData> condensationData = new ArrayList<>();
    Collection<?> objects = cepSession.getObjects(o -> o instanceof CondensationData &&
        ((CondensationData) o).getModuleID().equals(moduleId));
    for (Object obj : objects) {
      condensationData.add((CondensationData) obj);
    }

    return condensationData;
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
          System.out.println("EnvironmentalMonitoringService: CEP session reset (default session)");
        } else {
          System.out
              .println("EnvironmentalMonitoringService: Default KieSession creation returned null during reset");
        }
      } else {
        System.out.println("EnvironmentalMonitoringService: KieContainer is null; cannot reset CEP session");
      }
    } catch (Exception e) {
      System.out.println("EnvironmentalMonitoringService: Failed to reset CEP session: " + e.getMessage());
      cepSession = null;
    }
  }
}