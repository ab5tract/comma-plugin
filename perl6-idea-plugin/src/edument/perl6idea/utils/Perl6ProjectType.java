package edument.perl6idea.utils;

public enum Perl6ProjectType {
    PERL6_SCRIPT,
    PERL6_MODULE,
    PERL6_APPLICATION,
    CRO_WEB_APPLICATION;

    public static String getDescription(Perl6ProjectType type) {
        switch (type) {
            case PERL6_SCRIPT:
                return "Creates a stub script file, and nothing more.";
            case PERL6_MODULE:
                return "Creates a stub Perl 6 module, consisting of a module for application logic and a test file. " +
                        "Includes a META6.json so the module can be installed with its dependencies and distributed.";
            case PERL6_APPLICATION:
                return "Creates a stub Perl 6 application, consisting of a script, a module for application logic, and a test file. " +
                        "Includes a META6.json so the application can be installed with its dependencies and distributed.";
            case CRO_WEB_APPLICATION:
                return "Creates a stub Cro web application";
        }
        return null;
    }

    public static String toTypeLabel(Perl6ProjectType type) {
        switch (type) {
            case PERL6_SCRIPT:
                return "Perl 6 script";
            case PERL6_MODULE:
                return "Perl 6 module";
            case PERL6_APPLICATION:
                return "Perl 6 application";
            default:
                return "Cro web application";
        }
    }

    public static Perl6ProjectType fromTypeLabel(String label) {
        if (label == null)
            return PERL6_SCRIPT;
        switch (label) {
            case "Perl 6 script":
                return PERL6_SCRIPT;
            case "Perl 6 module":
                return PERL6_MODULE;
            case "Perl 6 application":
                return PERL6_APPLICATION;
            default:
                return CRO_WEB_APPLICATION;
        }
    }
}