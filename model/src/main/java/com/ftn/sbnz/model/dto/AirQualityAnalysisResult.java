package com.ftn.sbnz.model.dto;

import com.ftn.sbnz.model.models.Finding;
import java.util.List;

public class AirQualityAnalysisResult {
    private List<Finding> episodicContaminationFindings;
    private int totalEpisodes;
    private int rulesFired;
    private String status;

    public AirQualityAnalysisResult() {
    }

    public AirQualityAnalysisResult(List<Finding> episodicContaminationFindings, int totalEpisodes, int rulesFired,
            String status) {
        this.episodicContaminationFindings = episodicContaminationFindings;
        this.totalEpisodes = totalEpisodes;
        this.rulesFired = rulesFired;
        this.status = status;
    }

    public List<Finding> getEpisodicContaminationFindings() {
        return episodicContaminationFindings;
    }

    public void setEpisodicContaminationFindings(List<Finding> episodicContaminationFindings) {
        this.episodicContaminationFindings = episodicContaminationFindings;
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
                "episodicContaminationFindings=" + episodicContaminationFindings +
                ", totalEpisodes=" + totalEpisodes +
                ", rulesFired=" + rulesFired +
                ", status='" + status + '\'' +
                '}';
    }
}