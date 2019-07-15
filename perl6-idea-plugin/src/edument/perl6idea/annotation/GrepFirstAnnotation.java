package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.GrepFirstFix;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GrepFirstAnnotation implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6MethodCall))
            return;

        // First call is "grep"
        if (!((Perl6MethodCall)element).getCallName().equals(".grep"))
            return;

        Perl6PostfixApplication upperApplication = PsiTreeUtil.getParentOfType(element, Perl6PostfixApplication.class);
        if (upperApplication == null)
            return;

        PsiElement nonWhitespacePostfix = upperApplication.skipWhitespacesForward();
        if (!(nonWhitespacePostfix instanceof Perl6MethodCall))
            return;

        if (!((Perl6MethodCall)nonWhitespacePostfix).getCallName().equals(".first"))
            return;

        PsiElement[] grepArgs = ((Perl6MethodCall)element).getCallArguments();
        PsiElement[] firstArgs = ((Perl6MethodCall)nonWhitespacePostfix).getCallArguments();

        // Firstly, check if first call is arg-less, otherwise we don't
        // need to calculate grep at all
        if (firstArgs.length != 0 || grepArgs.length != 1)
            return;

        boolean shouldAnnotate = false;

        // Now check is grep argument is a single call
        for (PsiElement arg : grepArgs) {
            if (arg instanceof Perl6PostfixApplication) {
                PsiElement postfix = arg;
                while (true) {
                    postfix = postfix.getFirstChild();
                    if (postfix instanceof Perl6PostfixApplication)
                        continue;
                    else if (postfix instanceof Perl6Whatever) {
                        shouldAnnotate = true;
                        break;
                    }
                    break;
                }
            } else if (arg instanceof Perl6BlockOrHash) {
                Perl6StatementList statementList = PsiTreeUtil.findChildOfType(arg, Perl6StatementList.class);
                if (statementList == null)
                    break;
                Perl6Statement[] statements = PsiTreeUtil.getChildrenOfType(statementList, Perl6Statement.class);
                if (statements == null || statements.length != 1)
                    break;

                PsiElement child = statements[0];

                while (true) {
                    child = child.getFirstChild();
                    if (child instanceof Perl6PostfixApplication)
                        continue;
                    else if (child instanceof Perl6Variable) {
                        if (Objects.equals(((Perl6Variable)child).getName(), "$_")) {
                            shouldAnnotate = true;
                            break;
                        }
                    } else if (child instanceof Perl6MethodCall) {
                        shouldAnnotate = true;
                        break;
                    }
                    break;
                }
            }
        }

        if (shouldAnnotate) {
            holder.createWeakWarningAnnotation(new TextRange(element.getTextOffset(), nonWhitespacePostfix.getTextOffset() + nonWhitespacePostfix.getTextLength()),
                                               "Can be simplified into a single first method call")
                .registerFix(new GrepFirstFix((Perl6MethodCall)element, (Perl6MethodCall)nonWhitespacePostfix));
        }
    }
}
