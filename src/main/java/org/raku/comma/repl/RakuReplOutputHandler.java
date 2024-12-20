package org.raku.comma.repl;

import com.intellij.execution.console.LanguageConsoleView;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessOutputType;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.FoldingModelEx;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.Key;
import com.intellij.ui.JBColor;
import com.intellij.util.io.BaseOutputReader;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RakuReplOutputHandler extends OSProcessHandler {
    private static final Key<ConsoleViewContentType> SORRY_HEADER = Key.create("raku.repl.out.sorryHeader");
    private static final Key<ConsoleViewContentType> PRE_CODE = Key.create("raku.repl.out.preCode");
    private static final Key<ConsoleViewContentType> EJECT_MARKER = Key.create("raku.repl.out.ejectMarker");
    private static final Key<ConsoleViewContentType> POST_CODE = Key.create("raku.repl.out.postCode");

    static {
        TextAttributesKey sorryHeaderAttrsKey = TextAttributesKey.createTempTextAttributesKey(
            SORRY_HEADER.toString(), new TextAttributes(JBColor.RED, null, null, null, Font.BOLD));
        ConsoleViewContentType.registerNewConsoleViewType(SORRY_HEADER,
                TextAttributesKey.createTextAttributesKey(SORRY_HEADER.toString(), sorryHeaderAttrsKey));

        TextAttributesKey preCodeAttrsKey = TextAttributesKey.createTempTextAttributesKey(
            PRE_CODE.toString(), new TextAttributes(JBColor.GREEN, null, null, null, Font.BOLD));
        ConsoleViewContentType.registerNewConsoleViewType(PRE_CODE,
                TextAttributesKey.createTextAttributesKey(PRE_CODE.toString(), preCodeAttrsKey));

        TextAttributesKey ejectMarkerAttrsKey = TextAttributesKey.createTempTextAttributesKey(
            EJECT_MARKER.toString(), new TextAttributes(JBColor.YELLOW, null, null, null, Font.BOLD));
        ConsoleViewContentType.registerNewConsoleViewType(EJECT_MARKER,
                TextAttributesKey.createTextAttributesKey(EJECT_MARKER.toString(), ejectMarkerAttrsKey));

        TextAttributesKey postCodeAttrsKey = TextAttributesKey.createTempTextAttributesKey(
            POST_CODE.toString(), new TextAttributes(JBColor.RED, null, null, null, Font.BOLD));
        ConsoleViewContentType.registerNewConsoleViewType(POST_CODE,
                TextAttributesKey.createTextAttributesKey(POST_CODE.toString(), postCodeAttrsKey));
    }

    private enum SpecialOutputKind { None, CompileError, RuntimeError }

    private final RakuReplConsole repl;
    private final StringBuilder buffer;
    private SpecialOutputKind specialOutputKind;
    private final List<String> specialOutputLines;
    private boolean sawFirstSystemOutputLine;

    public RakuReplOutputHandler(@NotNull Process process, String commandLine, RakuReplConsole repl) {
        super(process, commandLine);
        this.repl = repl;
        this.buffer = new StringBuilder();
        this.specialOutputKind = SpecialOutputKind.None;
        this.specialOutputLines = new ArrayList<>();
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
        else if (outputType == ProcessOutputType.SYSTEM) {
            if (sawFirstSystemOutputLine)
                super.notifyTextAvailable(text, outputType);
            else
                sawFirstSystemOutputLine = true;
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
                repl.replState.markLatestCompiledOk();
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

    private record CompileError(int line, String pre, String post, String message) {
    }

    private void emitError() {
        if (specialOutputKind == SpecialOutputKind.CompileError && specialOutputLines.size() >= 4) {
            int outputLineIdx = 0;
            int numErrors = Integer.parseInt(specialOutputLines.get(outputLineIdx++));
            CompileError[] errors = new CompileError[numErrors];
            for (int i = 0; i < numErrors; i++) {
                int line = Integer.parseInt(specialOutputLines.get(outputLineIdx++));
                String pre = specialOutputLines.get(outputLineIdx++);
                String post = specialOutputLines.get(outputLineIdx++);
                StringBuilder message = new StringBuilder();
                while (true) {
                    String messagePart = specialOutputLines.get(outputLineIdx++);
                    if (messagePart.equals("\u0001 ERROR-SPLIT"))
                        break;
                    message.append(messagePart);
                }
                errors[i] = new CompileError(line, pre, post, message.toString());
            }
            ApplicationManager.getApplication().invokeAndWait(() -> emitCompileErrors(errors));
        }
        else if (specialOutputKind == SpecialOutputKind.RuntimeError) {
            // Collect backtrace lines and message lines.
            boolean inMessage = false;
            List<String> backtraceLines = new ArrayList<>();
            List<List<String>> awaitBacktraces = new ArrayList<>();
            StringBuilder message = new StringBuilder();
            for (String line : specialOutputLines) {
                if (inMessage) {
                    message.append(line).append("\n");
                }
                else if (line.equals("\u0001 AWAIT-BACKTRACE-END")) {
                    awaitBacktraces.add(backtraceLines);
                    backtraceLines = new ArrayList<>();
                }
                else if (line.equals("---")) {
                    inMessage = true;
                }
                else {
                    backtraceLines.add(line);
                }
            }
            final String finalMessage = message.toString();
            final List<String> finalBacktrace = backtraceLines;
            ApplicationManager.getApplication().invokeAndWait(() -> emitRuntimeError(finalMessage,
                    finalBacktrace, awaitBacktraces));
        }
        else {
            // Confused, just pass the output up.
            for (String line : specialOutputLines) {
                super.notifyTextAvailable(line + "\n", ProcessOutputType.STDERR);
            }
        }
        specialOutputLines.clear();
    }

    private void emitCompileErrors(CompileError[] errors) {
        LanguageConsoleView view = repl.getConsoleView();
        view.print("===", ConsoleViewContentType.getConsoleViewType(SORRY_HEADER));
        view.print("SORRY", ConsoleViewContentType.NORMAL_OUTPUT);
        view.print("===\n", ConsoleViewContentType.getConsoleViewType(SORRY_HEADER));
        for (CompileError error : errors) {
            view.print(error.message + "\nat evaluation line " + error.line + "\n",
                       ConsoleViewContentType.NORMAL_OUTPUT);
            if (!error.pre.isEmpty() && !error.post.isEmpty()) {
                view.print("------> ", ConsoleViewContentType.NORMAL_OUTPUT);
                view.print(error.pre, ConsoleViewContentType.getConsoleViewType(PRE_CODE));
                view.print("⏏", ConsoleViewContentType.getConsoleViewType(EJECT_MARKER));
                view.print(error.post + "\n", ConsoleViewContentType.getConsoleViewType(POST_CODE));
            }
        }
    }

    private void emitRuntimeError(String message, List<String> backtrace, List<List<String>> awaitBacktraces) {
        // Add the error.
        LanguageConsoleView view = repl.getConsoleView();
        view.print(message, ConsoleViewContentType.ERROR_OUTPUT);
        view.performWhenNoDeferredOutput(() -> {
            // Add backtrace and fold it.
            EditorEx historyViewer = view.getHistoryViewer();
            int startFolding = historyViewer.getDocument().getTextLength();
            view.print(String.join("\n", backtrace) + "\n", ConsoleViewContentType.ERROR_OUTPUT);
            for (List<String> awaitBacktrace : awaitBacktraces) {
                view.print("Awaited at:\n", ConsoleViewContentType.ERROR_OUTPUT);
                view.print(String.join("\n", awaitBacktrace) + "\n", ConsoleViewContentType.ERROR_OUTPUT);
            }
            if (backtrace.size() > 1 || awaitBacktraces.size() > 0) {
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

    @NotNull
    @Override
    protected BaseOutputReader.Options readerOptions() {
        return BaseOutputReader.Options.forMostlySilentProcess();
    }
}
