package com.ftn.sbnz.model.enums;

public enum Module {
    COMMAND("CMD"),
    LABORATORY("LAB"),
    COMMUNICATION("COMM");

    private final String id;

    Module(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    public static Module fromId(String id) {
        for (Module module : Module.values()) {
            if (module.id.equalsIgnoreCase(id)) {
                return module;
            }
        }
        throw new IllegalArgumentException("No module with ID " + id + " found");
    }

    public static String[] getAllModuleIds() {
        Module[] modules = Module.values();
        String[] ids = new String[modules.length];
        for (int i = 0; i < modules.length; i++) {
            ids[i] = modules[i].getId();
        }
        return ids;
    }

    public static String getNextModuleId(String currentModuleId, java.util.List<String> testedModules) {
        Module currentModule = fromId(currentModuleId);
        if (currentModule == null)
            return null;

        Module[] modules = Module.values();
        int currentIndex = currentModule.ordinal();

        // Find next untested module starting from current position
        for (int i = currentIndex + 1; i < modules.length; i++) {
            String nextModuleId = modules[i].getId();
            if (!testedModules.contains(nextModuleId)) {
                return nextModuleId;
            }
        }

        // If no next module found, wrap around to beginning
        for (int i = 0; i < currentIndex; i++) {
            String nextModuleId = modules[i].getId();
            if (!testedModules.contains(nextModuleId)) {
                return nextModuleId;
            }
        }

        return null; // All modules tested
    }
}