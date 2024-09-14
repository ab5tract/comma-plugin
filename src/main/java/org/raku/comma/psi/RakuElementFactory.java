package org.raku.comma.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.filetypes.RakuScriptFileType;
import org.raku.comma.refactoring.NewCodeBlockData;
import org.raku.comma.refactoring.RakuCodeBlockType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringJoiner;

public class RakuElementFactory {
    public static final String ARRAY_CONTEXTUALIZER = "@";
    public static final String HASH_CONTEXTUALIZER = "%";

    public static RakuFile createFileFromText(Project project, String text) {
        return produceElement(project, text, RakuFile.class);
    }

    public static RakuStatement createStatementFromText(Project project, String def) {
        return produceElement(project, def, RakuStatement.class);
    }

    public static RakuStatement createConstantAssignment(Project project, String name, String code) {
        return produceElement(project, getConstantAssignmentText(name, code), RakuStatement.class);
    }

    private static String getConstantAssignmentText(String name, String code) {
        return String.format("my constant %s = %s;", name, code);
    }

    public static RakuStatement createVariableAssignment(Project project, String name, String code, boolean control) {
        return produceElement(project, getVariableAssignmentText(name, code, control), RakuStatement.class);
    }

    private static String getVariableAssignmentText(String name, String code, boolean control) {
        return String.format(control ? "my %s = do %s;" : "my %s = %s;", name, code);
    }

    public static RakuLongName createModuleName(Project project, String name) {
        return produceElement(project, getModuleNameText(name), RakuLongName.class);
    }

    private static String getModuleNameText(String name) {
        return "use " + name;
    }

    public static PsiElement createTypeDeclarationName(Project project, String name) {
        return produceElement(project, getTypeDeclarationText(name), RakuPackageDecl.class).getNameIdentifier();
    }

    private static String getTypeDeclarationText(String name) {
        return String.format("class %s {}", name);
    }

    public static RakuLongName createIsTraitName(Project project, String name) {
        RakuPackageDecl packageDecl = produceElement(project, getIsTraitNameText(name), RakuPackageDecl.class);
        return PsiTreeUtil.findChildOfType(packageDecl.getTraits().get(0), RakuLongName.class);
    }

    private static String getIsTraitNameText(String name) {
        return String.format("class A is %s {}", name);
    }

    public static RakuLongName createTypeName(Project project, String name) {
        return produceElement(project, getTypeNameText(name), RakuLongName.class);
    }

    private static String getTypeNameText(String name) {
        return String.format("class %s {}", name);
    }

    public static RakuLongName createRoutineName(Project project, String name) {
        return produceElement(project, getRoutineNameText(name), RakuLongName.class);
    }

    private static String getRoutineNameText(String name) {
        return String.format("method %s() {}", name);
    }

    public static RakuLongName createMethodCallName(Project project, String name) {
        return produceElement(project, getMethodCallNameText(name), RakuLongName.class);
    }

    private static String getMethodCallNameText(String name) {
        return name.startsWith("!") ? "class { method " + name + " {} }" : "self." + name;
    }

    public static RakuSubCallName createSubCallName(Project project, String name) {
        return produceElement(project, getSubroutineText(name), RakuSubCallName.class);
    }

    private static String getSubroutineText(String name) {
        // We need explicit pair of `()`, because without it
        // possible subroutines with first capital letter are parsed
        // as a type, not as a routine call
        return String.format("%s();", name);
    }

    public static RakuRegexAtom createRegexCall(Project project, String name) {
        return produceElement(project, getRegexCallText(name), RakuRegexAtom.class);
    }

    private static String getRegexCallText(String name) {
        return String.format("grammar { rule { <%s> } }", name);
    }

    public static RakuLongName createRegexLongName(Project project, String name) {
        return produceElement(project, getRegexDeclText(name), RakuLongName.class);
    }

    private static String getRegexDeclText(String name) {
        return String.format("rule %s {<?>}", name);
    }

    public static RakuVariable createVariable(Project project, String name) {
        return produceElement(project, getVariableText(name), RakuVariable.class);
    }

    private static String getVariableText(String name) {
        if (name.length() > 1 && (name.charAt(1) == '.' || name.charAt(1) == '!')) {
            return "has " + name;
        }
        return name;
    }

