package edument.perl6idea.refactoring;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6ExtractionMockHandler;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.refactoring.introduce.variable.Perl6IntroduceVariableHandler;

public class ExtractDeclarationTest extends LightPlatformCodeInsightFixtureTestCase {
    public void testExpressionVariableExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say pi; say 1<selection>0 + 5</selection>0; say 10 + 50;");
        Perl6IntroduceVariableHandler handler = new Perl6ExtractionMockHandler(null, null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("say pi; my $foo = 10 + 50;say $foo; say $foo;");
    }
}
