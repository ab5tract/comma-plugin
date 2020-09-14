package edument.perl6idea.parsing;

import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class LoopLexerBugTest extends CommaFixtureTestCase {
    public void testLexerBug1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "loop (my $a = 5; $a < <caret>)");
        myFixture.type("5Foo");
        myFixture.checkResult("loop (my $a = 5; $a < 5Foo)");
    }
}
