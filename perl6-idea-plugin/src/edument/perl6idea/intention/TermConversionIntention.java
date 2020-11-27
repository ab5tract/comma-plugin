package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6LongName;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.Perl6TypeName;
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
        if (elementType == Perl6TokenTypes.SUB_CALL_NAME && elementUnderCaret.textMatches("set")) {
            Perl6SubCall call = PsiTreeUtil.getParentOfType(elementUnderCaret, Perl6SubCall.class);
            if (call != null && call.getCallArguments().length == 0)
                return Operation.TO_UNICODE_SET;
        }
        else if (elementType == Perl6TokenTypes.NAME) {
            PsiElement nameHolder = elementUnderCaret.getParent();
            if (!(nameHolder instanceof Perl6LongName))
                return Operation.NONE;
            if (!(nameHolder.getParent() instanceof Perl6TypeName))
                return Operation.NONE;
            if (nameHolder.textMatches("pi"))
                return Operation.TO_UNICODE_PI;
            if (nameHolder.textMatches("tau"))
                return Operation.TO_UNICODE_TAU;
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
