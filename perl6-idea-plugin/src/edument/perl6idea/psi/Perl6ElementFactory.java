package edument.perl6idea.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.List;
import java.util.StringJoiner;

public class Perl6ElementFactory {
    public static PsiElement createRoutine(Project project, String name, List<String> params) {
        String text = getRoutineText(name, params);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6Statement.class);
    }

    private static String getRoutineText(String name, List<String> params) {
        String base = "sub " + name + "(";
        StringJoiner joiner = new StringJoiner(", ");
        params.forEach(joiner::add);
        return base + joiner.toString() + ") {}";
    }

    public static Perl6Statement createStatementFromText(Project project, String def) {
        return PsiTreeUtil.findChildOfType(createFile(project, def), Perl6Statement.class);
    }

    public static PsiElement createMethod(Project project, String name, List<String> params) {
        String text = getMethodText(name, params);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6Statement.class);
    }

    private static String getMethodText(String name, List<String> params) {
        String base = "method " + name + "(";
        StringJoiner joiner = new StringJoiner(", ");
        params.forEach(joiner::add);
        return base + joiner.toString() + ") {}";
    }

    public static PsiElement createConstantAssignment(Project project, String name, String code) {
        String text = getConstantAssignmentText(name, code);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6Statement.class);
    }

    private static String getConstantAssignmentText(String name, String code) {
        return String.format("my constant %s = %s;", name, code);
    }

    public static PsiElement createVariableAssignment(Project project, String name, String code, boolean control) {
        String text = getVariableAssignmentText(name, code, control);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6Statement.class);
    }

    private static String getVariableAssignmentText(String name, String code, boolean control) {
        return String.format(control ? "my %s = do %s;" : "my %s = %s;", name, code);
    }

    public static Perl6LongName createModuleName(Project project, String name) {
        String text = getModuleNameText(name);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6LongName.class);
    }

    private static String getModuleNameText(String name) {
        return "use " + name;
    }

    public static Perl6LongName createPublicMethodCall(Project project, String name) {
        String text = getPublicMethodCallText(name);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6LongName.class);
    }

    private static String getPublicMethodCallText(String name) {
        return "self." + name;
    }

    public static PsiElement createTypeDeclarationName(Project project, String name) {
        String text = getTypeDeclarationText(name);
        Perl6File dummyFile = createFile(project, text);
        // first 6 characters are `class ` in `getTypeDeclarationText` dummy
        return dummyFile.findElementAt(6);
    }

    private static String getTypeDeclarationText(String name) {
        // It gives us 6 characters to skip when taking exact node for `createTypeDeclarationName` method
        return String.format("class %s {}", name);
    }

    public static Perl6LongName createIsTraitName(Project project, String name) {
        String text = getIsTraitNameText(name);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6LongName.class);
    }

    private static String getIsTraitNameText(String name) {
        return String.format("class Dummy is %s {}", name);
    }

    public static Perl6LongName createTypeName(Project project, String name) {
        String text = getTypeNameText(name);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6LongName.class);
    }

    private static String getTypeNameText(String name) {
        return name;
    }

    public static Perl6SubCallName createSubCallName(Project project, String name) {
        String text = getSubroutineText(name);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6SubCallName.class);
    }

    private static String getSubroutineText(String name) {
        return name;
    }

    public static Perl6LongName createPrivateMethodCall(Project project, String name) {
        String text = getPrivateMethodCallText(name);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6LongName.class);
    }

    private static String getPrivateMethodCallText(String name) {
        return String.format("class Dummy { method %s {} }", name);
    }

    public static Perl6Variable createVariable(Project project, String name) {
        String text = getVariableText(name);
        Perl6File dummyFile = createFile(project, text);
        return PsiTreeUtil.findChildOfType(dummyFile, Perl6Variable.class);
    }

    private static String getVariableText(String name) {
        if (name.length() > 1 && (name.charAt(1) == '.' || name.charAt(1) == '!')) {
            return "has " + name;
        }
        return name;
    }

    private static Perl6File createFile(Project project, String text) {
        String filename = "dummy." + Perl6ScriptFileType.INSTANCE.getDefaultExtension();
        return (Perl6File)PsiFileFactory.getInstance(project)
                                        .createFileFromText(filename, Perl6ScriptFileType.INSTANCE, text);
    }
}
