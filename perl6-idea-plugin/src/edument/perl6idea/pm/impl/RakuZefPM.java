package edument.perl6idea.pm.impl;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.sh.run.ShRunner;
import edument.perl6idea.pm.RakuPackageManager;
import edument.perl6idea.pm.RakuPackageManagerKind;
import edument.perl6idea.utils.Perl6CommandLine;

import java.util.HashSet;
import java.util.Set;

public class RakuZefPM extends RakuPackageManager {
    public RakuZefPM(String location) {
        super(location);
    }

    @Override
    public RakuPackageManagerKind getKind() {
        return RakuPackageManagerKind.ZEF;
    }

    @Override
    public void install(Project project, String spec) throws ExecutionException {
        Perl6CommandLine cmd = new Perl6CommandLine(project);
        cmd.addParameter(location);
        cmd.addParameter("install");
        cmd.addParameter(spec);

        ShRunner shRunner = ServiceManager.getService(project, ShRunner.class);
        if (shRunner != null) {
            ApplicationManager.getApplication().invokeAndWait(() -> {
                shRunner.run(cmd.getCommandLineString(), System.getProperty("java.io.tmpdir"), "Installing Raku Distributions...", true);
            });
        }
    }

    @Override
    public Set<String> getInstalledDistributions(Project project) throws ExecutionException {
        Perl6CommandLine cmd = new Perl6CommandLine(project);
        cmd.addParameter(location);
        cmd.addParameter("list");
        cmd.addParameter("--installed");
        return new HashSet<>(cmd.executeAndRead(null));
    }
}
