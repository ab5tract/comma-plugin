package edument.perl6idea.repl;

import com.intellij.execution.console.LanguageConsoleView;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessOutputType;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.FoldingModelEx;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.Key;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Perl6ReplOutputHandler extends OSProcessHandler {
    private static Key<ConsoleViewContentType> SORRY_HEADER = Key.create("perl6.repl.out.sorryHeader");
    private static Key<ConsoleViewContentType> PRE_CODE = Key.create("perl6.repl.out.preCode");
    private static Key<ConsoleViewContentType> EJECT_MARKER = Key.create("perl6.repl.out.ejectMarker");
    private static Key<ConsoleViewContentType> POST_CODE = Key.create("perl6.repl.out.postCode");

    static {
        TextAttributes sorryHeaderAttrs = new TextAttributes(JBColor.RED, null, null, null, Font.BOLD);
        ConsoleViewContentType.registerNewConsoleViewType(SORRY_HEADER,
                TextAttributesKey.createTextAttributesKey(SORRY_HEADER.toString(), sorryHeaderAttrs));

        TextAttributes preCodeAttrs = new TextAttributes(JBColor.GREEN, null, null, null, Font.BOLD);
        ConsoleViewContentType.registerNewConsoleViewType(PRE_CODE,
                TextAttributesKey.createTextAttributesKey(PRE_CODE.toString(), preCodeAttrs));

        TextAttributes ejectMarkerAttrs = new TextAttributes(JBColor.YELLOW, null, null, null, Font.BOLD);
        ConsoleViewContentType.registerNewConsoleViewType(EJECT_MARKER,
                TextAttributesKey.createTextAttributesKey(EJECT_MARKER.toString(), ejectMarkerAttrs));

        TextAttributes postCodeAttrs = new TextAttributes(JBColor.RED, null, null, null, Font.BOLD);
        ConsoleViewContentType.registerNewConsoleViewType(POST_CODE,
                TextAttributesKey.createTextAttributesKey(POST_CODE.toString(), postCodeAttrs));
    }

    private enum SpecialOutputKind { None, CompileError, RuntimeError };

    private final Perl6ReplConsole repl;
    private final StringBuilder buffer;
    private SpecialOutputKind specialOutputKind;
    private final List<String> specialOutputLines;

    public Perl6ReplOutputHandler(@NotNull Process process, String commandLine, Perl6ReplConsole repl) {
        super(process, commandLine);
        this.repl = repl;
        this.buffer = new StringBuilder();
        this.specialOutputKind = SpecialOutputKind.None;
        this.specialOutputLines = new ArrayList<>();

        LanguageConsoleView view = repl.getConsoleView();

    }

    @Override
    public void notifyTextAvailable(@NotNull String text, @NotNull Key outputType) {
        if (outputType == ProcessOutputType.STDERR) {
            buffer.append(text);
            if (buffer.charAt(buffer.length() - 1) == '\n') {
                processErrorBuffer(buffer.toString());
                buffer.setLength(0);
            }
        }
        else {
            super.notifyTextAvailable(text, outputType);
        }
    }

    private void processErrorBuffer(String string) {
        for (String line : string.split("\n")) {
            if (line.equals("\u0001 COMPILE-ERROR-START")) {
                specialOutputKind = SpecialOutputKind.CompileError;
            }
            else if (line.equals("\u0001 RUNTIME-ERROR-START")) {
                specialOutputKind = SpecialOutputKind.RuntimeError;
            }
            else if (line.equals("\u0001 ERROR-END")) {
                emitError();
                specialOutputKind = SpecialOutputKind.None;
            }
            else if (line.equals("\u0001 COMPILED-OK")) {
                repl.getReplState().markLatestCompiledOk();
            }
            else if (specialOutputKind == SpecialOutputKind.None) {
                // It's just normal stderr output; pass it on for default
                // handling.
                super.notifyTextAvailable(line + "\n", ProcessOutputType.STDERR);
            }
            else {
                // It's special output; collect it.
                specialOutputLines.add(line);
            }
        }
    }

    private void emitError() {
        if (specialOutputKind == SpecialOutputKind.CompileError && specialOutputLines.size() >= 4) {
            int line = Integer.parseInt(specialOutputLines.get(0));
            String pre = specialOutputLines.get(1);
            String post = specialOutputLines.get(2);
            String message = specialOutputLines.stream().skip(3).collect(Collectors.joining());
            ApplicationManager.getApplication().invokeAndWait(() -> emitCompileError(line, pre, post, message));
        }
        else if (specialOutputKind == SpecialOutputKind.RuntimeError) {
            // Collect backtrace lines and message lines.
            boolean inMessage = false;
            List<String> backtraceLines = new ArrayList<>();
            String message = "";
            for (String line : specialOutputLines) {
                if (inMessage) {
                    message += line + "\n";
                }
                else if (line.equals("---")) {
                    inMessage = true;
                }
                else {
                    backtraceLines.add(line);
                }
            }
            final String javaSucks = message;
            ApplicationManager.getApplication().invokeAndWait(() -> emitRuntimeError(javaSucks, backtraceLines));
        }
        else {
            // Confused, just pass the output up.
            for (String line : specialOutputLines) {
                super.notifyTextAvailable(line + "\n", ProcessOutputType.STDERR);
            }
        }
        specialOutputLines.clear();
    }

    private void emitCompileError(int line, String pre, String post, String message) {
        LanguageConsoleView view = repl.getConsoleView();
        view.print("===", ConsoleViewContentType.getConsoleViewType(SORRY_HEADER));
        view.print("SORRY", ConsoleViewContentType.NORMAL_OUTPUT);
        view.print("===\n", ConsoleViewContentType.getConsoleViewType(SORRY_HEADER));
        view.print(message + "\nat evaluation line " + line + "\n",
                   ConsoleViewContentType.NORMAL_OUTPUT);
        if (!pre.isEmpty() && !post.isEmpty()) {
            view.print("------> ", ConsoleViewContentType.NORMAL_OUTPUT);
            view.print(pre, ConsoleViewContentType.getConsoleViewType(PRE_CODE));
            view.print("⏏", ConsoleViewContentType.getConsoleViewType(EJECT_MARKER));
            view.print(post + "\n", ConsoleViewContentType.getConsoleViewType(POST_CODE));
        }
    }

    private void emitRuntimeError(String message, List<String> backtrace) {
        // Add the error.
        LanguageConsoleView view = repl.getConsoleView();
        view.print(message, ConsoleViewContentType.ERROR_OUTPUT);
        view.performWhenNoDeferredOutput(() -> {
            // Add backtrace and fold it.
            EditorEx historyViewer = view.getHistoryViewer();
            int startFolding = historyViewer.getDocument().getTextLength();
            view.print(String.join("\n", backtrace) + "\n", ConsoleViewContentType.ERROR_OUTPUT);
            if (backtrace.size() > 1) {
                view.performWhenNoDeferredOutput(() -> {
                    int endFolding = historyViewer.getDocument().getTextLength() - 1;
                    FoldingModelEx folding = historyViewer.getFoldingModel();
                    folding.runBatchFoldingOperation(() -> {
                        FoldRegion region = folding.addFoldRegion(startFolding, endFolding, backtrace.get(0));
                        if (region != null)
                            region.setExpanded(false);
                    });
                });
            }
        });
    }
}
