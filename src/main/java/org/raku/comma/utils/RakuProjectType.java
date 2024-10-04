package org.raku.comma.utils;

import java.util.Vector;

public enum RakuProjectType {
    RAKU_SCRIPT,
    RAKU_MODULE,
    RAKU_APPLICATION,
    CRO_WEB_APPLICATION;

    public static String getDescription(RakuProjectType type) {
        return switch (type) {
            case RAKU_SCRIPT -> "<p>Creates a stub script file, and nothing more.</p>";
            case RAKU_MODULE ->
                    "<p>Creates a stub Raku module, consisting of a module<br/> for application logic and a test file. " +
                            "Includes a<br/> META6.json so the module can be installed with its<br/> dependencies and distributed.</p>";
            case RAKU_APPLICATION ->
                    "<p>Creates a stub Raku application, consisting of a script,<br/> a module for application logic, and a test file. <br/>" +
                            "Includes a META6.json so the application can be <br/>installed with its dependencies and distributed.</p>";
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

    public static Vector<RakuProjectType> types() {
        var vector = new Vector<RakuProjectType>();
        vector.add(RAKU_SCRIPT);
        vector.add(RAKU_MODULE);
        vector.add(RAKU_APPLICATION);
        return vector;
    }

    public static Vector<String> typeLabels() {
        var vector = new Vector<String>();
        vector.add(toTypeLabel(RakuProjectType.RAKU_SCRIPT));
        vector.add(toTypeLabel(RakuProjectType.RAKU_MODULE));
        vector.add(toTypeLabel(RakuProjectType.RAKU_APPLICATION));
        return vector;
    }
}