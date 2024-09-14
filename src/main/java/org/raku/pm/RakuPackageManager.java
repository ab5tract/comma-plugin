package org.raku.pm;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;

import java.util.Set;

public abstract class RakuPackageManager {
    protected String location;

    public String getLocation() {
        return location;
    }

    private RakuPackageManager() {}

    public RakuPackageManager(String location) {
        this.location = location;
    }

    public abstract RakuPackageManagerKind getKind();
    public abstract void install(Project project, String spec) throws ExecutionException;
    public abstract Set<String> getInstalledDistributions(Project project) throws ExecutionException;
}
