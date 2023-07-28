package edument.perl6idea.utils;

public enum Perl6ProjectType {
    PERL6_SCRIPT,
    PERL6_MODULE,
    PERL6_APPLICATION,
    CRO_WEB_APPLICATION;

    public static String getDescription(Perl6ProjectType type) {
        return switch (type) {
            case PERL6_SCRIPT -> "Creates a stub script file, and nothing more.";
            case PERL6_MODULE -> "Creates a stub Raku module, consisting of a module for application logic and a test file. " +
                                 "Includes a META6.json so the module can be installed with its dependencies and distributed.";
            case PERL6_APPLICATION ->
                "Creates a stub Raku application, consisting of a script, a module for application logic, and a test file. " +
                "Includes a META6.json so the application can be installed with its dependencies and distributed.";
            case CRO_WEB_APPLICATION -> "Creates a stub Cro web application";
        };
    }

    public static String toTypeLabel(Perl6ProjectType type) {
        return switch (type) {
            case PERL6_SCRIPT -> "Raku script";
            case PERL6_MODULE -> "Raku module";
            case PERL6_APPLICATION -> "Raku application";
            default -> "Cro web application";
        };
    }

    public static Perl6ProjectType fromTypeLabel(String label) {
        if (label == null)
            return PERL6_SCRIPT;
        return switch (label) {
            case "Raku script" -> PERL6_SCRIPT;
            case "Raku module" -> PERL6_MODULE;
            case "Raku application" -> PERL6_APPLICATION;
            default -> CRO_WEB_APPLICATION;
        };
    }
}