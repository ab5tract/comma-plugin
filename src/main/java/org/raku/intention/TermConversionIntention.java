package org.raku.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.RakuLongName;
import org.raku.psi.RakuSubCall;
import org.raku.psi.RakuTypeName;
import org.jetbrains.annotations.NotNull;

public abstract class TermConversionIntention implements IntentionAction {
    protected enum Operation {
        NONE,
        TO_UNICODE_PI, TO_UNICODE_TAU, TO_UNICODE_SET,
        TO_ASCII_PI, TO_ASCII_TAU, TO_ASCII_SET
    }

    protected Operation classifyOperation(@NotNull Project project, Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementUnderCaret = file.findElementAt(offset);
        if (elementUnderCaret == null)
            return Operation.NONE;
        IElementType elementType = elementUnderCaret.getNode().getElementType();
        if (elementType == RakuTokenTypes.SUB_CALL_NAME && elementUnderCaret.textMatches("set")) {
            RakuSubCall call = PsiTreeUtil.getParentOfType(elementUnderCaret, RakuSubCall.class);
            if (call != null && call.getCallArguments().length == 0)
                return Operation.TO_UNICODE_SET;
        }
        else if (elementType == org.raku.parsing.RakuTokenTypes.NAME) {
            PsiElement nameHolder = elementUnderCaret.getParent();
            if (!(nameHolder instanceof RakuLongName))
                return Operation.NONE;
            if (!(nameHolder.getParent() instanceof RakuTypeName))
                return Operation.NONE;
            if (nameHolder.textMatches("pi"))
                return Operation.TO_UNICODE_PI;
            if (nameHolder.textMatches("tau"))
                return Operation.TO_UNICODE_TAU;
            if (nameHolder.textMatches("π"))
                return Operation.TO_ASCII_PI;
            if (nameHolder.textMatches("τ"))
                return Operation.TO_ASCII_TAU;
        }
        else if (elementType == org.raku.parsing.RakuTokenTypes.TERM) {
            if (elementUnderCaret.textMatches("∅"))
                return Operation.TO_ASCII_SET;
        }
        return Operation.NONE;
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return getText();
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
