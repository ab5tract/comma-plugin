package edument.perl6idea.debugger;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import edument.perl6idea.run.Perl6RunCommandLineState;
import edument.perl6idea.utils.Perl6CommandLine;

public class Perl6DebugCommandLineState extends Perl6RunCommandLineState {
    public Perl6DebugCommandLineState(ExecutionEnvironment environment) throws ExecutionException {
        super(environment);
    }

    @Override
    protected void populateRunCommand() {
        command = Perl6CommandLine.populateDebugCommandLine(getEnvironment().getProject(),
                                                            runConfiguration.getDebugPort());
        String params = runConfiguration.getInterpreterParameters();
        if (params != null && !params.trim().isEmpty()) {
            command.add(params);
        }
    }
}
