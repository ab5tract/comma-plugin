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
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>(^10).roll;</selection>");
        Perl6ConstantExtractionHandlerMock handler = new Perl6ConstantExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my constant $bar = (^10).roll;");
    }

    public void testWhitespaceIsHandled() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>if True { say 10 } else { say 'no' }   </selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do if True { say 10 } else { say 'no' }   ;");
    }

    public void testIfStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>if True { say 10 } else { say 'no' }</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do if True { say 10 } else { say 'no' };");
    }

    public void testUnlessStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>unless False { say 10 }</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do unless False { say 10 };");
    }


    public void testWithStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>with $foo { say 10 };</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do with $foo { say 10 };");
    }

    public void testWithoutStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>without $foo { say 10 };</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do without $foo { say 10 };");
    }

    public void testWhenStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>when $foo eq 50 { 10 };</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do when $foo eq 50 { 10 };");
    }

    public void testForStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>for 1..3 { 10 }</selection>;");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do for 1..3 { 10 };");
    }

    public void testGivenStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>given $foo { when 1 { say 10 } }</selection>;");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do given $foo { when 1 { say 10 } };");
    }

    public void testLoopStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>loop (my $i = 0; $i < 10; $i++) { say $i; }</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do loop (my $i = 0; $i < 10; $i++) { say $i; };");
    }

    public void testWhileStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>while $foo != 0 { say 10 };</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do while $foo != 0 { say 10 };");
    }

    public void testUntilStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>until $foo eq 'Foo' { say 10 };</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do until $foo eq 'Foo' { say 10 };");
    }

    public void testRepeatStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>repeat { say 10 } until True</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do repeat { say 10 } until True;");
    }

    public void testCorrectAnchorSelection() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say 3 * (<selection>10 + 10</selection>);");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = 10 + 10;say 3 * ($bar);");
    }

    public void testPhaserExtractionFailing() {
    }

    public void testImportsExtractionFailing() {
    }
}
