package edument.perl6idea.testing;

public enum RakUTestKind {
    ALL("All in project"), MODULE("All in module"), DIRECTORY("All in directory"), PATTERN("By glob pattern"), FILE("Test file");

    private final String prettyString;

    RakUTestKind(String string) {
        prettyString = string;
    }
    @Override
    public String toString() {
        return prettyString;
    }
}
