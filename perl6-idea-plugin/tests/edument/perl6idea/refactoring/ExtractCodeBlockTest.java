package edument.perl6idea.refactoring;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.Producer;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StatementList;
import org.jetbrains.annotations.NotNull;

public class ExtractCodeBlockTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/block-extract";
    }

    public void testTopFileSubroutineExtraction() {
        doTest(() -> getClosestListBySelection("say 1"),
                "foo-bar", Perl6CodeBlockType.ROUTINE);
    }

    public void testTopFileMethodImpossible() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () ->
                doTest(() -> getClosestListBySelection("say 1"),
                        "foo-bar", Perl6CodeBlockType.METHOD));
    }

    public void testInMethodMethodExtraction() {
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () ->
                doTest(() -> getClosestListBySelection("foo"),
                        "foo-bar", Perl6CodeBlockType.PRIVATEMETHOD));
    }

    public void testInClassMethodExtraction() {
        doTest(() -> PsiTreeUtil.getParentOfType(getClosestListBySelection("say 'foo'"), Perl6StatementList.class),
                "foo-bar", Perl6CodeBlockType.PRIVATEMETHOD);
    }

    private Perl6StatementList getClosestListBySelection(String text) {
        return myFixture.findElementByText(text, Perl6StatementList.class);
    }

    private void doTest(Producer<Perl6StatementList> getScope, String name, Perl6CodeBlockType type) {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        Perl6StatementList scope = getScope.produce();
        Perl6ExtractCodeBlockHandlerMock handler = new Perl6ExtractCodeBlockHandlerMock(type, scope, name);
        handler.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), (DataContext) null);
        myFixture.checkResultByFile(getTestName(true) + ".p6");
    }

    private class Perl6ExtractCodeBlockHandlerMock extends Perl6ExtractCodeBlockHandler {
        Perl6StatementList parent;
        private final String name;
        private final Perl6CodeBlockType type;

        Perl6ExtractCodeBlockHandlerMock(Perl6CodeBlockType type, Perl6StatementList parent,
                                         String name) {
            super(type);
            this.parent = parent;
            this.name = name;
            this.type = type;
        }

        @Override
        protected void invoke(@NotNull Project project, Editor editor, PsiFile file, PsiElement[] elements) {
            invoke(project, editor, file, parent, elements);
        }

        @Override
        protected NewCodeBlockData getNewBlockData(Project project) {
            return new NewCodeBlockData(type, name);
        }
    }
}
