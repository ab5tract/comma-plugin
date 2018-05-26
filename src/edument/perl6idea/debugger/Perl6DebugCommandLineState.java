package edument.perl6idea.debugger;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import edument.perl6idea.run.Perl6RunCommandLineState;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6CommandLine;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Perl6DebugCommandLineState extends Perl6RunCommandLineState {
    public Perl6DebugCommandLineState(ExecutionEnvironment environment) throws ExecutionException {
        super(environment);
    }

    @Override
    protected void populateRunCommand() throws ExecutionException {
        command = Perl6CommandLine.populateDebugCommandLine(getEnvironment().getProject(),
                                                            runConfiguration.getDebugPort());
    }
}
