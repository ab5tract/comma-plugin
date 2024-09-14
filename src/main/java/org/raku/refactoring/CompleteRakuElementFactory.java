package org.raku.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.raku.psi.*;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class CompleteRakuElementFactory extends RakuElementFactory {
    public static PsiElement createSubCall(Project project, NewCodeBlockData data) {
        if (data.containsExpression) {
            return produceElement(project, getSubCallText(data), RakuSubCall.class);
        } else {
            return produceElement(project, getSubCallText(data), RakuStatement.class);
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
            return produceElement(project, getMethodCallText(data), RakuPostfixApplication.class);
        } else {
            return produceElement(project, getMethodCallText(data), RakuStatement.class);
        }
    }

    private static String getMethodCallText(NewCodeBlockData data) {
        return String.format("self%s%s(%s)%s",
                             data.isPrivateMethod ? "!" : ".",
                             data.name,
                             data.formSignature(true),
                             data.containsExpression ? "" : ";");
    }

    public static RakuTypeName createNil(Project project) {
        return produceElement(project, "Nil", RakuTypeName.class);
    }

    public static RakuParenthesizedExpr createParenthesesExpr(PsiElement argument) {
        return produceElement(argument.getProject(),
                              String.format("(%s)", argument.getText()),
                              RakuParenthesizedExpr.class);
    }

    public static RakuStatement createRegexPart(Project project, NewRegexPartData data, PsiElement[] atoms) {
        return produceElement(project, createRegexPartText(data, atoms), RakuStatement.class);
    }

    private static String createRegexPartText(NewRegexPartData data, PsiElement[] inner) {
        String atoms = Arrays.stream(inner).map(p -> p.getText()).collect(Collectors.joining()).trim();
        return String.format("%s%s %s%s { %s }",
                             data.isLexical ? "my " : "",
                             data.type.name().toLowerCase(Locale.ROOT),
                             data.name, data.signature, atoms);
    }

    public static RakuPackageDecl createPackage(Project project, String packageType, String name) {
        return produceElement(project, String.format("%s %s {}", packageType, name), RakuPackageDecl.class);
    }
}
