package org.raku.repl;

import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.EditorUtil;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RakuReplHistoryKeyListener extends KeyAdapter {
    private final RakuReplConsole repl;
    private int historyPos = 0;
    private int prevCaretOffset = -1;
    private String unfinishedCommand = "";

    public RakuReplHistoryKeyListener(RakuReplConsole repl) {
        this.repl = repl;
        repl.getReplState().addNewHistoryListener(() -> {
            historyPos = repl.getReplState().getHistorySize();
            prevCaretOffset = -1;
            unfinishedCommand = "";
        });
    }

    private enum HistoryMove {
        UP, DOWN
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                moveHistoryCursor(HistoryMove.UP);
                break;
            case KeyEvent.VK_DOWN:
                moveHistoryCursor(HistoryMove.DOWN);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                EditorEx editor = repl.getConsoleView().getConsoleEditor();
                prevCaretOffset = editor.getCaretModel().getOffset();
                break;
        }
    }

    private void moveHistoryCursor(HistoryMove move) {
        // Approach taken here is thanks to the Kotlin REPL.
        RakuReplState state = repl.getReplState();

        // If there's no history or if auto-complete window is open, don't do
        // anything.
        if (state.getHistorySize() == 0)
            return;
        if (LookupManager.getInstance(repl.getProject()).getActiveLookup() != null)
            return;

        // Work out where we are now.
        EditorEx editor = repl.getConsoleView().getConsoleEditor();
        CaretModel caret = editor.getCaretModel();
        DocumentEx document = editor.getDocument();
        int curOffset = caret.getOffset();
        int curLine = document.getLineNumber(curOffset);
        int totalLines = document.getLineCount();
        boolean isMultiline = totalLines > 1;

        // Now process the movement.
        switch (move) {
            case UP:
                // If we're moving within the current editor, we're not moving through
                // history. Same if we're at the start of history.
                if (curLine != 0 || (isMultiline && prevCaretOffset != 0)) {
                    prevCaretOffset = curOffset;
                    return;
                }
                if (historyPos == 0)
                    return;

                // If we're moving away from the latest, unfinished, command,
                // we should remember it so we can reinstate it later.
                if (historyPos == state.getHistorySize()) {
                    unfinishedCommand = document.getText();
                }

                // Otherwise, we go back to the previous entry.
                historyPos--;
                WriteCommandAction.runWriteCommandAction(repl.getProject(), "Raku REPL", null, () -> {
                    document.setText(state.getExecutionText(historyPos));
                    EditorUtil.scrollToTheEnd(editor);
                    prevCaretOffset = 0;
                    caret.moveToOffset(0);
                });
                break;
            case DOWN:
                // If we're moving through the current multi-line document, or at
                // the end of history, nothing to do.
                if (curLine != totalLines - 1 || (isMultiline && prevCaretOffset != document.getTextLength())) {
                    prevCaretOffset = curOffset;
                    return;
                }
                if (historyPos == state.getHistorySize())
                    return;

                // Go forward in history. If we're at the end of history, then we
                // put in place the preserved unfinished command.
                historyPos++;
                WriteCommandAction.runWriteCommandAction(repl.getProject(), "Raku REPL", null, () -> {
                    document.setText(historyPos == state.getHistorySize()
                            ? unfinishedCommand
                            : state.getExecutionText(historyPos));
                    prevCaretOffset = document.getTextLength();
                    EditorUtil.scrollToTheEnd(editor);
                });
                break;
        }
    }
}
