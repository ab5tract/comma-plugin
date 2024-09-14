package org.raku.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.RakuMethodCall;
import org.raku.psi.RakuPostfixApplication;
import org.jetbrains.annotations.NotNull;

public class GrepFirstFix implements IntentionAction {
    private final RakuMethodCall myGrepCall;
    private final RakuMethodCall myFirstCall;

    public GrepFirstFix(RakuMethodCall element, RakuMethodCall postfix) {
        myGrepCall = element;
        myFirstCall = postfix;
    }

    @NotNull
    @Override
    public String getText() {
        return "Replace .grep.first with .first";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        myGrepCall.setName("first");
        RakuPostfixApplication postfixApp = PsiTreeUtil.getParentOfType(myGrepCall, RakuPostfixApplication.class);
        if (postfixApp != null) {
            PsiElement innerCall = postfixApp.copy();
            RakuPostfixApplication outerCall = PsiTreeUtil.getParentOfType(myFirstCall, RakuPostfixApplication.class);
            if (outerCall != null)
                outerCall.replace(innerCall);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
