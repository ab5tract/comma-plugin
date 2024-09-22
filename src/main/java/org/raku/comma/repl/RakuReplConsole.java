package org.raku.comma.repl;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.console.*;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import org.raku.comma.RakuLanguage;
import org.raku.comma.utils.RakuCommandLine;
import org.raku.comma.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RakuReplConsole extends AbstractConsoleRunnerWithHistory<LanguageConsoleView> {
    private RakuCommandLine commandLine;
    private final RakuReplState replState;
    private File myReplBackendFile;

    public RakuReplConsole(@NotNull Project project,
                           @NotNull String consoleTitle,
                           @Nullable String workingDir) {
        super(project, consoleTitle, workingDir);
        replState = new RakuReplState(this);
    }

    @Override
    protected LanguageConsoleView createConsoleView() {
        LanguageConsoleBuilder builder = new LanguageConsoleBuilder();
        builder.oneLineInput(false);
        LanguageConsoleView consoleView = builder
            .build(getProject(), RakuLanguage.INSTANCE);

        EditorEx consoleEditor = consoleView.getConsoleEditor();
        addHint(consoleEditor);
        consoleEditor.getSettings().setCaretRowShown(true);
        consoleEditor.getContentComponent().addKeyListener(new RakuReplHistoryKeyListener(this));

        return consoleView;
    }

    @Override
    protected void finishConsole() {
        super.finishConsole();
        myReplBackendFile.delete();
    }

    private static void addHint(EditorEx editor) {
        AnAction executeCommandAction = ActionManager.getInstance().getAction("Perl6ReplExecute");
        String executeCommandActionShortcutText = KeymapUtil.getFirstKeyboardShortcutText(executeCommandAction);
        editor.setPlaceholder("<" + executeCommandActionShortcutText + "> to execute");
        editor.setShowPlaceholderWhenFocused(true);

        TextAttributes placeholderAttrs = new TextAttributes();
        placeholderAttrs.setForegroundColor(JBColor.LIGHT_GRAY);
        placeholderAttrs.setFontType(Font.ITALIC);
        editor.setPlaceholderAttributes(placeholderAttrs);
    }

    @Nullable
    @Override
    protected Process createProcess() throws ExecutionException {
        myReplBackendFile = RakuUtils.getResourceAsFile("repl/repl-backend.raku");
        commandLine = new RakuCommandLine(getProject());
        commandLine.setWorkDirectory(getProject().getBasePath());
        commandLine.addParameter("-I.");
        commandLine.addParameter(myReplBackendFile.getAbsolutePath());
        return commandLine.createProcess();
    }

    @Override
    protected OSProcessHandler createProcessHandler(Process process) {
        return new RakuReplOutputHandler(process, commandLine.getCommandLineString(), this);
    }

    @NotNull
    @Override
    protected ProcessBackedConsoleExecuteActionHandler createExecuteActionHandler() {
        return new ProcessBackedConsoleExecuteActionHandler(getProcessHandler(), true) {
            @Override
            protected void beforeExecution(@NotNull LanguageConsoleView view) {
                // Ensure that the current REPL document ends in a newline before adding the text of
                // the command.
                String currentText = view.getHistoryViewer().getDocument().getText();
                if (!currentText.endsWith("\n"))
                    view.print("\n", ConsoleViewContentType.NORMAL_OUTPUT);
                ((ConsoleViewImpl)view).flushDeferredText();

                super.beforeExecution(view);
            }

            @Override
            public void processLine(@NotNull String line) {
                // Wrap in envelope for REPL backend.
                String[] lines = line.split("\n");
                sendText("EVAL " + lines.length + "\n" +
                         String.join("\n", lines) + "\n");

                // Add this line to the history, (used for history and auto-complete).
                replState.addExecuted(line);
            }
        };
    }

    @Override
    protected AnAction createConsoleExecAction(@NotNull ProcessBackedConsoleExecuteActionHandler consoleExecuteActionHandler) {
        return new ConsoleExecuteAction(getConsoleView(), consoleExecuteActionHandler, "Perl6ReplExecute", consoleExecuteActionHandler);
    }

    @Override
    protected java.util.List<AnAction> fillToolBarActions(final DefaultActionGroup toolbarActions,
                                                          final Executor defaultExecutor,
                                                          final RunContentDescriptor contentDescriptor) {
        List<AnAction> actionList = new ArrayList<>();
        actionList.add(createCloseAction(defaultExecutor, contentDescriptor));
        actionList.add(createConsoleExecAction(getConsoleExecuteActionHandler()));
        toolbarActions.addAll(actionList);
        return actionList;
    }

    public RakuReplState getReplState() {
        return replState;
    }

    public void executeStatement(String command) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            getConsoleView().getEditorDocument().setText(command);
            getConsoleExecuteActionHandler().runExecuteAction(getConsoleView());
        });
    }
}
