package org.raku.comma.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.*;
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
        RakuPostfixApplication app = getAwaitPostfix(element);
        if (app == null)
            return false;
        PsiElement caller = app.getOperand();
        PsiElement postfix = app.getPostfix();
        return (caller instanceof RakuTypeName &&
                postfix instanceof RakuMethodCall &&
                ((RakuTypeName)caller).getTypeName().equals("Promise") &&
                ((RakuMethodCall)postfix).getCallName().equals(".allof") &&
                ((RakuMethodCall)postfix).getCallArguments().length > 0);
    }

    private static RakuPostfixApplication getAwaitPostfix(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (!(parent instanceof RakuSubCallName callNameElement))
            return null;
        String callName = callNameElement.getCallName();
        if (!callName.equals("await"))
            return null;

        PsiElement next = callNameElement.skipWhitespacesForward();
        if (!(next instanceof RakuPostfixApplication))
            return null;
        return (RakuPostfixApplication)next;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RakuPostfixApplication app = getAwaitPostfix(element);
        if (app == null)
            return;

        RakuMethodCall methodCall = (RakuMethodCall)app.getPostfix();
        //RakuPostfixApplication postfix = PsiTreeUtil.getParentOfType(app, RakuPostfixApplication.class);
        if (methodCall == null)
            return;

        PsiElement replacer;
        PsiElement[] args = methodCall.getCallArguments();
        if (args.length == 0)
            return;

        if (args.length == 1) {
            replacer = args[0];
        } else {
            replacer = RakuElementFactory.createInfixApplication(project, ", ", Arrays.asList(args));
        }
        app.replace(replacer);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
