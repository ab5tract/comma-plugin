package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class UnwrapPromiseAllOfIntention extends PsiElementBaseIntentionAction implements IntentionAction {
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
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        Perl6PostfixApplication app = getAwaitPostfix(element);
        if (app == null)
            return false;
        PsiElement caller = app.getCaller();
        PsiElement postfix = app.getPostfix();
        return (caller instanceof Perl6TypeName &&
                postfix instanceof Perl6MethodCall &&
                ((Perl6TypeName)caller).getTypeName().equals("Promise") &&
                ((Perl6MethodCall)postfix).getCallName().equals(".allof") &&
                ((Perl6MethodCall)postfix).getCallArguments().length > 0);
    }

    private static Perl6PostfixApplication getAwaitPostfix(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (!(parent instanceof Perl6SubCallName))
            return null;
        Perl6SubCallName callNameElement = (Perl6SubCallName)parent;
        String callName = callNameElement.getCallName();
        if (!callName.equals("await"))
            return null;

        PsiElement next = callNameElement.skipWhitespacesForward();
        if (!(next instanceof Perl6PostfixApplication))
            return null;
        return (Perl6PostfixApplication)next;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        Perl6PostfixApplication app = getAwaitPostfix(element);
        if (app == null)
            return;

        Perl6MethodCall methodCall = (Perl6MethodCall)app.getPostfix();
        //Perl6PostfixApplication postfix = PsiTreeUtil.getParentOfType(app, Perl6PostfixApplication.class);
        if (methodCall == null)
            return;

        PsiElement replacer;
        PsiElement[] args = methodCall.getCallArguments();
        if (args.length == 0)
            return;

        if (args.length == 1) {
            replacer = args[0];
        } else {
            replacer = Perl6ElementFactory.createInfixApplication(project, ", ", Arrays.asList(args));
        }
        app.replace(replacer);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
