package edument.perl6idea.editor;

import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Blockoid;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StatementList;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.BLOCK_CURLY_BRACKET_CLOSE;
import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6StatementMover extends StatementUpDownMover {
    @Override
    public boolean checkAvailable(@NotNull Editor editor, @NotNull PsiFile file, @NotNull MoveInfo info, boolean down) {
        return file instanceof Perl6File;
    }

    @Override
    public void beforeMove(@NotNull Editor editor, @NotNull MoveInfo info, boolean down) {
        if (editor.getProject() == null || !(editor instanceof EditorEx)) return;
        EditorEx editorEx = (EditorEx) editor;
        final PsiFile psiFile = PsiManager.getInstance(editor.getProject()).findFile(editorEx.getVirtualFile());
        assert psiFile != null;

        if (editor.getSelectionModel().getSelectionStart() != editor.getSelectionModel().getSelectionEnd())
            return;

        PsiElement rangeElement1 = getNode(editor.getDocument(), info.toMove.startLine, psiFile);
        if (rangeElement1 == null) return;
        PsiElement rangeElement2 = getNode(editor.getDocument(), info.toMove2.startLine, psiFile);
        if (rangeElement2 == null) return;

        // For multi-line statements
        if (rangeElement1.equals(rangeElement2)) {
            PsiElement tempRange = skipEmpty(down ? rangeElement2.getNextSibling() : rangeElement1.getPrevSibling(), down);
            if (tempRange == null) {
                info.toMove2 = info.toMove;
                return;
            }
            rangeElement2 = tempRange;
        }

        LineRange lineRange1 = new LineRange(rangeElement1);
        LineRange lineRange2 = new LineRange(rangeElement2);

        info.toMove = lineRange1;
        info.toMove2 = lineRange2;
    }

    private static PsiElement getNode(@NotNull Document document, int startOffset, PsiFile psiFile) {
        PsiElement element = skipEmpty(psiFile.findElementAt(getLineStartSafeOffset(document, startOffset)), true);
        if (element == null) return null;
        if (element.getNode().getElementType().equals(BLOCK_CURLY_BRACKET_CLOSE)) return element;
        if (element instanceof Perl6StatementList && element.getParent() instanceof Perl6Blockoid)
            element = element.getFirstChild();
        return element instanceof Perl6Statement ? element : PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
    }

    private static PsiElement skipEmpty(PsiElement node, boolean toRight) {
        PsiElement temp = node;
        while (temp != null && (temp instanceof PsiWhiteSpace || temp.getNode().getElementType().equals(UNV_WHITE_SPACE)))
            temp = toRight ? temp.getNextSibling() : temp.getPrevSibling();
        return temp == null ? node : temp;
    }

    @Override
    public void afterMove(@NotNull Editor editor, @NotNull PsiFile file, @NotNull MoveInfo info, boolean down) {
    }
}
