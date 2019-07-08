package edument.perl6idea.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.runners.RunTab;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.execution.ui.ObservableConsoleView;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManagerImpl;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.PinActiveTabAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A RunTab subclass that offers a UI run tab configuration when the user has two tabs, a Console and a custom pane (visible by default)
 * It uses some parts of Java's `RunContentBuilder` implementation that are not generic enough and cannot be re-used easily,
 */
public abstract class CustomConsoleRunTab extends RunTab {
    protected final ExecutionResult myExecutionResult;

    protected CustomConsoleRunTab(@NotNull ExecutionEnvironment environment,
                                  ExecutionResult executionResult,
                                  @NotNull String runnerType) {
        super(environment.getProject(), GlobalSearchScope.allScope(environment.getProject()),
              environment.getRunner().getRunnerId(), runnerType,
              environment.getRunProfile().getName());
        myExecutionResult = executionResult;
        myEnvironment = environment;
    }

    public RunContentDescriptor showRunContent(@Nullable RunContentDescriptor reuseContent,
                                               Object uiUpdater) throws ExecutionException {
        myUi.getDefaults()
            .initTabDefaults(0, getCustomTabText(), null)
            .initTabDefaults(1, "Console", null);
        RunContentDescriptor descriptor = createDescriptor(uiUpdater);
        Disposer.register(descriptor, this);
        Disposer.register(myProject, descriptor);
        RunContentManagerImpl.copyContentAndBehavior(descriptor, reuseContent);
        myRunContentDescriptor = descriptor;
        return descriptor;
    }

    protected abstract String getCustomTabText();

    @NotNull
    protected RunContentDescriptor createDescriptor(Object uiUpdater) throws ExecutionException {
        final RunProfile profile = myEnvironment.getRunProfile();
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return new RunContentDescriptor(profile, myExecutionResult, myUi);
        }
        RunContentDescriptor contentDescriptor = new RunContentDescriptor(profile, myExecutionResult, myUi);
        addCustomTab(uiUpdater);
        addConsoleTab(profile, contentDescriptor);
        return contentDescriptor;
    }

    protected abstract void addCustomTab(Object uiUpdater) throws ExecutionException;

    protected void addConsoleTab(RunProfile profile, RunContentDescriptor contentDescriptor) {
        final ExecutionConsole console = myExecutionResult.getExecutionConsole();
        Content content = myUi.createContent(ExecutionConsole.CONSOLE_CONTENT_ID, console.getComponent(),
                                             "Console",
                                             AllIcons.Debugger.Console,
                                             console.getPreferredFocusableComponent());
        content.setCloseable(false);
        myUi.addContent(content, 1, PlaceInGrid.bottom, false);
        myUi.getOptions().setLeftToolbar(createActionToolbar(contentDescriptor), ActionPlaces.RUNNER_TOOLBAR);
        if (profile instanceof RunConfigurationBase) {
            if (console instanceof ObservableConsoleView && !ApplicationManager.getApplication().isUnitTestMode()) {
                ((ObservableConsoleView)console).addChangeListener(
                    new RunContentBuilder.ConsoleToFrontListener(
                        (RunConfigurationBase)profile,
                        myProject, myEnvironment.getExecutor(), contentDescriptor, myUi), this);
            }
        }
    }

    @NotNull
    protected ActionGroup createActionToolbar(@NotNull final RunContentDescriptor contentDescriptor) {
        final DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(ActionManager.getInstance().getAction(IdeActions.ACTION_RERUN));
        final AnAction[] actions = contentDescriptor.getRestartActions();
        actionGroup.addAll(actions);
        if (actions.length > 0) {
            actionGroup.addSeparator();
        }

        actionGroup.add(ActionManager.getInstance().getAction(IdeActions.ACTION_STOP_PROGRAM));
        actionGroup.addAll(myExecutionResult.getActions());

        actionGroup.addSeparator();
        actionGroup.add(myUi.getOptions().getLayoutActions());
        actionGroup.addSeparator();
        actionGroup.add(new PinActiveTabAction());
        return actionGroup;
    }
}
