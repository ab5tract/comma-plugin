// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.timeline;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.runners.RunTab;
import com.intellij.execution.ui.*;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


public class TimelineContentBuilder extends RunTab {
    private final static String TIMELINE_CONTENT_ID = "TimelineContent";
    private ExecutionResult myExecutionResult;

    public TimelineContentBuilder(@NotNull ExecutionResult executionResult,
                                  @NotNull ExecutionEnvironment environment) {
        super(environment.getProject(), GlobalSearchScope.allScope(environment.getProject()),
              environment.getRunner().getRunnerId(), "Timeline",
              environment.getRunProfile().getName());
        myEnvironment = environment;
        myExecutionResult = executionResult;
        myUi.getDefaults()
            .initTabDefaults(0, "Timeline", null)
            .initTabDefaults(1, "Console", null);
    }

    public RunContentDescriptor showRunContent(@Nullable RunContentDescriptor reuseContent,
                                               TimelineClient client) {
        RunContentDescriptor descriptor = createDescriptor(client);
        Disposer.register(descriptor, this);
        Disposer.register(myProject, descriptor);
        RunContentManagerImpl.copyContentAndBehavior(descriptor, reuseContent);
        myRunContentDescriptor = descriptor;
        return descriptor;
    }

    @NotNull
    private RunContentDescriptor createDescriptor(TimelineClient client) {
        final RunProfile profile = myEnvironment.getRunProfile();
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return new RunContentDescriptor(profile, myExecutionResult, myUi);
        }
        RunContentDescriptor contentDescriptor = new RunContentDescriptor(profile, myExecutionResult, myUi);
        addTimelineTab(client);
        addConsoleTab(profile, contentDescriptor);
        return contentDescriptor;
    }

    private void addTimelineTab(TimelineClient client) {
        JPanel timeline = new JPanel();
        timeline.add(new JLabel("timeline goes here"));
        Content content = myUi.createContent(TIMELINE_CONTENT_ID, timeline, "Timeline", null, null);
        content.setCloseable(false);
        myUi.addContent(content, 0, PlaceInGrid.center, false);
    }

    private void addConsoleTab(RunProfile profile, RunContentDescriptor contentDescriptor) {
        final ExecutionConsole console = myExecutionResult.getExecutionConsole();
        Content content = myUi.createContent(ExecutionConsole.CONSOLE_CONTENT_ID, console.getComponent(),
                                             "Console",
                                             AllIcons.Debugger.Console,
                                             console.getPreferredFocusableComponent());
        content.setCloseable(false);
        myUi.addContent(content, 1, PlaceInGrid.bottom, false);
        if (profile instanceof RunConfigurationBase) {
            if (console instanceof ObservableConsoleView && !ApplicationManager.getApplication().isUnitTestMode()) {
                ((ObservableConsoleView)console).addChangeListener(
                        new RunContentBuilder.ConsoleToFrontListener((RunConfigurationBase)profile,
                                myProject, myEnvironment.getExecutor(), contentDescriptor, myUi),
                        this);
            }
        }
    }
}
