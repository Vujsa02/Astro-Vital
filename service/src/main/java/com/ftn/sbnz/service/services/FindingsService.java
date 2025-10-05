package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.models.Finding;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FindingsService {

  // key = moduleId (use "global" if not module-specific)
  private final Map<String, List<Finding>> store = new ConcurrentHashMap<>();

  public synchronized void addFindings(String moduleId, Collection<Finding> findings) {
    String key = moduleId == null ? "global" : moduleId;
    store.compute(key, (k, v) -> {
      List<Finding> list = v == null ? new ArrayList<>() : new ArrayList<>(v);
      list.addAll(findings);
      return list;
    });
  }

  public List<Finding> getFindings(String moduleId) {
    return Collections
        .unmodifiableList(store.getOrDefault(moduleId == null ? "global" : moduleId, Collections.emptyList()));
  }

  public Map<String, List<Finding>> getAllFindings() {
    return Collections.unmodifiableMap(store);
  }

  public void clearModuleFindings(String moduleId) {
    store.remove(moduleId == null ? "global" : moduleId);
  }

  public void clearAll() {
    store.clear();
  }

  // Check if an active (non-expired) finding of a specific type exists for a
  // module
  public synchronized boolean hasActiveFinding(String moduleId, String findingType) {
    List<Finding> findings = getFindings(moduleId);
    return findings.stream()
        .anyMatch(f -> f.getType().equals(findingType) && !f.isExpired());
  }

  // Clean up expired findings for a specific module
  public synchronized void cleanupExpiredFindings(String moduleId) {
    String key = moduleId == null ? "global" : moduleId;
    List<Finding> findings = store.getOrDefault(key, Collections.emptyList());
    List<Finding> activeFindings = findings.stream()
        .filter(f -> !f.isExpired())
        .collect(java.util.stream.Collectors.toList());

    if (activeFindings.size() != findings.size()) {
      store.put(key, activeFindings);
      System.out.println("FindingsService: Cleaned up " + (findings.size() - activeFindings.size()) +
          " expired findings for module " + moduleId);
    }
  }

  // Clean up expired findings for all modules
  public synchronized void cleanupAllExpiredFindings() {
    for (String moduleId : new ArrayList<>(store.keySet())) {
      cleanupExpiredFindings(moduleId);
    }
  }

  // Delete specific findings by type and moduleId
  public synchronized boolean deleteFindingsByTypeAndModule(String type, String moduleId) {
    String key = moduleId == null ? "global" : moduleId;
    List<Finding> findings = store.getOrDefault(key, Collections.emptyList());

    List<Finding> filteredFindings = findings.stream()
        .filter(f -> !f.getType().equals(type))
        .collect(java.util.stream.Collectors.toList());

    boolean removed = filteredFindings.size() != findings.size();

    if (removed) {
      if (filteredFindings.isEmpty()) {
        store.remove(key);
      } else {
        store.put(key, filteredFindings);
      }
      System.out.println("FindingsService: Deleted findings of type '" + type +
          "' for module " + moduleId);
    }

    return removed;
  }

  // Delete multiple findings by their type and moduleId
  public synchronized int deleteMultipleFindings(List<FindingIdentifier> identifiers) {
    int deletedCount = 0;

    for (FindingIdentifier identifier : identifiers) {
      if (deleteFindingsByTypeAndModule(identifier.getType(), identifier.getModuleId())) {
        deletedCount++;
      }
    }

    return deletedCount;
  }

  // Helper class for finding identification
  public static class FindingIdentifier {
    private String type;
    private String moduleId;

    public FindingIdentifier() {
    }

    public FindingIdentifier(String type, String moduleId) {
      this.type = type;
      this.moduleId = moduleId;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getModuleId() {
      return moduleId;
    }

    public void setModuleId(String moduleId) {
      this.moduleId = moduleId;
    }
  }
}
