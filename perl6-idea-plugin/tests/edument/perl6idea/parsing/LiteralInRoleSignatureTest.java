package edument.perl6idea.parsing;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class LiteralInRoleSignatureTest extends LightPlatformCodeInsightFixtureTestCase {
    public void testLexerBug1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class A does B[<caret>]");
        myFixture.type("1");
        myFixture.checkResult("class A does B[1]");
    }
}
