package org.raku.comma.debugger;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import org.raku.comma.run.RakuRunConfiguration;
import org.raku.comma.testing.RakuTestRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuDebugRunner extends RakuDefaultRunner {
    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state,
                                             @NotNull final ExecutionEnvironment env) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        XDebuggerManager xDebuggerManager = XDebuggerManager.getInstance(env.getProject());
        return xDebuggerManager.startSession(env, new XDebugProcessStarter() {
            @NotNull
            @Override
            public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
                session.setPauseActionSupported(true);
                return new RakuXDebugProcess(session, state.execute(env.getExecutor(), RakuDebugRunner.this));
            }
        }).getRunContentDescriptor();
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return "Raku Debugger";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) &&
               (profile instanceof RakuRunConfiguration || profile instanceof RakuTestRunConfiguration);
    }
}
