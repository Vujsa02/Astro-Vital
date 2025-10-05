package com.ftn.sbnz.model.models;

import java.io.Serializable;

public class EnvironmentalThresholdTemplateModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String parameter;
    private String operator;
    private Double threshold;
    private String duration;
    private String moduleId;
    private String alarmType;
    private String priority;
    private String description;

    public EnvironmentalThresholdTemplateModel() {
    }

    public EnvironmentalThresholdTemplateModel(String parameter, String operator, Double threshold,
            String duration, String moduleId, String alarmType,
            String priority, String description) {
        this.parameter = parameter;
        this.operator = operator;
        this.threshold = threshold;
        this.duration = duration;
        this.moduleId = moduleId;
        this.alarmType = alarmType;
        this.priority = priority;
        this.description = description;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EnvironmentalThresholdTemplateModel{" +
                "parameter='" + parameter + '\'' +
                ", operator='" + operator + '\'' +
                ", threshold=" + threshold +
                ", duration='" + duration + '\'' +
                ", moduleId='" + moduleId + '\'' +
                ", alarmType='" + alarmType + '\'' +
                ", priority='" + priority + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}