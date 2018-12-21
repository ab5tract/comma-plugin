package edument.perl6idea.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.refactoring.NewCodeBlockData;
import edument.perl6idea.refactoring.Perl6CodeBlockType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class Perl6ElementFactory {
    public static Perl6Statement createNamedCodeBlock(Project project,
                                                  NewCodeBlockData data,
                                                  List<String> contents) {
        return produceElement(project, getNamedCodeBlockText(data, contents), Perl6Statement.class);
    }

    private static String getNamedCodeBlockText(NewCodeBlockData data,
                                                List<String> contents) {
        String base = "%s %s(%s)";
        StringJoiner signatureJoiner = new StringJoiner(", ");
        Arrays.stream(data.signatureParts).forEachOrdered(signatureJoiner::add);
        String signature = signatureJoiner.toString();
        if (!data.returnType.isEmpty())
            signature += " --> " + data.returnType;

        String type = data.type == Perl6CodeBlockType.ROUTINE ? "sub" : "method";
        String baseFilled = String.format(base, type, data.name, signature);

        StringJoiner bodyJoiner = new StringJoiner("");
        contents.forEach(bodyJoiner::add);

        return String.format("%s {\n%s\n}", baseFilled, bodyJoiner.toString());
    }

    public static Perl6Statement createStatementFromText(Project project, String def) {
        return produceElement(project, def, Perl6Statement.class);
    }

    public static Perl6Statement createConstantAssignment(Project project, String name, String code) {
        return produceElement(project, getConstantAssignmentText(name, code), Perl6Statement.class);
    }

    private static String getConstantAssignmentText(String name, String code) {
        return String.format("my constant %s = %s;", name, code);
    }

    public static Perl6Statement createVariableAssignment(Project project, String name, String code, boolean control) {
        return produceElement(project, getVariableAssignmentText(name, code, control), Perl6Statement.class);
    }

    private static String getVariableAssignmentText(String name, String code, boolean control) {
        return String.format(control ? "my %s = do %s;" : "my %s = %s;", name, code);
    }

    public static Perl6LongName createModuleName(Project project, String name) {
        return produceElement(project, getModuleNameText(name), Perl6LongName.class);
    }

    private static String getModuleNameText(String name) {
        return "use " + name;
    }

    public static PsiElement createTypeDeclarationName(Project project, String name) {
        // 6 number of chars in `class `
        return produceElement(project, getTypeDeclarationText(name), Perl6Statement.class).findElementAt(6);
    }

    private static String getTypeDeclarationText(String name) {
        return String.format("class %s {}", name);
    }

    public static Perl6LongName createIsTraitName(Project project, String name) {
        return produceElement(project, getIsTraitNameText(name), Perl6LongName.class);
    }

    private static String getIsTraitNameText(String name) {
        return String.format("class Dummy is %s {}", name);
    }

    public static Perl6LongName createTypeName(Project project, String name) {
        return produceElement(project, getTypeNameText(name), Perl6LongName.class);
    }

    private static String getTypeNameText(String name) {
        // right now if type is lowercase, it will be parsed as a routine name,
        // so cheat with a trait
        return String.format("class A is %s;", name);
    }

    public static Perl6LongName createMethodCallName(Project project, String name) {
        return produceElement(project, getMethodCallNameText(name), Perl6LongName.class);
    }

    private static String getMethodCallNameText(String name) {
        return name.startsWith("!") ? "class { method " + name + " {} }" : "self." + name;
    }

    public static Perl6SubCallName createSubCallName(Project project, String name) {
        return produceElement(project, getSubroutineText(name), Perl6SubCallName.class);
    }

    private static String getSubroutineText(String name) {
        // We need explicit pair of `()`, because without it
        // possible subroutines with first capital letter are parsed
        // as a type, not as a routine call
        return String.format("%s();", name);
    }

    public static Perl6Variable createVariable(Project project, String name) {
        return produceElement(project, getVariableText(name), Perl6Variable.class);
    }

    private static String getVariableText(String name) {
        if (name.length() > 1 && (name.charAt(1) == '.' || name.charAt(1) == '!')) {
            return "has " + name;
        }
        return name;
    }

    public static Perl6Statement createSubCall(Project project, NewCodeBlockData data) {
        return produceElement(project, getSubCallText(data), Perl6Statement.class);
    }

    private static String getSubCallText(NewCodeBlockData data) {
        return String.format("%s();", data.name);
    }

    public static PsiElement createMethodCall(Project project, NewCodeBlockData data) {
        return produceElement(project, getMethodCallText(data), Perl6Statement.class);
    }

    private static String getMethodCallText(NewCodeBlockData data) {
        return String.format("self%s%s;", data.isPrivateMethod ? "." : "!", data.name);
    }

    private static <T extends PsiElement> T produceElement(Project project, @NotNull String text, Class<T> clazz) {
        String filename = "dummy." + Perl6ScriptFileType.INSTANCE.getDefaultExtension();
        Perl6File dummyFile = (Perl6File) PsiFileFactory.getInstance(project)
                .createFileFromText(filename, Perl6ScriptFileType.INSTANCE, text);
        return PsiTreeUtil.findChildOfType(dummyFile, clazz);
    }
}
