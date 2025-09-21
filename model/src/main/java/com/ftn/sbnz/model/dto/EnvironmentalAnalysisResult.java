package com.ftn.sbnz.model.dto;

import com.ftn.sbnz.model.models.MoistureInvestigation;
import com.ftn.sbnz.model.models.Finding;
import java.util.List;

public class EnvironmentalAnalysisResult {
  private MoistureInvestigation investigation;
  private List<Finding> findings;
  private int rulesFired;

  public EnvironmentalAnalysisResult() {
  }

  public EnvironmentalAnalysisResult(MoistureInvestigation investigation, List<Finding> findings, int rulesFired) {
    this.investigation = investigation;
    this.findings = findings;
    this.rulesFired = rulesFired;
  }

  public MoistureInvestigation getInvestigation() {
    return investigation;
  }

  public void setInvestigation(MoistureInvestigation investigation) {
    this.investigation = investigation;
  }

  public List<Finding> getFindings() {
    return findings;
  }

  public void setFindings(List<Finding> findings) {
    this.findings = findings;
  }

  public int getRulesFired() {
    return rulesFired;
  }

  public void setRulesFired(int rulesFired) {
    this.rulesFired = rulesFired;
  }

  @Override
  public String toString() {
    return "EnvironmentalAnalysisResult{" +
        "investigation=" + investigation +
        ", findings=" + findings +
        ", rulesFired=" + rulesFired +
        '}';
  }
}