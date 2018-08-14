package edument.perl6idea.profiler;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import edument.perl6idea.run.Perl6RunCommandLineState;
import edument.perl6idea.sdk.Perl6SdkType;

public class Perl6ProfileCommandLineState extends Perl6RunCommandLineState {
    public Perl6ProfileCommandLineState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected void populateRunCommand() throws ExecutionException {
        checkSDK();
        command.add(Perl6SdkType.perl6Command());
        command.add("--profile");
        setInterpreterParameters();
    }
}
