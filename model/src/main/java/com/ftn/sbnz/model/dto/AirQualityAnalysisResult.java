package com.ftn.sbnz.model.dto;

import com.ftn.sbnz.model.models.Finding;
import java.util.List;

public class AirQualityAnalysisResult {
    private List<Finding> findings;
    private int totalEpisodes;
    private int rulesFired;
    private String status;

    public AirQualityAnalysisResult() {
    }

    public AirQualityAnalysisResult(List<Finding> findings, int totalEpisodes, int rulesFired,
            String status) {
        this.findings = findings;
        this.totalEpisodes = totalEpisodes;
        this.rulesFired = rulesFired;
        this.status = status;
    }

    public List<Finding> getfindings() {
        return findings;
    }

    public void setfindings(List<Finding> findings) {
        this.findings = findings;
    }

    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(int totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public int getRulesFired() {
        return rulesFired;
    }

    public void setRulesFired(int rulesFired) {
        this.rulesFired = rulesFired;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AirQualityAnalysisResult{" +
                "findings=" + findings +
                ", totalEpisodes=" + totalEpisodes +
                ", rulesFired=" + rulesFired +
                ", status='" + status + '\'' +
                '}';
    }
}