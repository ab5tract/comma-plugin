package edument.perl6idea.repl;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.console.*;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.utils.Perl6CommandLine;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;

public class Perl6ReplConsole extends AbstractConsoleRunnerWithHistory<LanguageConsoleView> {
    private Perl6CommandLine commandLine;
    private Perl6ReplState replState;

    public Perl6ReplConsole(@NotNull Project project,
                            @NotNull String consoleTitle,
                            @Nullable String workingDir) {
        super(project, consoleTitle, workingDir);
        replState = new Perl6ReplState(this);
    }

    @Override
    protected LanguageConsoleView createConsoleView() {
        LanguageConsoleBuilder builder = new LanguageConsoleBuilder();
        builder.oneLineInput(false);
        LanguageConsoleView consoleView = builder
                .build(getProject(), Perl6Language.INSTANCE);

        EditorEx consoleEditor = consoleView.getConsoleEditor();
        addHint(consoleEditor);
        consoleEditor.getSettings().setCaretRowShown(true);
        consoleEditor.getContentComponent().addKeyListener(new Perl6ReplHistoryKeyListener(this));

        return consoleView;
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
        File replBackend = Perl6Utils.getResourceAsFile("repl/repl-backend.p6");
        commandLine = new Perl6CommandLine(getProject());
        commandLine.setWorkDirectory(getProject().getBasePath());
        commandLine.addParameter(replBackend.getAbsolutePath());
        return commandLine.createProcess();
    }

    @Override
    protected OSProcessHandler createProcessHandler(Process process) {
        return new Perl6ReplOutputHandler(process, commandLine.getCommandLineString(), this);
    }

    @NotNull
    @Override
    protected ProcessBackedConsoleExecuteActionHandler createExecuteActionHandler() {
        return new ProcessBackedConsoleExecuteActionHandler(getProcessHandler(), true) {
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

    public Perl6ReplState getReplState() {
        return replState;
    }
}
