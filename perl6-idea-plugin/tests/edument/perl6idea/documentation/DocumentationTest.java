package edument.perl6idea.documentation;

import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class DocumentationTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/docs";
    }

    public void doTest(String result) {
        myFixture.configureByFile(getTestName(true) + ".p6");
        PsiElement element = myFixture.getElementAtCaret();
        DocumentationProvider provider = DocumentationManager.getProviderFromElement(element);
        String quickNavigateInfo = provider.getQuickNavigateInfo(element, null);
        assertEquals(result, quickNavigateInfo);
    }

    public void testQuickDocsClass() {
        doTest("Base class for magicians\nTrailing one\n");
    }

    public void testQuickDocsRole() {
        doTest("Base role for magicians\nTrailing one\n");
    }

    public void testQuickDocsGrammar() {
        doTest("Base grammar for magicians\nTrailing one\n");
    }

    public void testQuickDocsRoutine() {
        doTest("Fight mechanics\nMagicians only, no mortals.\n");
    }

    public void testQuickDocsAttribute() {
        doTest("One\nTwo\nThree!\nVery important variable\n");
    }

    public void testQuickDocsPrivateMethod() {
        doTest("Fight mechanics\nMagicians only, no mortals.\n");
    }

    public void testQuickDocsMethod() {
        doTest("Fight mechanics\nMagicians only, no mortals.\n");
    }
}
