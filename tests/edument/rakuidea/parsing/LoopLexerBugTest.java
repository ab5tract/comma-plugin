package edument.rakuidea.parsing;

import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;

public class LoopLexerBugTest extends CommaFixtureTestCase {
    public void testLexerBug1() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "loop (my $a = 5; $a < <caret>)");
        myFixture.type("5Foo");
        myFixture.checkResult("loop (my $a = 5; $a < 5Foo)");
    }
}
