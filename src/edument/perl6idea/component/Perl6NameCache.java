package edument.perl6idea.component;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6CommandLine;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Perl6NameCache implements ProjectComponent {
    private Map<String, Set<String>> nameCache = new ConcurrentHashMap<>();
    private static Logger LOG = Logger.getInstance(Perl6NameCache.class);

    public Set<String> getNames(Project project, String name) {
        Set<String> result = new HashSet<>();
        Set<String> cached = nameCache.get(name);
        if (cached != null) return cached;

        String homePath = Perl6SdkType.getSdkHomeByProject(project);
        if (homePath == null) {
            LOG.error(new ExecutionException("SDK path is not set"));
            return result;
        }
        GeneralCommandLine cmd = Perl6CommandLine.getPerl6CommandLine(
                System.getProperty("java.io.tmpdir"),
                homePath);
        String exec = String.format("use %s; for UNIT::.keys { when /^[<[$@%%&]>]<:Ll>/ { .say } }", name);
        List<String> names = Perl6CommandLine.execute(Perl6CommandLine.pushLine(cmd, exec));
        if (names != null) {
            result = new HashSet<>(names);
            nameCache.put(name, result);
        }
        return result;
    }
}
