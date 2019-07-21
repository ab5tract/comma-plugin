package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6PostfixApplication;
import edument.perl6idea.psi.Perl6Statement;
import org.jetbrains.annotations.Nls;
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
        Perl6Statement executableCall = Perl6ElementFactory.createStatementFromText(project, "$*EXECUTABLE.absolute");
        Perl6PostfixApplication postfix = PsiTreeUtil.findChildOfType(executableCall, Perl6PostfixApplication.class);
        if (postfix != null)
            strLiteral.replace(postfix);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
