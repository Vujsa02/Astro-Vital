package com.ftn.sbnz.model.models;

public class CrewMember {
    private String crewMemberID;
    private String name;
    private String moduleID;

    public CrewMember() {
    }

    public CrewMember(String crewMemberID, String name, String moduleID) {
        this.crewMemberID = crewMemberID;
        this.name = name;
        this.moduleID = moduleID;
    }

    public String getCrewMemberID() {
        return crewMemberID;
    }

    public void setCrewMemberID(String crewMemberID) {
        this.crewMemberID = crewMemberID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }

    @Override
    public String toString() {
        return "CrewMember{" +
                "crewMemberID='" + crewMemberID + '\'' +
                ", name='" + name + '\'' +
                ", moduleID='" + moduleID + '\'' +
                '}';
    }
}