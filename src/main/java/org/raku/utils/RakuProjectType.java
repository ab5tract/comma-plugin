package org.raku.utils;

public enum RakuProjectType {
    RAKU_SCRIPT,
    RAKU_MODULE,
    RAKU_APPLICATION,
    CRO_WEB_APPLICATION;

    public static String getDescription(RakuProjectType type) {
        return switch (type) {
            case RAKU_SCRIPT -> "Creates a stub script file, and nothing more.";
            case RAKU_MODULE ->
                    "Creates a stub Raku module, consisting of a module for application logic and a test file. " +
                            "Includes a META6.json so the module can be installed with its dependencies and distributed.";
            case RAKU_APPLICATION ->
                    "Creates a stub Raku application, consisting of a script, a module for application logic, and a test file. " +
                            "Includes a META6.json so the application can be installed with its dependencies and distributed.";
            case CRO_WEB_APPLICATION -> "Creates a stub Cro web application";
        };
    }

    public static String toTypeLabel(RakuProjectType type) {
        return switch (type) {
            case RAKU_SCRIPT -> "Raku script";
            case RAKU_MODULE -> "Raku module";
            case RAKU_APPLICATION -> "Raku application";
            default -> "Cro web application";
        };
    }

    public static RakuProjectType fromTypeLabel(String label) {
        if (label == null)
            return RAKU_SCRIPT;
        return switch (label) {
            case "Raku script" -> RAKU_SCRIPT;
            case "Raku module" -> RAKU_MODULE;
            case "Raku application" -> RAKU_APPLICATION;
            default -> CRO_WEB_APPLICATION;
        };
    }
}