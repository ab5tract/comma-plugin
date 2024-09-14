package edument.rakuidea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PodCompletionTest extends CommaFixtureTestCase {
    public void testPodCompletion() {
        testContains("=<caret>", "begin", "for", "head1", "finish");
        testContains("=begin\n=<caret>", "end");
        testContains("=begin h<caret>", "head1");
        testContains("=begin d<caret>", "defn");
        testContains("=for <caret>", "head1", "defn", "code");
        testDoesntContain("=for <caret>", "begin", "end", "finish");
    }

    private void testContains(String text, String... contains) {
        List<String> directives = getStrings(text);
        assertContainsElements(directives, contains);
    }

    private void testDoesntContain(String text, String... contains) {
        List<String> directives = getStrings(text);
        assertDoesntContain(directives, contains);
    }

    @NotNull
    private List<String> getStrings(String text) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, text);
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> directives = myFixture.getLookupElementStrings();
        assertNotNull(directives);
        return directives;
    }
}
