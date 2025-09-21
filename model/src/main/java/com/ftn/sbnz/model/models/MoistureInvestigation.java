package com.ftn.sbnz.model.models;

import java.util.ArrayList;
import java.util.List;

public class MoistureInvestigation {
    private String currentModuleID;
    private List<String> testedModules;
    private boolean investigationComplete;
    private boolean sourceFound;
    private String confirmedHypothesis;
    private String result;
    private String evidence;

    // Cleanup management fields
    private long completionTimestamp;

    public MoistureInvestigation() {
        this.testedModules = new ArrayList<>();
        this.investigationComplete = false;
        this.sourceFound = false;
    }

    public MoistureInvestigation(String currentModuleID) {
        this();
        this.currentModuleID = currentModuleID;
    }

    public String getCurrentModuleID() {
        return currentModuleID;
    }

    public void setCurrentModuleID(String currentModuleID) {
        this.currentModuleID = currentModuleID;
    }

    public List<String> getTestedModules() {
        return testedModules;
    }

    public void setTestedModules(List<String> testedModules) {
        this.testedModules = testedModules;
    }

    public void addTestedModule(String moduleID) {
        if (!this.testedModules.contains(moduleID)) {
            this.testedModules.add(moduleID);
        }
    }

    public boolean isInvestigationComplete() {
        return investigationComplete;
    }

    public void setInvestigationComplete(boolean investigationComplete) {
        this.investigationComplete = investigationComplete;
    }

    public boolean isSourceFound() {
        return sourceFound;
    }

    public void setSourceFound(boolean sourceFound) {
        this.sourceFound = sourceFound;
    }

    public String getConfirmedHypothesis() {
        return confirmedHypothesis;
    }

    public void setConfirmedHypothesis(String confirmedHypothesis) {
        this.confirmedHypothesis = confirmedHypothesis;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public long getCompletionTimestamp() {
        return completionTimestamp;
    }

    public void setCompletionTimestamp(long completionTimestamp) {
        this.completionTimestamp = completionTimestamp;
    }

    @Override
    public String toString() {
        return "MoistureInvestigation{" +
                "currentModuleID='" + currentModuleID + '\'' +
                ", testedModules=" + testedModules +
                ", investigationComplete=" + investigationComplete +
                ", sourceFound=" + sourceFound +
                ", confirmedHypothesis='" + confirmedHypothesis + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}