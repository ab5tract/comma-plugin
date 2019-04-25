package edument.perl6idea.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.*;

import java.util.List;
import java.util.StringJoiner;

public class CompletePerl6ElementFactory extends Perl6ElementFactory {
    public static Perl6Statement createSubCall(Project project, NewCodeBlockData data) {
        return produceElement(project, getSubCallText(data), Perl6Statement.class);
    }

    private static String getSubCallText(NewCodeBlockData data) {
        return String.format("%s(%s)%s",
                             data.name,
                             data.formSignature(true),
                             data.containsExpression ? "" : ";");
    }

    public static Perl6Statement createMethodCall(Project project, NewCodeBlockData data) {
        return produceElement(project, getMethodCallText(data), Perl6Statement.class);
    }

    private static String getMethodCallText(NewCodeBlockData data) {
        return String.format("self%s%s(%s)%s",
                             data.isPrivateMethod ? "!" : ".",
                             data.name,
                             data.formSignature(true),
                             data.containsExpression ? "" : ";");
    }

    public static Perl6InfixApplication createInfixApplication(Project project, List<PsiElement> parts) {
        return produceElement(project, getInfixApplicationText(parts), Perl6InfixApplication.class);
    }

    private static String getInfixApplicationText(List<PsiElement> parts) {
        StringJoiner infix = new StringJoiner(", ");
        parts.stream().map(PsiElement::getText).forEach(infix::add);
        return infix.toString() + ";";
    }

    public static Perl6Signature createRoutineSignature(Project project, List<Perl6Parameter> parameters) {
        return produceElement(project, createRoutineSignatureText(parameters), Perl6Signature.class);
    }

    private static String createRoutineSignatureText(List<Perl6Parameter> parameters) {
        StringJoiner signature = new StringJoiner(", ");
        parameters.stream().map(PsiElement::getText).forEach(signature::add);
        return "sub foo(" + signature.toString() + ") {}";
    }
}