    public static RakuStatement createNamedCodeBlock(Project project,
                                                     NewCodeBlockData data,
                                                     List<String> contents) {
        return produceElement(project, getNamedCodeBlockText(data, contents), RakuStatement.class);
    }

    private static String getNamedCodeBlockText(NewCodeBlockData data,
                                                List<String> contents) {
        String signature = data.formSignature(false);
        if (!data.returnType.isEmpty()) {
            signature += " --> " + data.returnType;
        }
        String nameToUse = data.type == RakuCodeBlockType.PRIVATEMETHOD && !data.name.startsWith("!") ? "!" + data.name : data.name;
        String type = data.type == RakuCodeBlockType.ROUTINE ? "sub" : "method";
        String baseFilled = String.format("%s %s(%s)", type, nameToUse, signature);
        StringJoiner bodyJoiner = new StringJoiner("");
        contents.forEach(bodyJoiner::add);
        return String.format("%s {\n%s\n}", baseFilled, bodyJoiner);
    }


    public static RakuColonPair createColonPair(Project project, String text) {
        return produceElement(project, ":" + text, RakuColonPair.class);
    }

    public static RakuFatArrow createFatArrow(Project project, String key, PsiElement value) {
        return produceElement(project, String.format("%s => %s", key, value.getText()), RakuFatArrow.class);
    }

    public static RakuLoopStatement createLoop(Project project, PsiElement block) {
        return produceElement(project, "loop " + block.getText(), RakuLoopStatement.class);
    }

    protected static <T extends PsiElement> T produceElement(Project project, @NotNull String text, Class<T> clazz) {
        String filename = "dummy." + RakuScriptFileType.INSTANCE.getDefaultExtension();
        RakuFile dummyFile = (RakuFile) PsiFileFactory.getInstance(project)
                .createFileFromText(filename, RakuScriptFileType.INSTANCE, text);
        return PsiTreeUtil.findChildOfType(dummyFile, clazz, false);
    }

    public static RakuInfixApplication createInfixApplication(Project project, String infix, List<PsiElement> parts) {
        return produceElement(project, getInfixApplicationText(infix, parts), RakuInfixApplication.class);
    }

    private static String getInfixApplicationText(String infix, List<PsiElement> parts) {
        StringJoiner infixJoiner = new StringJoiner(infix);
        parts.stream().map(PsiElement::getText).forEach(infixJoiner::add);
        return infixJoiner + ";";
    }

    public static RakuSignature createRoutineSignature(Project project, List<RakuParameter> parameters) {
        return produceElement(project, createRoutineSignatureText(parameters), RakuSignature.class);
    }

    private static String createRoutineSignatureText(List<RakuParameter> parameters) {
        StringJoiner signature = new StringJoiner(", ");
        parameters.stream().map(PsiElement::getText).forEach(signature::add);
        return "sub foo(" + signature + ") {}";
    }

    public static PsiElement createMethodCallOperator(Project project, boolean isPrivate) {
        RakuMethodCall methodCall = produceElement(project, String.format("self%sa();", isPrivate ? "!" : "."), RakuMethodCall.class);
        return methodCall.getCallOperatorNode();
    }

    public static RakuTrait createTrait(Project project, String modifier, String name) {
        return produceElement(project, createTraitText(modifier, name), RakuTrait.class);
    }

    private static String createTraitText(String modifier, String name) {
        return String.format("my $a %s %s;", modifier, name);
    }

    public static PsiElement createPackageDeclarator(Project project, String type) {
        RakuPackageDecl packageDecl = produceElement(project, String.format("%s {}", type), RakuPackageDecl.class);
        return packageDecl.getPackageKeywordNode();
    }

    public static RakuRoutineDecl createRoutineDeclaration(Project project, String name, List<String> parameters) {
        return produceElement(project, String.format("sub %s(%s) {}", name, String.join(", ", parameters)), RakuRoutineDecl.class);
    }

    public static PsiElement createRoutineDeclarator(Project project, String type) {
        RakuRoutineDecl routineDecl = produceElement(project, String.format("%s a() {}", type), RakuRoutineDecl.class);
        return routineDecl.getDeclaratorNode();
    }

