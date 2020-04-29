package edument.perl6idea.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.*;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    public static Perl6Statement createRegexPart(Project project, NewRegexPartData data, PsiElement[] atoms) {
        return produceElement(project, createRegexPartText(data, atoms), Perl6Statement.class);
    }

    private static String createRegexPartText(NewRegexPartData data, PsiElement[] inner) {
        String atoms = Arrays.stream(inner).map(p -> p.getText()).collect(Collectors.joining()).trim();
        return String.format("%s%s %s%s { %s }",
                             data.isLexical ? "my " : "",
                             data.type.name().toLowerCase(),
                             data.name, data.signature, atoms);
    }
}
