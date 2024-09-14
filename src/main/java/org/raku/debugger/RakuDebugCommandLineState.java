package org.raku.debugger;

import com.intellij.execution.runners.ExecutionEnvironment;
import org.raku.run.RakuRunCommandLineState;

public class RakuDebugCommandLineState extends RakuRunCommandLineState {
    public RakuDebugCommandLineState(ExecutionEnvironment environment) {
        super(environment);
        isDebug = true;
    }
}
