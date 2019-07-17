package edument.perl6idea.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.refactoring.NewCodeBlockData;
import edument.perl6idea.refactoring.Perl6CodeBlockType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringJoiner;

public class Perl6ElementFactory {
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

    public static Perl6LongName createRoutineName(Project project, String name) {
        return produceElement(project, getRoutineNameText(name), Perl6LongName.class);
    }

    private static String getRoutineNameText(String name) {
        return String.format("method %s() {}", name);
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

    public static Perl6RegexCall createRegexCall(Project project, String name) {
        return produceElement(project, getRegexCallText(name), Perl6RegexCall.class);
    }

    private static String getRegexCallText(String name) {
        return String.format("grammar { rule { <%s> } }", name);
    }

    public static Perl6LongName createRegexLongName(Project project, String name) {
        return produceElement(project, getRegexDeclText(name), Perl6LongName.class);
    }

    private static String getRegexDeclText(String name) {
        return String.format("rule %s {<?>}", name);
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


    public static Perl6ColonPair createColonPair(Project project, String text) {
        return produceElement(project, ":" + text, Perl6ColonPair.class);
    }

    public static Perl6FatArrow createFatArrow(Project project, String key, PsiElement value) {
        return produceElement(project, String.format("%s => %s", key, value.getText()), Perl6FatArrow.class);
    }

    public static Perl6LoopStatement createLoop(Project project, PsiElement block) {
        return produceElement(project, "loop " + block.getText(), Perl6LoopStatement.class);
    }

    protected static <T extends PsiElement> T produceElement(Project project, @NotNull String text, Class<T> clazz) {
        String filename = "dummy." + Perl6ScriptFileType.INSTANCE.getDefaultExtension();
        Perl6File dummyFile = (Perl6File) PsiFileFactory.getInstance(project)
                .createFileFromText(filename, Perl6ScriptFileType.INSTANCE, text);
        return PsiTreeUtil.findChildOfType(dummyFile, clazz);
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

    public static PsiElement createMethodCallOperator(Project project, boolean isPrivate) {
        Perl6MethodCall methodCall = produceElement(project, String.format("self%sa();", isPrivate ? "!" : "."), Perl6MethodCall.class);
        return methodCall.getCallOperatorNode();
    }

    public static Perl6Trait createTrait(Project project, String modifier, String name) {
        return produceElement(project, createTraitText(modifier, name), Perl6Trait.class);
    }

    private static String createTraitText(String modifier, String name) {
        return String.format("my $a %s %s;", modifier, name);
    }

    public static PsiElement createPackageDeclarator(Project project, String type) {
        Perl6PackageDecl packageDecl = produceElement(project, String.format("%s {}", type), Perl6PackageDecl.class);
        return packageDecl.getPackageKeywordNode();
    }

    public static PsiElement createRoutineDeclarator(Project project, String type) {
        Perl6RoutineDecl routineDecl = produceElement(project, String.format("%s a() {}", type), Perl6RoutineDecl.class);
        return routineDecl.getDeclaratorNode();
    }
}
