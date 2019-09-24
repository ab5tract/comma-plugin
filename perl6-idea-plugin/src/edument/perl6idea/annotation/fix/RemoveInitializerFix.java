package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;

public class RemoveInitializerFix implements IntentionAction {
    private final Perl6VariableDecl variableDecl;
    private final String variableName;

    public RemoveInitializerFix(Perl6VariableDecl decl, String name) {
        variableDecl = decl;
        variableName = name;
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Remove redundant initializer";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Perl6VariableDecl newDecl = Perl6ElementFactory.createVariableDecl(project, variableDecl.getScope(), variableName);
        variableDecl.replace(newDecl);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
