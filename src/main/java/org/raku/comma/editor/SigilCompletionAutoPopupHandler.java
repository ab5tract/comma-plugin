package org.raku.comma.editor;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtilEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.raku.comma.parsing.RakuTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@InternalIgnoreDependencyViolation
public class SigilCompletionAutoPopupHandler extends TypedHandlerDelegate {
    private static final Set<Character> sigilsAndTwigils;
    private static final Set<Character> sigils;

    static {
        sigils = new HashSet<>();
        sigils.add('$');
        sigils.add('@');
        sigils.add('%');
        sigils.add('&');
        sigilsAndTwigils = new HashSet<>();
        sigilsAndTwigils.add('!');
        sigilsAndTwigils.add('.');
        sigilsAndTwigils.add('*');
        sigilsAndTwigils.add('?');
        sigilsAndTwigils.add('=');
        sigilsAndTwigils.add(':');
        sigilsAndTwigils.add('<');
        sigilsAndTwigils.add('-');
        sigilsAndTwigils.add('_');
    }

    @NotNull
    @Override
    public Result checkAutoPopup(char charTyped, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        LookupImpl lookup = (LookupImpl) LookupManager.getActiveLookup(editor);
        if (lookup != null) {
            if (editor.getSelectionModel().hasSelection()) {
                lookup.performGuardedChange(() -> EditorModificationUtilEx.deleteSelectedText(editor));
            }
            return Result.STOP;
        }
        else if (sigilsAndTwigils.contains(charTyped) || sigils.contains(charTyped) || Character.isLetter(charTyped)) {
            IElementType curToken = getCurrentToken(editor);
            if (curToken == RakuTokenTypes.VARIABLE || sigils.contains(charTyped)) {
                AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
                return Result.STOP;
            }
        }
        else if (charTyped == '^') {
            IElementType curToken = getCurrentToken(editor);
            if (curToken == RakuTokenTypes.METHOD_CALL_OPERATOR) {
                AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
                return Result.STOP;
            }
        }
        return Result.CONTINUE;
    }

    @Nullable
    private static IElementType getCurrentToken(@NotNull Editor editor) {
        int start = editor.getCaretModel().getOffset() - 1;
        if (start < 0)
            return null;
        HighlighterIterator iterator = editor.getHighlighter().createIterator(start);
        return iterator.getTokenType();
    }
}
