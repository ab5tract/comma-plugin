package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import edument.perl6idea.annotation.fix.UnwrapPromiseAllOfFix;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6PostfixApplication;
import edument.perl6idea.psi.Perl6SubCallName;
import edument.perl6idea.psi.Perl6TypeName;
import org.jetbrains.annotations.NotNull;

public class AwaitAllOfAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6SubCallName))
            return;

        Perl6SubCallName callNameElement = (Perl6SubCallName)element;
        String callName = callNameElement.getCallName();
        if (!callName.equals("await"))
            return;

        PsiElement next = callNameElement.skipWhitespacesForward();
        if (next instanceof Perl6PostfixApplication) {
            Perl6PostfixApplication app = (Perl6PostfixApplication)next;
            PsiElement caller = app.getCaller();
            PsiElement postfix = app.getPostfix();
            if (caller instanceof Perl6TypeName &&
                postfix instanceof Perl6MethodCall &&
                ((Perl6TypeName)caller).getTypeName().equals("Promise") &&
                ((Perl6MethodCall)postfix).getCallName().equals(".allof") &&
                ((Perl6MethodCall)postfix).getCallArguments().length > 0)
                holder
                    .createWarningAnnotation(postfix, "Promise.allof call is redundant with await")
                    .registerFix(new UnwrapPromiseAllOfFix((Perl6MethodCall)postfix));
        }
    }
}
