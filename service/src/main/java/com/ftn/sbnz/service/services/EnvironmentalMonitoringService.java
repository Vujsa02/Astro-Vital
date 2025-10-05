package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.models.Environment;
import com.ftn.sbnz.model.models.CondensationData;
import com.ftn.sbnz.model.models.MoistureInvestigation;
import com.ftn.sbnz.model.models.ModuleLink;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
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
        // Seed module connectivity graph for recursive query (acyclic upstream chain
        // COMM -> LAB -> CMD)
        // Direction: ModuleLink(prev, next). Query walks upstream via ModuleLink($prev,
        // mod;)
        this.cepSession.insert(new ModuleLink("COMM", "LAB"));
        this.cepSession.insert(new ModuleLink("LAB", "CMD"));
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

    findingsService.cleanupAllExpiredFindings();

    int rulesFired = 0;
    if (ensureSession()) {

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
          // Use moduleId directly from Finding object
          String moduleId = finding.getModuleId();

          // Use the recursive query with visited list (pass null to start)
          try {
            String[] hypotheses = { "H1", "H2", "H3" };
            boolean sourceFound = false;

            for (String hypothesis : hypotheses) {
              System.out.println("[MoistureQuery] Query start: hypothesis=" + hypothesis + ", start=" + moduleId);
              QueryResults results = cepSession.getQueryResults("findMoistureSource", hypothesis, moduleId);
              int size = results.size();
              System.out.println("[MoistureQuery] Result count=" + size + " (single traversal)");
              if (size > 0) {
                String matchedModule = moduleId; // default
                for (QueryResultsRow row : results) { // grab first row's bound 'mod'
                  try {
                    Object bound = row.get("mod");
                    if (bound instanceof String) {
                      matchedModule = (String) bound;
                    }
                  } catch (Exception ignore) {
                  }
                  break;
                }
                // Derive traversal path & depth using auxiliary path query
                try {
                  QueryResults pathResults = cepSession.getQueryResults("findMoistureSourcePath", hypothesis, moduleId, null);
                  if (pathResults.size() > 0) {
                    QueryResultsRow prow = pathResults.iterator().next();
                    Object pathObj = prow.get("path");
                    if (pathObj instanceof java.util.List) {
                      @SuppressWarnings("unchecked")
                      java.util.List<String> path = (java.util.List<String>) pathObj;
                      System.out.println("[MoistureQuery] Traversal path=" + path + " depth=" + (path.size() - 1));
                    } else {
                      System.out.println("[MoistureQuery] Path binding not a List: " + pathObj);
                    }
                  } else {
                    System.out.println("[MoistureQuery] Path query returned no rows despite primary match");
                  }
                } catch (Exception px) {
                  System.out.println("[MoistureQuery] Path query error: " + px.getMessage());
                }
                Finding moistureFinding = new Finding("Moisture source found", matchedModule,
                    "Hypothesis " + hypothesis + " satisfied at module " + matchedModule, "HIGH");
                cepSession.insert(moistureFinding);
                allFindings.add(moistureFinding);
                sourceFound = true;
                System.out.println("[MoistureQuery] MATCH: hypothesis=" + hypothesis + " module=" + matchedModule);
                break; // only first matching hypothesis reported
              } else {
                System.out
                    .println("[MoistureQuery] No match for hypothesis=" + hypothesis + " starting at " + moduleId);
              }
            }

            if (!sourceFound) {
              Finding noSourceFinding = new Finding("Investigation complete - no source", moduleId,
                  "No moisture source found after checking all hypotheses", "LOW");
              cepSession.insert(noSourceFinding);
              allFindings.add(noSourceFinding);
              System.out.println("[MoistureQuery] NO MATCH for module=" + moduleId);
            }
          } catch (Exception e) {
            System.out.println("Error during moisture investigation query: " + e.getMessage());
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