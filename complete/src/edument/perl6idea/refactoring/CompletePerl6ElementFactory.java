package edument.perl6idea.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.*;

public class CompletePerl6ElementFactory extends Perl6ElementFactory {
    public static PsiElement createSubCall(Project project, NewCodeBlockData data) {
        if (data.containsExpression) {
            return produceElement(project, getSubCallText(data), Perl6SubCall.class);
        } else {
            return produceElement(project, getSubCallText(data), Perl6Statement.class);
        }
    }

    private static String getSubCallText(NewCodeBlockData data) {
        return String.format("%s(%s)%s",
                             data.name,
                             data.formSignature(true),
                             data.containsExpression ? "" : ";");
    }

    public static PsiElement createMethodCall(Project project, NewCodeBlockData data) {
        if (data.containsExpression) {
            return produceElement(project, getMethodCallText(data), Perl6MethodCall.class);
        } else {
            return produceElement(project, getMethodCallText(data), Perl6Statement.class);
        }
    }

    private static String getMethodCallText(NewCodeBlockData data) {
        return String.format("self%s%s(%s)%s",
                             data.isPrivateMethod ? "!" : ".",
                             data.name,
                             data.formSignature(true),
                             data.containsExpression ? "" : ";");
    }

    public static Perl6TypeName createNil(Project project) {
        return produceElement(project, "Nil", Perl6TypeName.class);
    }

    public static Perl6ParenthesizedExpr createParenthesesExpr(PsiElement argument) {
        return produceElement(argument.getProject(),
                              String.format("(%s)", argument.getText()),
                              Perl6ParenthesizedExpr.class);
    }
}
