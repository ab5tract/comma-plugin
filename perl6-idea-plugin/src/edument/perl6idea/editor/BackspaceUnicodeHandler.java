package edument.perl6idea.editor;

import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.utils.Perl6OperatorUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BackspaceUnicodeHandler extends BackspaceHandlerDelegate {
    private boolean shouldRemove = false;
    private static final Map<Character, String> replacements = new HashMap<>();

    static {
        for (Pair<Character, String> op : ContainerUtil.zip(Perl6OperatorUtils.unicodeOperators, Perl6OperatorUtils.asciiOperators)) {
            replacements.put(op.getFirst(), op.getSecond());
        }
    }

    @Override
    public void beforeCharDeleted(char c, @NotNull PsiFile file, @NotNull Editor editor) {
        Integer lastReplacementPos = editor.getUserData(UnicodeReplacementHandler.UNICODE_REPLACEMENT_POS);
        if (lastReplacementPos != null && editor.getCaretModel().getOffset() == lastReplacementPos) {
            IElementType curToken = ((EditorEx)editor).getHighlighter()
                .createIterator(editor.getCaretModel().getOffset() - 1).getTokenType();
            shouldRemove = curToken == Perl6TokenTypes.INFIX || curToken == Perl6TokenTypes.METAOP;
        }
        else {
            shouldRemove = false;
        }
    }

    @Override
    public boolean charDeleted(char c, @NotNull PsiFile file, @NotNull Editor editor) {
        if (!Perl6OperatorUtils.isUnicodeConversionEnabled(editor))
            return false;
        if (replacements.containsKey(c) && shouldRemove) {
            String replacer = replacements.get(c);
            editor.getDocument().insertString(editor.getCaretModel().getOffset(), replacer);
            editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() + replacer.length());
        }
        return false;
    }
}
