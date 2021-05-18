package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.List;

public class DynamicVariablesTest extends CommaFixtureTestCase {
    public void testIntegration() {
        doTest("sub { my $*DYNAMIC-VAR1 }; { my $*DYNAMIC-VAR2; }; { say $*DY<caret> }",
               "$*DYNAMIC-VAR1", "$*DYNAMIC-VAR2");
    }

    private void doTest(String text, String... elems) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, text);
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertContainsElements(methods, elems);
    }

}
