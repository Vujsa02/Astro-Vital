package com.ftn.sbnz.model.enums;

public enum CondensationLocation {
    WATER_LINES("water_lines"),
    PANELS("panels"),
    WALLS("walls"),
    EQUIPMENT("equipment"),
    GENERAL("general");

    private final String value;

    CondensationLocation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static CondensationLocation fromString(String text) {
        for (CondensationLocation location : CondensationLocation.values()) {
            if (location.value.equalsIgnoreCase(text)) {
                return location;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}