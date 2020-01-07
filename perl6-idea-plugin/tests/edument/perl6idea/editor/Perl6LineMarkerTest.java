package edument.perl6idea.editor;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import edument.perl6idea.CommaFixtureTestCase;

import java.util.List;

public class Perl6LineMarkerTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/editor";
    }

    public void testRoles() {
        doTest(4);
    }

    public void testClasses() {
        doTest(6);
    }

    public void testNameUsages() {
        doTest(2);
    }

    private void doTest(int size) {
        myFixture.configureByFile(getTestName(true) + ".pm6");
        myFixture.doHighlighting();
        List<LineMarkerInfo<?>> list = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), getProject());
        assertEquals(list.size(), size);
    }
}
