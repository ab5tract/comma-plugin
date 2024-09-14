package org.raku.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuInfix;
import org.jetbrains.annotations.NotNull;

public class UseBindingToDestructureFix implements IntentionAction {
    private final RakuInfix myInfix;

    public UseBindingToDestructureFix(RakuInfix infix) {
        myInfix = infix;
    }

    @NotNull
    @Override
    public String getText() {
        return "Use binding to destructure";
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
        myInfix.getOperator().replace(RakuElementFactory.createInfixOperator(project, ":="));
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}