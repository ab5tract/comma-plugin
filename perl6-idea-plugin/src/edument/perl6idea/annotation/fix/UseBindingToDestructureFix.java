package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Infix;
import org.jetbrains.annotations.NotNull;

public class UseBindingToDestructureFix implements IntentionAction {
    private final Perl6Infix myInfix;

    public UseBindingToDestructureFix(Perl6Infix infix) {
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
        myInfix.getOperator().replace(Perl6ElementFactory.createInfixOperator(project, ":="));
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}