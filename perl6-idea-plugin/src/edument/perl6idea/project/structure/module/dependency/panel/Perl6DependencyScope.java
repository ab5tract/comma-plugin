package edument.perl6idea.project.structure.module.dependency.panel;

public enum Perl6DependencyScope {
    DEPENDS("depends"),
    BUILD_DEPENDS("build-depends"),
    TEST_DEPENDS("test-depends");

    private final String myDisplayName;

    Perl6DependencyScope(String displayName) {
        myDisplayName = displayName;
    }

    @Override
    public String toString() {
        return myDisplayName;
    }
}
