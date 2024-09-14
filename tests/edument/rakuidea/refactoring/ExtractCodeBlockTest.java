package edument.rakuidea.refactoring;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.util.Consumer;
import com.intellij.util.Function;
import com.intellij.util.Producer;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.psi.*;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtractCodeBlockTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/block-extract";
    }

    public void testMethodSingleScopePresence() {
        doScopeTest("start", RakuCodeBlockType.METHOD,
                (scopes) -> {
                    TestCase.assertEquals(1, scopes.size());
                    checkPackage(scopes, 0, "A", "class");
                });
    }

    public void testMethodOuterClassScopePresence() {
        doScopeTest("start", RakuCodeBlockType.METHOD,
                (scopes) -> {
                    TestCase.assertEquals(4, scopes.size());
                    checkPackage(scopes, 0, "M", "monitor");
                    checkPackage(scopes, 1, "G", "grammar");
                    checkPackage(scopes, 2, "R", "role");
                    checkPackage(scopes, 3, "C", "class");
                });
    }

    public void testSubFilePresence() {
        doScopeTest("'start'", RakuCodeBlockType.ROUTINE,
                (scopes) -> {
                    TestCase.assertEquals(1, scopes.size());
                    PsiElement decl = PsiTreeUtil.getParentOfType(scopes.get(0), RakuPackageDecl.class, RakuRoutineDecl.class, RakuFile.class);
                    TestCase.assertTrue(decl instanceof RakuFile);
                });
    }

    public void testSubNestedScopePresence() {
        doScopeTest("'start'", RakuCodeBlockType.ROUTINE,
                (scopes) -> {
                    TestCase.assertEquals(4, scopes.size());
                    checkPackage(scopes, 2, "ABC", "class");
                });
    }

    private void checkPackage(List<RakuStatementList> scopes, int index, String packageName, String packageKind) {
        PsiElement decl = PsiTreeUtil.getParentOfType(scopes.get(index), RakuPackageDecl.class, RakuRoutineDecl.class, RakuFile.class);
        TestCase.assertTrue(decl instanceof RakuPackageDecl);
        TestCase.assertNotNull(decl);
        TestCase.assertEquals(packageName, ((RakuPackageDecl)decl).getPackageName());
        TestCase.assertEquals(packageKind, ((RakuPackageDecl)decl).getPackageKind());
    }

    public void testTopFileSubroutineExtraction() {
        doTest(() -> getClosestStatementListByText("say 1"),
                "foo-bar", RakuCodeBlockType.ROUTINE);
    }

    public void testTopFileMethodImpossible() {
        UsefulTestCase.assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () ->
                doTest(() -> getClosestStatementListByText("say 1"),
                        "foo-bar", RakuCodeBlockType.METHOD));
    }

    public void testInMethodMethodExtraction() {
        UsefulTestCase.assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () ->
                doTest(() -> getClosestStatementListByText("foo"),
                        "foo-bar", RakuCodeBlockType.PRIVATEMETHOD));
    }

    public void testInClassMethodExtraction() {
        doTest(() -> getNextList(getClosestStatementListByText("say 'foo'")),
                "foo-bar", RakuCodeBlockType.METHOD);
    }

    public void testInClassPrivateMethodExtraction() {
        doTest(() -> getNextList(getClosestStatementListByText("say 'foo'")),
                "foo-bar", RakuCodeBlockType.PRIVATEMETHOD);
    }

    public void testSubroutineExtractionTwoLevelsUp() {
        doTest(() -> getNextList(getNextList(getClosestStatementListByText("say 'foo'"))),
                "outer-sub", RakuCodeBlockType.ROUTINE);
    }

    public void testSubroutineWithLocalVariablesExtraction() {
        doTest(() -> getNextList(getClosestStatementListByText("Magic number")),
                "do-magic", RakuCodeBlockType.ROUTINE);
    }

    public void testSubroutineWithTypedLocalVariablesExtraction() {
        doTest(() -> getNextList(getClosestStatementListByText("Magic number")),
                "do-magic", RakuCodeBlockType.ROUTINE);
    }

    public void testLocalDeclarationsAreNotPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("inner")),
                "extracted", RakuCodeBlockType.ROUTINE);
    }

    public void testSelfInSameClassMethodIsUntouched() {
        doTest(() -> getNextList(getNextList(getClosestStatementListByText("self"))),
                "inner", RakuCodeBlockType.METHOD);
    }

    public void testSelfInSubroutineIsPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("self")),
                "foo", RakuCodeBlockType.ROUTINE);
    }

    public void testSelfInAnotherClassIsPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("self")),
                "foo", RakuCodeBlockType.METHOD);
    }

    public void testAttributesToSubArePassed() {
        doTest(() -> getNextList(getClosestStatementListByText("$!")),
                "foo", RakuCodeBlockType.ROUTINE);
    }

    public void testAttributesToNewNearMethodAreNotPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("say $!")),
                "two", RakuCodeBlockType.PRIVATEMETHOD);
    }

    public void testAttributesToMethodLexicalSubAreNotPassed() {
        doTest(() -> getClosestStatementListByText("say $!"),
                "inner-lexical", RakuCodeBlockType.ROUTINE);
    }

    public void testAttributesArePassedToOuterClass() {
        doTest(() -> getNextList(getNextList(getClosestStatementListByText("say $!"))),
                "outer", RakuCodeBlockType.METHOD);
    }

    public void testLexicalSubBeingPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("a(5)")),
                "with-a-lexical", RakuCodeBlockType.ROUTINE);
    }

    public void testLexicalSubsAreDifferentiated() {
        doTest(() -> getNextList(getClosestStatementListByText("will be")),
                "extracted", RakuCodeBlockType.ROUTINE);
    }

    public void testVarUsedInDeclarationIsPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("$var.key")),
                "foo", RakuCodeBlockType.PRIVATEMETHOD);
    }

    public void testVarsUsedAreNotDuplicated() {
        doTest(() -> getNextList(getClosestStatementListByText("$foo")),
                "foo", RakuCodeBlockType.ROUTINE);
    }

    public void testVarRenaming() {
        doTest(() -> getNextList(getClosestStatementListByText("say $aaa")),
               "foo-bar", RakuCodeBlockType.ROUTINE,
               (data) -> {
                   data.variables[0].parameterName = "$bbb";
                   return data;
               });
    }

    public void testVarsSwapping() {
        doTest(() -> getNextList(getClosestStatementListByText("say $one")),
               "sum", RakuCodeBlockType.ROUTINE,
               (data) -> {
                   RakuVariableData temp = data.variables[0];
                   data.variables[0] = data.variables[1];
                   data.variables[1] = temp;
                   return data;
               });
    }

    public void testHeredoc() {
        doTest(() -> getClosestStatementListByText("END"),
               "heredoc", RakuCodeBlockType.ROUTINE);
    }

    public void testMathExpression() {
        doTest(() -> getClosestStatementListByText("say"),
                "math", RakuCodeBlockType.ROUTINE);
    }

    public void testFullMathExpression() {
        doTest(() -> getClosestStatementListByText("say"),
                "math", RakuCodeBlockType.ROUTINE, 1);
    }

    public void testTopMathExpression() {
        doTest(() -> getClosestStatementListByText("say"),
                "math", RakuCodeBlockType.ROUTINE, 2);
    }

    public void testMathExpressionFromSelection() {
        doTest(() -> getClosestStatementListByText("say"),
                "math", RakuCodeBlockType.ROUTINE);
    }

    public void testFullMathExpressionFromSelection() {
        doTest(() -> getClosestStatementListByText("say"),
                "math", RakuCodeBlockType.ROUTINE, 1);
    }

    public void testCallchain() {
        doTest(() -> getClosestStatementListByText("foo"),
                "cond", RakuCodeBlockType.ROUTINE, 0);
    }

    public void testCallchainFromSelection1() {
        doTest(() -> getClosestStatementListByText("foo"),
                "cond", RakuCodeBlockType.ROUTINE, 1);
    }

    public void testCallchainFromSelection2() {
        doTest(() -> getClosestStatementListByText("foo"),
                "cond", RakuCodeBlockType.ROUTINE);
    }

    public void testConstructWithBracesExtractionAsLastExpr() {
        doTest(() -> getClosestStatementListByText("method"),
               "foo", RakuCodeBlockType.METHOD, 0);
    }

    // Helper methods
    /**
     * Gets innermost statement list in an opened file around a line of text passed
     */
    private RakuStatementList getClosestStatementListByText(String text) {
        return myFixture.findElementByText(text, RakuStatementList.class);
    }

    private static RakuStatementList getNextList(RakuStatementList list) {
        return PsiTreeUtil.getParentOfType(list, RakuStatementList.class, true);
    }

    private void doScopeTest(String text, RakuCodeBlockType type, Consumer<List<RakuStatementList>> check) {
        myFixture.configureByFile(getTestName(true) + ".p6");
        PsiElement start = myFixture.findElementByText(text, PsiElement.class);
        List<RakuStatementList> scopes = (new RakuExtractCodeBlockHandlerMock(type)).getPossibleScopes(new PsiElement[]{start});
        check.consume(scopes);
    }

    private void doTest(Producer<RakuStatementList> getScope, String name, RakuCodeBlockType type) {
        doTest(getScope, name, type, null);
    }

    private void doTest(Producer<RakuStatementList> getScope, String name, RakuCodeBlockType type, Function<NewCodeBlockData, NewCodeBlockData> userAction) {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        RakuStatementList scope = getScope.produce();
        RakuExtractCodeBlockHandlerMock handler = new RakuExtractCodeBlockHandlerMock(type, scope, name, userAction);
        handler.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResultByFile(getTestName(true) + ".p6", true);
    }

    private void doTest(Producer<RakuStatementList> getScope, String name, RakuCodeBlockType type, int exprLevel) {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        RakuStatementList scope = getScope.produce();
        RakuExtractCodeBlockHandlerMock handler = new RakuExtractCodeBlockHandlerMock(type, scope, name, exprLevel);
        handler.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResultByFile(getTestName(true) + ".p6", true);
    }

    private static class RakuExtractCodeBlockHandlerMock extends RakuExtractCodeBlockHandler {
        private final Function<NewCodeBlockData, NewCodeBlockData> userAction;
        RakuStatementList parent;
        private final String name;
        private int myExpressionTargetIndex;

        RakuExtractCodeBlockHandlerMock(RakuCodeBlockType type,
                                        RakuStatementList parent,
                                        String name,
                                        int expressionTargetIndex) {
            super(type);
            this.parent = parent;
            this.name = name;
            this.userAction = null;
            this.myExpressionTargetIndex = expressionTargetIndex;
        }

        RakuExtractCodeBlockHandlerMock(RakuCodeBlockType type,
                                        RakuStatementList parent,
                                        String name,
                                        Function<NewCodeBlockData, NewCodeBlockData> userAction) {
            super(type);
            this.parent = parent;
            this.name = name;
            this.userAction = userAction;
        }

        public RakuExtractCodeBlockHandlerMock(RakuCodeBlockType type) {
            super(type);
            userAction = null;
            name = "";
        }

        @Override
        protected void invokeWithStatements(@NotNull Project project, Editor editor, PsiFile file, PsiElement[] elementsToExtract) {
            invokeWithScope(project, editor, parent, elementsToExtract);
        }

        @Override
        protected NewCodeBlockData getNewBlockData(Project project, RakuStatementList parentToCreateAt, PsiElement[] elements) {
            NewCodeBlockData data = new NewCodeBlockData(myCodeBlockType, name, getCapturedVariables(parent, elements));
            data.containsExpression = isExpr;
            data.wantsSemicolon = isExpr && elements.length == 1 && checkNeedsSemicolon(elements[0]);
            if (userAction == null)
                return data;
            else
                return userAction.fun(data);
        }

        @Override
        protected PsiElement[] getExpressionsFromSelection(PsiFile file, Editor editor, @NotNull PsiElement commonParent, PsiElement fullStatementBackup) {
            List<PsiElement> targets = getExpressionTargets(commonParent);
            PsiElement psiElement = targets.get(myExpressionTargetIndex);
            isExpr = !(psiElement instanceof RakuStatement);
            return new PsiElement[]{psiElement};
        }

        @Override
        protected PsiElement[] getElementsFromCaret(PsiFile file, Editor editor) {
            int offset = editor.getCaretModel().getOffset();
            PsiElement element = file.findElementAt(offset);
            if (element == null) {
                return PsiElement.EMPTY_ARRAY;
            }
            List<PsiElement> targets = getExpressionTargets(element.getParent());
            PsiElement psiElement = targets.get(myExpressionTargetIndex);
            isExpr = !(psiElement instanceof RakuStatement);
            return new PsiElement[]{psiElement};
        }
    }
}
