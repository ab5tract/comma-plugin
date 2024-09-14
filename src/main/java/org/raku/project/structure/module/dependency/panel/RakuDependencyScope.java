package org.raku.project.structure.module.dependency.panel;

public enum RakuDependencyScope {
    DEPENDS("depends"),
    BUILD_DEPENDS("build-depends"),
    TEST_DEPENDS("test-depends");

    private final String myDisplayName;

    RakuDependencyScope(String displayName) {
        myDisplayName = displayName;
    }

    @Override
    public String toString() {
        return myDisplayName;
    }
}
