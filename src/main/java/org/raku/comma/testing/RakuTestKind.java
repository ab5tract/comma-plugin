package org.raku.comma.testing;

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
        switch (this) {
            case FILE: return "FILE";
            case DIRECTORY: return "DIRECTORY";
            case MODULE: return "MODULE";
            case ALL: return "ALL";
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
