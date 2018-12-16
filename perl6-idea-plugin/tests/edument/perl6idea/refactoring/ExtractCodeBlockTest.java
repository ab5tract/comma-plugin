package edument.perl6idea.refactoring;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.Function;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.Perl6StatementList;
import org.jetbrains.annotations.NotNull;

public class ExtractCodeBlockTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/block-extract";
    }

    public void testTopFileSubroutineExtraction() {
        doTest((a) -> PsiTreeUtil.getParentOfType(myFixture.getElementAtCaret(), Perl6StatementList.class),
                "foo-bar", Perl6CodeBlockType.ROUTINE);
    }

    private void doTest(Function<Void, Perl6StatementList> getScope, String name, Perl6CodeBlockType type) {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        Perl6StatementList scope = getScope.fun(null);
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
