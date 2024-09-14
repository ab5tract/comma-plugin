package org.raku.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuWhileStatement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class IdiomaticLoopFix implements IntentionAction {
    private final RakuWhileStatement myWhileStatement;

    public IdiomaticLoopFix(RakuWhileStatement whileStatement) {
        this.myWhileStatement = whileStatement;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Use idiomatic 'loop' syntax";
    }

    @Nls
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
        PsiElement loop = RakuElementFactory.createLoop(project, myWhileStatement.getBlock());
        myWhileStatement.replace(loop);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
