package edument.perl6idea.debugger;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.apache.commons.lang.StringUtils;
import edument.perl6idea.run.Perl6RunCommandLineState;
import edument.perl6idea.sdk.Perl6SdkType;

import java.util.LinkedList;
import java.util.Map;

public class Perl6DebugCommandLineState extends Perl6RunCommandLineState {
    public Perl6DebugCommandLineState(ExecutionEnvironment environment) throws ExecutionException {
        super(environment);
        this.command = new LinkedList<>();
        populateRunCommand();
    }

    private void populateRunCommand() throws ExecutionException {
        Perl6SdkType projectSdk = Perl6SdkType.getInstance();
        Map<String, String> moarBuildConfiguration = projectSdk.getMoarBuildConfiguration();
        String prefix = moarBuildConfiguration.getOrDefault("perl6::prefix", "");
        this.command.add(prefix + "/bin/moar");
        this.command.add("--debug-port=" + runConfiguration.getDebugPort());
        this.command.add("--debug-suspend");
        this.command.add("--libpath=" + prefix + "/share/nqp/lib");
        this.command.add("--libpath=" + prefix + "/share/perl6/lib");
        this.command.add("--libpath=" + prefix + "/share/perl6/runtime");
        this.command.add(prefix + "/share/perl6/runtime/perl6.moarvm");
        if (StringUtils.isNotBlank(runConfiguration.getInterpreterParameters()))
            this.command.add(runConfiguration.getInterpreterParameters());
    }
}
