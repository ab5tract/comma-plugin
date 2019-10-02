package edument.perl6idea.documentation;

import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import edument.perl6idea.CommaFixtureTestCase;

public class DocumentationTest extends CommaFixtureTestCase {
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
        doTest("Base class for magicians\nTrailing one");
    }

    public void testQuickDocsRole() {
        doTest("Base role for magicians\nTrailing one");
    }

    public void testQuickDocsGrammar() {
        doTest("Base grammar for magicians\nTrailing one");
    }

    public void testQuickDocsRoutine() {
        doTest("Fight mechanics\nMagicians only, no mortals.");
    }

    public void testQuickDocsAttribute() {
        doTest("One\nTwo\nThree!\nVery important variable");
    }

    public void testQuickDocsPrivateMethod() {
        doTest("Fight mechanics\nMagicians only, no mortals.");
    }

    public void testQuickDocsMethod() {
        doTest("Fight mechanics\nMagicians only, no mortals.");
    }

    public void testQuickDocsLongPre() {
        doTest("Opening.\nSecond sentence");
    }

    public void testQuickDocsLongPost() {
        doTest("Opening.\nSecond sentence");
    }

    public void testQuickDocsLong() {
        doTest("Opening.\nSecond sentence\nEnding.\nThird sentence");
    }
}
