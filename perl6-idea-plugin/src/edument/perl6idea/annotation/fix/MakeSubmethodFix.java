package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6RoutineDecl;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class MakeSubmethodFix implements IntentionAction {
    private final Perl6RoutineDecl myDecl;

    public MakeSubmethodFix(Perl6RoutineDecl decl) {
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
        PsiElement submethodDeclarator = Perl6ElementFactory.createRoutineDeclarator(project, "submethod");
        myDecl.getDeclaratorNode().replace(submethodDeclarator);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
