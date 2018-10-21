package edument.perl6idea.refactoring;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6ConstantExtractionHandlerMock;
import edument.perl6idea.Perl6VariableExtractionHandlerMock;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.refactoring.introduce.variable.Perl6IntroduceVariableHandler;

public class ExtractDeclarationTest extends LightPlatformCodeInsightFixtureTestCase {
    public void testExpressionVariableExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say pi; say 1<selection>0 + 5</selection>0; say 10 + 50;");
        Perl6IntroduceVariableHandler handler = new Perl6VariableExtractionHandlerMock(null, null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("say pi; my $foo = 10 + 50;say $foo; say $foo;");
    }

    public void testExpressionVariableExtractionFromCursor() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "foo(\"st<selection>ring-v</selection>alue\");");
        Perl6IntroduceVariableHandler handler = new Perl6VariableExtractionHandlerMock(null, null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $foo = \"string-value\";foo($foo);");
    }

    public void testExpressionConstantExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say pi; say 1<selection>0 + 5</selection>0; say 10 + 50;");
        Perl6ConstantExtractionHandlerMock handler = new Perl6ConstantExtractionHandlerMock(null, null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("say pi; my constant $foo = 10 + 50;say $foo; say $foo;");
    }

    public void testStatementVariableExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>(^10).roll</selection>;");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $foo = (^10).roll;");
    }

    public void testStatementVariableExtractionFull() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>(^10).roll;</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = (^10).roll;");
    }

    public void testStatementConstantExtraction() {
    }

    public void testControlLikeStatementExtraction() {
    }

    public void testPhaserExtractionFailing() {
    }

    public void testImportsExtractionFailing() {
    }

    public void testForExtraction() {
    }

    public void testGatherExtraction() {
    }
}
