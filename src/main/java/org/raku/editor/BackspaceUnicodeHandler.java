package org.raku.editor;

import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import org.raku.parsing.RakuTokenTypes;
import org.raku.utils.RakuOperatorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@InternalIgnoreDependencyViolation
public class BackspaceUnicodeHandler extends BackspaceHandlerDelegate {
    @Nullable
    private String stringToInsert = null;
    private static final Map<Character, String> replacements = new HashMap<>();

    static {
        for (Pair<Character, String> op : ContainerUtil.zip(RakuOperatorUtils.unicodeOperators, RakuOperatorUtils.asciiOperators)) {
            replacements.put(op.getFirst(), op.getSecond());
        }
    }

    @Override
    public void beforeCharDeleted(char c, @NotNull PsiFile file, @NotNull Editor editor) {
        Pair<Pair<Integer, Integer>, String> lastReplacement = editor.getUserData(UnicodeReplacementHandler.UNICODE_REPLACEMENT_POS);
        if (lastReplacement != null && editor.getCaretModel().getOffset() - 1 == lastReplacement.first.first) {
            IElementType curToken = editor.getHighlighter()
                .createIterator(editor.getCaretModel().getOffset() - 1).getTokenType();
            if (curToken == org.raku.parsing.RakuTokenTypes.INFIX || curToken == RakuTokenTypes.METAOP)
                stringToInsert = lastReplacement.second;
            else
                stringToInsert = null;
        }
        else {
            stringToInsert = null;
        }
    }

    @Override
    public boolean charDeleted(char c, @NotNull PsiFile file, @NotNull Editor editor) {
        if (!RakuOperatorUtils.isUnicodeConversionEnabled(editor))
            return false;
        Pair<Pair<Integer, Integer>, String> lastReplacement = editor.getUserData(UnicodeReplacementHandler.UNICODE_REPLACEMENT_POS);
        if (replacements.containsKey(c) && stringToInsert != null && lastReplacement != null) {
            if (lastReplacement.first.second != 0) {
                editor.getDocument().replaceString(lastReplacement.first.second, editor.getCaretModel().getOffset(), stringToInsert);
            } else {
                editor.getDocument().insertString(editor.getCaretModel().getOffset(), lastReplacement.second);
                editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() + stringToInsert.length());
            }
        }
        return false;
    }
}
