package org.raku.comma.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.parsing.RakuElementTypes;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.RakuHyperMetaOp;
import org.raku.comma.utils.RakuOperatorUtils;
import org.jetbrains.annotations.NotNull;

public abstract class OperatorConverterIndention implements IntentionAction {
    enum OperatorResult {
        ASCII, UNICODE, NONE
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return getText();
    }

    protected static @NotNull OperatorResult canBeConverted(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementUnderCaret = file.findElementAt(offset);
        if (elementUnderCaret == null)
            return OperatorResult.NONE;
        IElementType elementType = elementUnderCaret.getNode().getElementType();
        if (elementType == RakuTokenTypes.INFIX || elementType == RakuTokenTypes.METAOP) {
            // If METAOP with infix is incomplete - ignore it
            if (elementType == RakuTokenTypes.METAOP) {
                if (elementUnderCaret.getParent().getLastChild().getNode().getElementType() == RakuElementTypes.INFIX)
                    return OperatorResult.NONE;
            }
            String text = elementUnderCaret.getText();
            for (char uniOp : RakuOperatorUtils.unicodeOperators)
                if (text.equals(String.valueOf(uniOp)))
                    return OperatorResult.UNICODE;
            for (String asciiOp : RakuOperatorUtils.asciiOperators)
                if (text.equals(asciiOp))
                    return OperatorResult.ASCII;
        }
        return OperatorResult.NONE;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementUnderCaret = file.findElementAt(offset);
        assert elementUnderCaret != null;
        String text = elementUnderCaret.getText();
        String oppositeOp = null;
        for (Pair<Character, String> opPair : ContainerUtil.zip(RakuOperatorUtils.unicodeOperators, RakuOperatorUtils.asciiOperators)) {
            if (String.valueOf(opPair.first).equals(text)) {
                oppositeOp = opPair.second;
                break;
            }
            if (opPair.second.equals(text)) {
                oppositeOp = String.valueOf(opPair.first);
                break;
            }
        }
        if (oppositeOp == null)
            throw new IncorrectOperationException("Incorrect operator");
        Document document = editor.getDocument();
        if (elementUnderCaret.getNode().getElementType() == RakuTokenTypes.METAOP &&
            (elementUnderCaret.getNextSibling() != null && elementUnderCaret.getNextSibling().getNode().getElementType() == RakuElementTypes.INFIX
                        || elementUnderCaret.getPrevSibling() != null && elementUnderCaret.getPrevSibling().getNode().getElementType() == RakuElementTypes.INFIX)) {
            RakuHyperMetaOp fullOp = (RakuHyperMetaOp) elementUnderCaret.getParent();
            // We first change last one, then first one,
            // because otherwise range of the end op will change with
            // the first one being rewritten into something shorter/longer
            PsiElement endOp = fullOp.getLastChild();
            document.replaceString(endOp.getTextRange().getStartOffset(), endOp.getTextRange().getEndOffset(), getMetaReplacer(endOp.getText()));
            PsiElement startOp = fullOp.getFirstChild();
            document.replaceString(startOp.getTextRange().getStartOffset(), startOp.getTextRange().getEndOffset(), getMetaReplacer(startOp.getText()));
        } else {
            TextRange elementRange = elementUnderCaret.getTextRange();
            document.replaceString(elementRange.getStartOffset(), elementRange.getEndOffset(), oppositeOp);
        }
    }

    private static CharSequence getMetaReplacer(String text) {
        if (text.equals("<<")) {
            return "«";
        } else if (text.equals(">>")) {
            return "»";
        } else if (text.equals("»")) {
            return ">>";
        } else {
            return "<<";
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
