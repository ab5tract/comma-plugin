package edument.perl6idea.testing;

public enum RakUTestKind {
    ALL("All in project"), MODULE("All in module"),
    DIRECTORY("All in directory"), PATTERN("Pattern"),
    FILE("Test file");

    private final String prettyString;

    RakUTestKind(String string) {
        prettyString = string;
    }

    @Override
    public String toString() {
        return prettyString;
    }

    public String baseString() {
        switch (this) {
            case FILE: return "FILE";
            case DIRECTORY: return "DIRECTORY";
            case PATTERN: return "PATTERN";
            case MODULE: return "MODULE";
            case ALL: return "ALL";
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
