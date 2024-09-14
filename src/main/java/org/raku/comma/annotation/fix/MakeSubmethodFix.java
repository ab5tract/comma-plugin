package org.raku.comma.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuRoutineDecl;
import org.jetbrains.annotations.NotNull;

public class MakeSubmethodFix implements IntentionAction {
    private final RakuRoutineDecl myDecl;

    public MakeSubmethodFix(RakuRoutineDecl decl) {
        myDecl = decl;
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Make submethod";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement submethodDeclarator = RakuElementFactory.createRoutineDeclarator(project, "submethod");
        myDecl.getDeclaratorNode().replace(submethodDeclarator);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
