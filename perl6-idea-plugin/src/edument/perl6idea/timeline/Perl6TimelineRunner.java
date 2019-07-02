// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.timeline;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import edument.perl6idea.run.Perl6RunConfiguration;
import edument.perl6idea.timeline.client.TimelineClient;
import org.jetbrains.annotations.NotNull;

public class Perl6TimelineRunner extends DefaultProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Perl 6 Timeline";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return Perl6TimelineExecutor.EXECUTOR_ID.equals(executorId) &&
               (profile instanceof Perl6RunConfiguration);
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        ExecutionResult executionResult = state.execute(env.getExecutor(), this);
        return showRunContent(executionResult, env, ((Perl6TimelineCommandLineState)state).getTimelineClient());
    }

    private static RunContentDescriptor showRunContent(ExecutionResult execute,
                                                       ExecutionEnvironment env,
                                                       TimelineClient client) throws ExecutionException {
        return execute != null
                ? new TimelineContentBuilder(execute, env).showRunContent(env.getContentToReuse(), client)
                : null;
    }
}
