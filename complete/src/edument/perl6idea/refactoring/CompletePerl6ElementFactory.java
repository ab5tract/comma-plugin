package edument.perl6idea.refactoring;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Statement;

import java.util.List;
import java.util.StringJoiner;

public class CompletePerl6ElementFactory extends Perl6ElementFactory {
    public static Perl6Statement createNamedCodeBlock(Project project,
                                                      NewCodeBlockData data,
                                                      List<String> contents) {
        return produceElement(project, getNamedCodeBlockText(data, contents), Perl6Statement.class);
    }

    private static String getNamedCodeBlockText(NewCodeBlockData data,
                                                List<String> contents) {
        String signature = data.formSignature(false);
        if (!data.returnType.isEmpty()) {
            signature += " --> " + data.returnType;
        }
        String nameToUse = data.type == Perl6CodeBlockType.PRIVATEMETHOD && !data.name.startsWith("!") ? "!" + data.name : data.name;
        String type = data.type == Perl6CodeBlockType.ROUTINE ? "sub" : "method";
        String baseFilled = String.format("%s %s(%s)", type, nameToUse, signature);
        StringJoiner bodyJoiner = new StringJoiner("");
        contents.forEach(bodyJoiner::add);
        return String.format("%s {\n%s\n}", baseFilled, bodyJoiner.toString());
    }

    public static Perl6Statement createSubCall(Project project, NewCodeBlockData data) {
        return produceElement(project, getSubCallText(data), Perl6Statement.class);
    }

    private static String getSubCallText(NewCodeBlockData data) {
        return String.format("%s(%s);", data.name, data.formSignature(true));
    }

    public static Perl6Statement createMethodCall(Project project, NewCodeBlockData data) {
        return produceElement(project, getMethodCallText(data), Perl6Statement.class);
    }

    private static String getMethodCallText(NewCodeBlockData data) {
        return String.format("self%s%s(%s);", data.isPrivateMethod ? "!" : ".", data.name, data.formSignature(true));
    }
}
