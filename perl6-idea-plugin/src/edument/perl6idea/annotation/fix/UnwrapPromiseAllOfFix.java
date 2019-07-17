package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6PostfixApplication;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class UnwrapPromiseAllOfFix implements IntentionAction {
    private final Perl6MethodCall methodCall;
    private final Perl6PostfixApplication postfix;

    public UnwrapPromiseAllOfFix(Perl6MethodCall postfixCall) {
        postfix = PsiTreeUtil.getParentOfType(postfixCall, Perl6PostfixApplication.class);
        methodCall = postfixCall;
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Unwrap Promise.allof call";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement replacer;
        PsiElement[] args = methodCall.getCallArguments();
        // This annotation is called from AwaitAllOfAnnotator,
        // where we check that length is not zero, but stilll check it just in case
        if (args.length == 0)
            return;

        if (args.length == 1) {
            replacer = args[0];
        } else {
            replacer = Perl6ElementFactory.createInfixApplication(project, ", ", Arrays.asList(args));
        }
        postfix.replace(replacer);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
