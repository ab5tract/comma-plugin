package edument.perl6idea.refactoring;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.Consumer;
import com.intellij.util.Function;
import com.intellij.util.Producer;
import edument.perl6idea.psi.*;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtractCodeBlockTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/block-extract";
    }

    public void testMethodSingleScopePresence() {
        doScopeTest("start", Perl6CodeBlockType.METHOD,
                (scopes) -> {
                    TestCase.assertEquals(1, scopes.size());
                    checkPackage(scopes, 0, "A", "class");
                });
    }

    public void testMethodOuterClassScopePresence() {
        doScopeTest("start", Perl6CodeBlockType.METHOD,
                (scopes) -> {
                    TestCase.assertEquals(4, scopes.size());
                    checkPackage(scopes, 0, "M", "monitor");
                    checkPackage(scopes, 1, "G", "grammar");
                    checkPackage(scopes, 2, "R", "role");
                    checkPackage(scopes, 3, "C", "class");
                });
    }

    public void testSubFilePresence() {
        doScopeTest("'start'", Perl6CodeBlockType.ROUTINE,
                (scopes) -> {
                    TestCase.assertEquals(1, scopes.size());
                    PsiElement decl = PsiTreeUtil.getParentOfType(scopes.get(0), Perl6PackageDecl.class, Perl6RoutineDecl.class, Perl6File.class);
                    TestCase.assertTrue(decl instanceof Perl6File);
                });
    }

    public void testSubNestedScopePresence() {
        doScopeTest("'start'", Perl6CodeBlockType.ROUTINE,
                (scopes) -> {
                    TestCase.assertEquals(4, scopes.size());
                    checkPackage(scopes, 2, "ABC", "class");
                });
    }

    private void checkPackage(List<Perl6StatementList> scopes, int index, String packageName, String packageKind) {
        PsiElement decl = PsiTreeUtil.getParentOfType(scopes.get(index), Perl6PackageDecl.class, Perl6RoutineDecl.class, Perl6File.class);
        TestCase.assertTrue(decl instanceof Perl6PackageDecl);
        TestCase.assertNotNull(decl);
        TestCase.assertEquals(packageName, ((Perl6PackageDecl)decl).getPackageName());
        TestCase.assertEquals(packageKind, ((Perl6PackageDecl)decl).getPackageKind());
    }

    public void testTopFileSubroutineExtraction() {
        doTest(() -> getClosestStatementListByText("say 1"),
                "foo-bar", Perl6CodeBlockType.ROUTINE);
    }

    public void testTopFileMethodImpossible() {
        UsefulTestCase.assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () ->
                doTest(() -> getClosestStatementListByText("say 1"),
                        "foo-bar", Perl6CodeBlockType.METHOD));
    }

    public void testInMethodMethodExtraction() {
        UsefulTestCase.assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () ->
                doTest(() -> getClosestStatementListByText("foo"),
                        "foo-bar", Perl6CodeBlockType.PRIVATEMETHOD));
    }

    public void testInClassMethodExtraction() {
        doTest(() -> getNextList(getClosestStatementListByText("say 'foo'")),
                "foo-bar", Perl6CodeBlockType.METHOD);
    }

    public void testInClassPrivateMethodExtraction() {
        doTest(() -> getNextList(getClosestStatementListByText("say 'foo'")),
                "foo-bar", Perl6CodeBlockType.PRIVATEMETHOD);
    }

    public void testSubroutineExtractionTwoLevelsUp() {
        doTest(() -> getNextList(getNextList(getClosestStatementListByText("say 'foo'"))),
                "outer-sub", Perl6CodeBlockType.ROUTINE);
    }

    public void testSubroutineWithLocalVariablesExtraction() {
        doTest(() -> getNextList(getClosestStatementListByText("Magic number")),
                "do-magic", Perl6CodeBlockType.ROUTINE);
    }

    public void testSubroutineWithTypedLocalVariablesExtraction() {
        doTest(() -> getNextList(getClosestStatementListByText("Magic number")),
                "do-magic", Perl6CodeBlockType.ROUTINE);
    }

    public void testLocalDeclarationsAreNotPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("inner")),
                "extracted", Perl6CodeBlockType.ROUTINE);
    }

    public void testSelfInSameClassMethodIsUntouched() {
        doTest(() -> getNextList(getNextList(getClosestStatementListByText("self"))),
                "inner", Perl6CodeBlockType.METHOD);
    }

    public void testSelfInSubroutineIsPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("self")),
                "foo", Perl6CodeBlockType.ROUTINE);
    }

    public void testSelfInAnotherClassIsPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("self")),
                "foo", Perl6CodeBlockType.METHOD);
    }

    public void testAttributesToSubArePassed() {
        doTest(() -> getNextList(getClosestStatementListByText("$!")),
                "foo", Perl6CodeBlockType.ROUTINE);
    }

    public void testAttributesToNewNearMethodAreNotPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("say $!")),
                "two", Perl6CodeBlockType.PRIVATEMETHOD);
    }

    public void testAttributesToMethodLexicalSubAreNotPassed() {
        doTest(() -> getClosestStatementListByText("say $!"),
                "inner-lexical", Perl6CodeBlockType.ROUTINE);
    }

    public void testAttributesArePassedToOuterClass() {
        doTest(() -> getNextList(getNextList(getClosestStatementListByText("say $!"))),
                "outer", Perl6CodeBlockType.METHOD);
    }

    public void testLexicalSubBeingPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("a(5)")),
                "with-a-lexical", Perl6CodeBlockType.ROUTINE);
    }

    public void testLexicalSubsAreDifferentiated() {
        doTest(() -> getNextList(getClosestStatementListByText("will be")),
                "extracted", Perl6CodeBlockType.ROUTINE);
    }

    public void testVarUsedInDeclarationIsPassed() {
        doTest(() -> getNextList(getClosestStatementListByText("$var.key")),
                "foo", Perl6CodeBlockType.PRIVATEMETHOD);
    }

    public void testVarsUsedAreNotDuplicated() {
        doTest(() -> getNextList(getClosestStatementListByText("$foo")),
                "foo", Perl6CodeBlockType.ROUTINE);
    }

    public void testVarRenaming() {
        doTest(() -> getNextList(getClosestStatementListByText("say $aaa")),
               "foo-bar", Perl6CodeBlockType.ROUTINE,
               (data) -> {
                   data.variables[0].parameterName = "$bbb";
                   return data;
               });
    }

    public void testVarsSwapping() {
        doTest(() -> getNextList(getClosestStatementListByText("say $one")),
               "sum", Perl6CodeBlockType.ROUTINE,
               (data) -> {
                   Perl6VariableData temp = data.variables[0];
                   data.variables[0] = data.variables[1];
                   data.variables[1] = temp;
                   return data;
               });
    }

    public void testHeredoc() {
        doTest(() -> getClosestStatementListByText("END"),
               "heredoc", Perl6CodeBlockType.ROUTINE);
    }

    public void testMathExpression() {
        doTest(() -> getClosestStatementListByText("say"),
                "math", Perl6CodeBlockType.ROUTINE);
    }

    public void testFullMathExpression() {
        doTest(() -> getClosestStatementListByText("say"),
                "math", Perl6CodeBlockType.ROUTINE, 1);
    }

    public void testTopMathExpression() {
        doTest(() -> getClosestStatementListByText("say"),
                "math", Perl6CodeBlockType.ROUTINE, 2);
    }

    public void testMathExpressionFromSelection() {
        doTest(() -> getClosestStatementListByText("say"),
                "math", Perl6CodeBlockType.ROUTINE);
    }

    public void testFullMathExpressionFromSelection() {
        doTest(() -> getClosestStatementListByText("say"),
                "math", Perl6CodeBlockType.ROUTINE, 1);
    }

    public void testCallchain() {
        doTest(() -> getClosestStatementListByText("foo"),
                "cond", Perl6CodeBlockType.ROUTINE, 0);
    }

    public void testCallchainFromSelection1() {
        doTest(() -> getClosestStatementListByText("foo"),
                "cond", Perl6CodeBlockType.ROUTINE, 1);
    }

    public void testCallchainFromSelection2() {
        doTest(() -> getClosestStatementListByText("foo"),
                "cond", Perl6CodeBlockType.ROUTINE);
    }

    // Helper methods
    /**
     * Gets innermost statement list in an opened file around a line of text passed
     */
    private Perl6StatementList getClosestStatementListByText(String text) {
        return myFixture.findElementByText(text, Perl6StatementList.class);
    }

    private static Perl6StatementList getNextList(Perl6StatementList list) {
        return PsiTreeUtil.getParentOfType(list, Perl6StatementList.class, true);
    }

    private void doScopeTest(String text, Perl6CodeBlockType type, Consumer<List<Perl6StatementList>> check) {
        myFixture.configureByFile(getTestName(true) + ".p6");
        PsiElement start = myFixture.findElementByText(text, PsiElement.class);
        List<Perl6StatementList> scopes = (new Perl6ExtractCodeBlockHandlerMock(type)).getPossibleScopes(new PsiElement[]{start});
        check.consume(scopes);
    }

    private void doTest(Producer<Perl6StatementList> getScope, String name, Perl6CodeBlockType type) {
        doTest(getScope, name, type, null);
    }

    private void doTest(Producer<Perl6StatementList> getScope, String name, Perl6CodeBlockType type, Function<NewCodeBlockData, NewCodeBlockData> userAction) {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        Perl6StatementList scope = getScope.produce();
        Perl6ExtractCodeBlockHandlerMock handler = new Perl6ExtractCodeBlockHandlerMock(type, scope, name, userAction);
        handler.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResultByFile(getTestName(true) + ".p6", true);
    }

    private void doTest(Producer<Perl6StatementList> getScope, String name, Perl6CodeBlockType type, int exprLevel) {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        Perl6StatementList scope = getScope.produce();
        Perl6ExtractCodeBlockHandlerMock handler = new Perl6ExtractCodeBlockHandlerMock(type, scope, name, exprLevel);
        handler.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResultByFile(getTestName(true) + ".p6", true);
    }

    private static class Perl6ExtractCodeBlockHandlerMock extends Perl6ExtractCodeBlockHandler {
        private final Function<NewCodeBlockData, NewCodeBlockData> userAction;
        Perl6StatementList parent;
        private final String name;
        private int myExpressionTargetIndex;

        Perl6ExtractCodeBlockHandlerMock(Perl6CodeBlockType type,
                                         Perl6StatementList parent,
                                         String name,
                                         int expressionTargetIndex) {
            super(type);
            this.parent = parent;
            this.name = name;
            this.userAction = null;
            this.myExpressionTargetIndex = expressionTargetIndex;
        }

        Perl6ExtractCodeBlockHandlerMock(Perl6CodeBlockType type,
                                         Perl6StatementList parent,
                                         String name,
                                         Function<NewCodeBlockData, NewCodeBlockData> userAction) {
            super(type);
            this.parent = parent;
            this.name = name;
            this.userAction = userAction;
        }

        public Perl6ExtractCodeBlockHandlerMock(Perl6CodeBlockType type) {
            super(type);
            userAction = null;
            name = "";
        }

        @Override
        protected void invokeWithStatements(@NotNull Project project, Editor editor, PsiFile file, PsiElement[] elementsToExtract) {
            invokeWithScope(project, editor, parent, elementsToExtract);
        }

        @Override
        protected NewCodeBlockData getNewBlockData(Project project, Perl6StatementList parentToCreateAt, PsiElement[] elements) {
            NewCodeBlockData data = new NewCodeBlockData(myCodeBlockType, name, getCapturedVariables(parent, elements));
            data.containsExpression = isExpr;
            if (userAction == null)
                return data;
            else
                return userAction.fun(data);
        }

        @Override
        protected PsiElement[] getExpressionsFromSelection(PsiFile file, Editor editor, @NotNull PsiElement commonParent, PsiElement fullStatementBackup) {
            List<PsiElement> targets = getExpressionTargets(commonParent);
            PsiElement psiElement = targets.get(myExpressionTargetIndex);
            isExpr = !(psiElement instanceof Perl6Statement);
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
            isExpr = !(psiElement instanceof Perl6Statement);
            return new PsiElement[]{psiElement};
        }
    }
}
