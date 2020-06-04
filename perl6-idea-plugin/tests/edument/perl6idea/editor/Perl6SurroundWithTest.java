package edument.perl6idea.editor;

import com.intellij.codeInsight.generation.surroundWith.SurroundWithHandler;
import com.intellij.lang.LanguageSurrounders;
import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiElement;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.surrountWith.descriptors.Perl6RegexGroupSurrounder;
import edument.perl6idea.surrountWith.descriptors.Perl6RegexNamedSurrounder;
import edument.perl6idea.surrountWith.descriptors.Perl6RegexPositionalSurrounder;
import edument.perl6idea.surrountWith.descriptors.surrounder.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Perl6SurroundWithTest extends CommaFixtureTestCase {
    @NotNull
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/surroundWith";
    }

    public void testIfSurround() {
        doTest(new Perl6IfSurrounder(true));
    }

    public void testWithSurround() {
        doTest(new Perl6WithSurrounder(true));
    }

    public void testUnlessSurround() {
        doTest(new Perl6UnlessSurrounder(true));
    }

    public void testWithoutSurround() {
        doTest(new Perl6WithoutSurrounder(true));
    }

    public void testGivenSurround() {
        doTest(new Perl6GivenSurrounder(true));
    }

    public void testForSurround() {
        doTest(new Perl6ForSurrounder(true));
    }

    public void testWheneverSurround() {
        doTest(new Perl6WheneverSurrounder(true));
    }

    public void testWhenSurround() {
        doTest(new Perl6WhenSurrounder(true));
    }

    public void testTrySurround() {
        doTest(new Perl6TrySurrounder(true));
    }

    public void testTryWhenSurround() {
        doTest(new Perl6TryCatchWhenSurrounder(true));
    }

    public void testTryDefaultSurround() {
        doTest(new Perl6TryCatchDefaultSurrounder(true));
    }

    public void testStartSurround() {
        doTest(new Perl6StartSurrounder(true));
    }

    public void testPointyBlockSurround() {
        doTest(new Perl6PointyBlockSurrounder(true));
    }

    public void testHashComposerSurround() {
        doTest(new Perl6HashSurrounder(true));
    }

    public void testArrayComposerSurround() {
        doTest(new Perl6ArraySurrounder(true));
    }

    public void testArrayContextualizerSurround() {
        doTest(new Perl6ArrayContextSurrounder(true));
    }

    public void testHashContextualizerSurround() {
        doTest(new Perl6HashContextSurrounder(true));
    }

    public void testIfExpr() {
        doTest(new Perl6IfSurrounder(false));
    }

    public void testUnlessExpr() {
        doTest(new Perl6UnlessSurrounder(false));
    }

    public void testTryExpr() {
        doTest(new Perl6TrySurrounder(false));
    }

    public void testStartExpr() {
        doTest(new Perl6StartSurrounder(false));
    }

    public void testPointyBlockExpr() {
        doTest(new Perl6PointyBlockSurrounder(false));
    }

    public void testHashContextExpr() {
        doTest(new Perl6HashContextSurrounder(false));
    }

    public void testRegexGroup() {
        doTest(new Perl6RegexGroupSurrounder());
    }

    public void testRegexPositional() {
        doTest(new Perl6RegexPositionalSurrounder());
    }

    public void testRegexNamed() {
        doTest(new Perl6RegexNamedSurrounder());
    }

    private void doTest(Surrounder surrounder) {
        myFixture.configureByFile(getTestName(true) + "Before.p6");
        List<SurroundDescriptor> descriptors = LanguageSurrounders.INSTANCE.allForLanguage(Perl6Language.INSTANCE);
        SelectionModel selectionModel = myFixture.getEditor().getSelectionModel();
        PsiElement[] elements = null;
        for (SurroundDescriptor descriptor : descriptors) {
            elements = descriptor.getElementsToSurround(
                myFixture.getFile(), selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());
            if (elements.length > 0)
                break;
        }
        assertNotNull(elements);
        assertFalse(elements.length == 0);
        assertTrue(surrounder.isApplicable(elements));
        SurroundWithHandler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), surrounder);
        myFixture.checkResultByFile(getTestName(true) + ".p6");
    }
}