    public static RakuVariableDecl createVariableDecl(Project project, String scope, String name) {
        return produceElement(project, String.format("%s %s;", scope, name), RakuVariableDecl.class);
    }

    public static PsiElement createInfixOperator(Project project, String op) {
        return produceElement(project, String.format("1 %s 1", op), RakuInfix.class).getOperator();
    }

    public static RakuIfStatement createIfStatement(Project project, boolean isIf, int numberOfBranches) {
        return produceElement(project, createIfStatementText(isIf, numberOfBranches), RakuIfStatement.class);
    }

    private static String createIfStatementText(boolean isIf, int numberOfBranches) {
        StringJoiner ifText = new StringJoiner("\n");
        ifText.add(String.format("%s True {}", isIf ? "if" : "with"));
        numberOfBranches--;
        while (numberOfBranches > 1) {
            ifText.add(String.format("%s True {}", isIf ? "elsif" : "orwith"));
            numberOfBranches--;
        }
        if (numberOfBranches == 1)
            ifText.add("else {}");
        return ifText.toString();
    }

    public static RakuUnlessStatement createUnlessStatement(Project project) {
        return produceElement(project, "unless False {}", RakuUnlessStatement.class);
    }

    public static RakuGivenStatement createGivenStatement(Project project) {
        return produceElement(project, "given $_ {}", RakuGivenStatement.class);
    }

    public static RakuWithoutStatement createWithoutStatement(Project project) {
        return produceElement(project, "without $_ {}", RakuWithoutStatement.class);
    }

    public static RakuForStatement createForStatement(Project project) {
        return produceElement(project, "for $_ {}", RakuForStatement.class);
    }

    public static RakuWheneverStatement createWheneverStatement(Project project) {
        return produceElement(project, "whenever $_ {}", RakuWheneverStatement.class);
    }

    public static RakuWhenStatement createWhenStatement(Project project) {
        return produceElement(project, "when $_ {}", RakuWhenStatement.class);
    }

    public static RakuTry createTryStatement(Project project) {
        return produceElement(project, "try {}", RakuTry.class);
    }

    public static RakuDefaultStatement createDefaultStatement(Project project) {
        return produceElement(project, "default {}", RakuDefaultStatement.class);
    }

    public static RakuStart createStartStatement(Project project) {
        return produceElement(project, "start {}", RakuStart.class);
    }

    public static RakuPointyBlock createPointyBlock(Project project) {
        return produceElement(project, "-> $_ {}", RakuPointyBlock.class);
    }

    public static RakuBlockOrHash createBlockOrHash(Project project) {
        return produceElement(project, "{}", RakuBlockOrHash.class);
    }

    public static RakuContextualizer createContextualizer(Project project, String contextualizer) {
        return produceElement(project, String.format("%s();", contextualizer), RakuContextualizer.class);
    }

    public static RakuArrayComposer createArrayComposer(Project project) {
        return produceElement(project, "[]", RakuArrayComposer.class);
    }

    public static RakuCatchStatement createCatchStatement(Project project) {
        return produceElement(project, "CATCH {}", RakuCatchStatement.class);
    }

    public static PsiElement createNewLine(Project project) {
        return PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n");
    }

    public static RakuDo createDoStatement(Project project) {
        return produceElement(project, createDoBlockText(), RakuDo.class);
    }

    private static String createDoBlockText() {
        return "do {}";
    }

    public static RakuRegexAtom createRegexGroup(Project project, boolean isCapture) {
        return produceElement(project, isCapture ? "/()/" : "/[]/", RakuRegexAtom.class);
    }

    public static PsiElement createRegexVariable(Project project) {
        return produceElement(project, "/$<x> = [ ] /", RakuRegexAtom.class);
    }

    public static RakuStrLiteral createStrLiteral(Project project, String text) {
        return produceElement(project, text, RakuStrLiteral.class);
    }

    public static RakuParameter createParameter(Project project, String text) {
        return produceElement(project, String.format("sub (%s) {}", text), RakuParameter.class);
    }

    public static PsiElement createRegexRangeDelimiter(Project project) {
        RakuRegexCClassElem elem = produceElement(project, "/<[..]>/", RakuRegexCClassElem.class);
        return elem.getFirstChild().getNextSibling();
    }
}
