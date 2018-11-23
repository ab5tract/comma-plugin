package edument.perl6idea.parsing;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class LoopLexerBug extends LightPlatformCodeInsightFixtureTestCase {
    public void testLexerBug1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "loop (my $a = 5; $a < <caret>)");
        myFixture.type("5Foo");
        myFixture.checkResult("loop (my $a = 5; $a < 5Foo)");
    }
}
