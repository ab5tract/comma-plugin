package org.raku.comma.pm.impl;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import org.raku.comma.pm.RakuPackageManager;
import org.raku.comma.pm.RakuPackageManagerKind;
import org.raku.comma.pm.ui.RakuPMWidget;
import org.raku.comma.utils.RakuCommandLine;

import java.util.HashSet;
import java.util.Set;

public class RakuPakkuPM extends RakuPackageManager {
    public RakuPakkuPM(String location) {
        super(location);
    }

    @Override
    public RakuPackageManagerKind getKind() {
        return RakuPackageManagerKind.PAKKU;
    }

    @Override
    public void install(Project project, String spec) throws ExecutionException {
        RakuCommandLine cmd = new RakuCommandLine(project);
        cmd.addParameter(location);
        cmd.addParameter("add");
        cmd.addParameter(spec);
        RakuPMWidget.initAndRun(project, cmd);
    }

    @Override
    public Set<String> getInstalledDistributions(Project project) throws ExecutionException {
        RakuCommandLine cmd = new RakuCommandLine(project);
        cmd.addParameter(location);
        cmd.addParameter("list");
        return new HashSet<>(cmd.executeAndRead(null));
    }
}