package edument.rakuidea.parsing;

import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;

public class LiteralInRoleSignatureTest extends CommaFixtureTestCase {
    public void testLexerBug1() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "class A does B[<caret>]");
        myFixture.type("1");
        myFixture.checkResult("class A does B[1]");
    }
}
