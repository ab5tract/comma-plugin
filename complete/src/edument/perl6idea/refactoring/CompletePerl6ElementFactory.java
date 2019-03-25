package edument.perl6idea.refactoring;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Statement;

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
}
