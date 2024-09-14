package org.raku.comma.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuPostfixApplication;
import org.raku.comma.psi.RakuStatement;
import org.jetbrains.annotations.NotNull;

public class UseExecutableDynamicVariableFix implements IntentionAction {
    private final PsiElement strLiteral;

    public UseExecutableDynamicVariableFix(PsiElement element) {
        strLiteral = element;
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Use $*EXECUTABLE.absolute";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        RakuStatement executableCall = RakuElementFactory.createStatementFromText(project, "$*EXECUTABLE.absolute");
        RakuPostfixApplication postfix = PsiTreeUtil.findChildOfType(executableCall, RakuPostfixApplication.class);
        if (postfix != null)
            strLiteral.replace(postfix);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
