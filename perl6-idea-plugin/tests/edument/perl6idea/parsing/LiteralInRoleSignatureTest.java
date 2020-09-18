package edument.perl6idea.parsing;

import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class LiteralInRoleSignatureTest extends CommaFixtureTestCase {
    public void testLexerBug1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class A does B[<caret>]");
        myFixture.type("1");
        myFixture.checkResult("class A does B[1]");
    }
}
