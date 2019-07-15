package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6PostfixApplication;
import org.jetbrains.annotations.NotNull;

public class GrepFirstFix implements IntentionAction {
    private final Perl6MethodCall myGrepCall;
    private final Perl6MethodCall myFirstCall;

    public GrepFirstFix(Perl6MethodCall element, Perl6MethodCall postfix) {
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
        PsiElement innerCall = PsiTreeUtil.getParentOfType(myGrepCall, Perl6PostfixApplication.class).copy();
        Perl6PostfixApplication outerCall = PsiTreeUtil.getParentOfType(myFirstCall, Perl6PostfixApplication.class);
        outerCall.replace(innerCall);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
