package edument.perl6idea.editor;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import edument.perl6idea.parsing.Perl6TokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

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
                lookup.performGuardedChange(() -> EditorModificationUtil.deleteSelectedText(editor));
            }
            return Result.STOP;
        }
        else if (sigilsAndTwigils.contains(charTyped) || sigils.contains(charTyped) || Character.isLetter(charTyped)) {
            int start = editor.getCaretModel().getOffset() - 1;
            HighlighterIterator iterator = ((EditorEx)editor).getHighlighter().createIterator(start);
            IElementType curToken = iterator.getTokenType();
            if (curToken == Perl6TokenTypes.VARIABLE || sigils.contains(charTyped)) {
                AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
                return Result.STOP;
            }
        }
        return Result.CONTINUE;
    }
}
