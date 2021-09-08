package edument.perl6idea.pm.impl;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import edument.perl6idea.pm.RakuPackageManager;
import edument.perl6idea.pm.RakuPackageManagerKind;
import edument.perl6idea.utils.Perl6CommandLine;

import java.util.HashSet;
import java.util.List;
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
        Perl6CommandLine cmd = new Perl6CommandLine(project);
        cmd.addParameter(location);
        cmd.addParameter("add");
        cmd.addParameter(spec);
        List<String> result = cmd.executeAndRead(null);
    }

    @Override
    public Set<String> getInstalledDistributions(Project project) throws ExecutionException {
        Perl6CommandLine cmd = new Perl6CommandLine(project);
        cmd.addParameter(location);
        cmd.addParameter("list");
        return new HashSet<>(cmd.executeAndRead(null));
    }
}
