package edument.perl6idea.refactoring;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.Consumer;
import com.intellij.util.Function;
import com.intellij.util.Producer;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtractCodeBlockTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/block-extract";
    }

    public void testMethodSingleScopePresence() {
        doScopeTest("start", Perl6CodeBlockType.METHOD,
                (scopes) -> {
                    assertEquals(1, scopes.size());
                    checkPackage(scopes, 0, "A", "class");
                });
    }

    public void testMethodOuterClassScopePresence() {
        doScopeTest("start", Perl6CodeBlockType.METHOD,
                (scopes) -> {
                    assertEquals(4, scopes.size());
                    checkPackage(scopes, 0, "M", "monitor");
                    checkPackage(scopes, 1, "G", "grammar");
                    checkPackage(scopes, 2, "R", "role");
                    checkPackage(scopes, 3, "C", "class");
                });
    }

    public void testSubFilePresence() {
        doScopeTest("'start'", Perl6CodeBlockType.ROUTINE,
                (scopes) -> {
                    assertEquals(1, scopes.size());
                    PsiElement decl = PsiTreeUtil.getParentOfType(scopes.get(0), Perl6PackageDecl.class, Perl6RoutineDecl.class, Perl6File.class);
                    assertTrue(decl instanceof Perl6File);
                });
    }

    public void testSubNestedScopePresence() {
        doScopeTest("'start'", Perl6CodeBlockType.ROUTINE,
                (scopes) -> {
                    assertEquals(4, scopes.size());
                    checkPackage(scopes, 2, "ABC", "class");
                });
    }

    private void checkPackage(List<Perl6StatementList> scopes, int index, String packageName, String packageKind) {
        PsiElement decl = PsiTreeUtil.getParentOfType(scopes.get(index), Perl6PackageDecl.class, Perl6RoutineDecl.class, Perl6File.class);
        assertTrue(decl instanceof Perl6PackageDecl);
        assertNotNull(decl);
        assertEquals(packageName, ((Perl6PackageDecl)decl).getPackageName());
        assertEquals(packageKind, ((Perl6PackageDecl)decl).getPackageKind());
    }

    public void testTopFileSubroutineExtraction() {
        doTest(() -> getClosestStatementListByText("say 1"),
                "foo-bar", Perl6CodeBlockType.ROUTINE);
    }

    public void testTopFileMethodImpossible() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () ->
                doTest(() -> getClosestStatementListByText("say 1"),
                        "foo-bar", Perl6CodeBlockType.METHOD));
    }

    public void testInMethodMethodExtraction() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () ->
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
        handler.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), (DataContext) null);
        myFixture.checkResultByFile(getTestName(true) + ".p6", true);
    }

    private static class Perl6ExtractCodeBlockHandlerMock extends Perl6ExtractCodeBlockHandler {
        private final Function<NewCodeBlockData, NewCodeBlockData> userAction;
        Perl6StatementList parent;
        private final String name;

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
        protected void invoke(@NotNull Project project, Editor editor, PsiFile file, PsiElement[] elements) {
            invoke(project, editor, file, parent, elements);
        }

        @Override
        protected NewCodeBlockData getNewBlockData(Project project, Perl6StatementList parentToCreateAt, PsiElement[] elements) {
            NewCodeBlockData data = new NewCodeBlockData(myCodeBlockType, name, getCapturedVariables(parent, elements));
            if (userAction == null)
                return data;
            else
                return userAction.fun(data);
        }
    }
}
