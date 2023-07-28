package edument.perl6idea.testing;

public enum RakuTestKind {
    ALL("All in project"), MODULE("All in module"),
    DIRECTORY("All in directory"), FILE("Test file");

    private final String prettyString;

    RakuTestKind(String string) {
        prettyString = string;
    }

    @Override
    public String toString() {
        return prettyString;
    }

    public String baseString() {
        return switch (this) {
            case FILE -> "FILE";
            case DIRECTORY -> "DIRECTORY";
            case MODULE -> "MODULE";
            case ALL -> "ALL";
        };
    }
}
