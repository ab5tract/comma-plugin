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

import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6StatementMover extends StatementUpDownMover {
    private int offset = 0;

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

        PsiElement rangeElement1 = getNode(editor.getDocument(), info.toMove.startLine, psiFile);
        if (rangeElement1 == null) return;
        PsiElement rangeElement2 = getNode(editor.getDocument(), info.toMove2.startLine, psiFile);
        if (rangeElement2 == null) return;

        if (rangeElement1.equals(rangeElement2)) {
            PsiElement tempRange = skipEmpty(down ? rangeElement2.getNextSibling() : rangeElement1.getPrevSibling(), down);
            if (tempRange == null) {
                info.toMove2 = info.toMove;
                return;
            }
            if (down)
                rangeElement2 = tempRange;
            else
                rangeElement1 = tempRange;
        }

        LineRange lineRange1 = new LineRange(rangeElement1);
        LineRange lineRange2 = new LineRange(rangeElement2);

        if (down) {
            int begin2 = countStart(new LineRange(rangeElement2).startLine, editor.getDocument(), psiFile);
            int indent = begin2 - getLineStartSafeOffset(editor.getDocument(), new LineRange(rangeElement2).startLine);
            int size2 = indent + rangeElement2.getTextLength() + 1;
            offset = editor.getCaretModel().getOffset() + size2;
        } else {
            if (lineRange1.startLine > lineRange2.startLine) {
                PsiElement temp = rangeElement1;
                rangeElement1 = rangeElement2;
                rangeElement2 = temp;
            }
            int size1 = countStart(new LineRange(rangeElement1).startLine, editor.getDocument(), psiFile);
            int begin2 = countStart(new LineRange(rangeElement2).startLine, editor.getDocument(), psiFile);
            int rel = editor.getCaretModel().getOffset() - begin2;
            offset = size1 + rel;
        }

        info.toMove = lineRange1;
        info.toMove2 = lineRange2;
    }

    private static int countStart(int startLine, Document doc, PsiFile file) {
        int zero = getLineStartSafeOffset(doc, startLine);
        PsiElement temp = file.findElementAt(zero);
        while (temp != null && (temp instanceof PsiWhiteSpace || temp.getNode().getElementType().equals(UNV_WHITE_SPACE))) {
            zero += temp.getTextLength();
            temp = temp.getNextSibling();
        }
        return zero;
    }

    private static PsiElement getNode(@NotNull Document document, int startOffset, PsiFile psiFile) {
        PsiElement element = skipEmpty(psiFile.findElementAt(getLineStartSafeOffset(document, startOffset)), true);
        if (element == null) return null;
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
        if (offset != 0)
            editor.getCaretModel().moveToOffset(offset);
    }
}
