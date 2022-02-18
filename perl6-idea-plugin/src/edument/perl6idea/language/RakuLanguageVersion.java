package edument.perl6idea.language;

public enum RakuLanguageVersion {
    C, D;

    public String toString() {
        switch (this) {
            case C:
                return "6.c";
            case D:
                return "6.d";
            default:
                return "";
        }
    }
}
