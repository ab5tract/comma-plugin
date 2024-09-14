package edument.rakuidea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;

import java.util.List;

public class DynamicVariablesTest extends CommaFixtureTestCase {
    public void testIntegration() {
        doTest("sub { my $*DYNAMIC-VAR1 }; { my $*DYNAMIC-VAR2; }; { say $*DY<caret> }",
               "$*DYNAMIC-VAR1", "$*DYNAMIC-VAR2");
    }

    private void doTest(String text, String... elems) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, text);
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertContainsElements(methods, elems);
    }

}
