package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.List;

public class PodCompletionTest extends CommaFixtureTestCase {
    public void testBasicPodCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "=<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> directives = myFixture.getLookupElementStrings();
        assertNotNull(directives);
        assertContainsElements(directives, "begin", "end", "table", "head1", "for");
    }
}
