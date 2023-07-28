package edument.perl6idea.language;

public enum RakuLanguageVersion {
    C, D;

    public String toString() {
        return switch (this) {
            case C -> "6.c";
            case D -> "6.d";
        };
    }
}
