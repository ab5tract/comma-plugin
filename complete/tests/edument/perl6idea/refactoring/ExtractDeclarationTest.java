package edument.perl6idea.refactoring;

import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.Perl6ConstantExtractionHandlerMock;
import edument.perl6idea.Perl6VariableExtractionHandlerMock;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.refactoring.introduce.variable.Perl6IntroduceVariableHandler;

public class ExtractDeclarationTest extends CommaFixtureTestCase {
    public void testExpressionVariableExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say pi; say 1<selection>0 + 5</selection>0; say 10 + 50;");
        Perl6IntroduceVariableHandler handler = new Perl6VariableExtractionHandlerMock(null,  "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("say pi;\nmy $foo = 10 + 50;\nsay $foo;\nsay $foo;");
    }

    public void testExpressionVariableExtractionFromCursor() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "foo(\"st<selection>ring-v</selection>alue\");");
        Perl6IntroduceVariableHandler handler = new Perl6VariableExtractionHandlerMock(null,  "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $foo = \"string-value\";\nfoo($foo);");
    }

    public void testExpressionConstantExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say pi; say 1<selection>0 + 5</selection>0; say 10 + 50;");
        Perl6ConstantExtractionHandlerMock handler = new Perl6ConstantExtractionHandlerMock(null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("say pi;\nmy constant $foo = 10 + 50;\nsay $foo;\nsay $foo;");
    }

    public void testStatementVariableExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>(^10).roll</selection>;");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $foo = (^10).roll;\n");
    }

    public void testStatementVariableExtractionFull() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>(^10).roll;</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = (^10).roll;");
    }

    public void testStatementConstantExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>(^10).roll;</selection>");
        Perl6ConstantExtractionHandlerMock handler = new Perl6ConstantExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my constant $bar = (^10).roll;");
    }

    public void testWhitespaceIsHandled() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>if True { say 10 } else { say 'no' }   </selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do if True { say 10 } else { say 'no' };\n");
    }

    public void testIfStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>if True { say 10 } else { say 'no' }</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do if True { say 10 } else { say 'no' };\n");
    }

    public void testUnlessStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>unless False { say 10 }</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do unless False { say 10 };\n");
    }


    public void testWithStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>with $foo { say 10 };</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do with $foo { say 10 };");
    }

    public void testWithoutStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>without $foo { say 10 };</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do without $foo { say 10 };");
    }

    public void testWhenStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>when $foo eq 50 { 10 };</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do when $foo eq 50 { 10 };");
    }

    public void testForStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>for 1..3 { 10 }</selection>;");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do for 1..3 { 10 };\n");
    }

    public void testGivenStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>given $foo { when 1 { say 10 } }</selection>;");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do given $foo { when 1 { say 10 } };\n");
    }

    public void testLoopStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>loop (my $i = 0; $i < 10; $i++) { say $i; }</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do loop (my $i = 0; $i < 10; $i++) { say $i; };\n");
    }

    public void testWhileStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>while $foo != 0 { say 10 };</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do while $foo != 0 { say 10 };");
    }

    public void testUntilStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>until $foo eq 'Foo' { say 10 };</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do until $foo eq 'Foo' { say 10 };");
    }

    public void testRepeatStatementExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>repeat { say 10 } until True</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do repeat { say 10 } until True;\n");
    }

    public void testCorrectAnchorSelection() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say 3 * (<selection>10 + 10</selection>);");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = 10 + 10;\nsay 3 * ($bar);");
    }

    public void testPhaserExtractionFailing() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>BEGIN { say 10; }</selection>");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        UsefulTestCase.assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () -> {
            handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        });
    }

    public void testImportsExtractionFailing() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use <selection>Foo::Bar</selection>;");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$bar");
        UsefulTestCase.assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () -> {
            handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        });
    }

    public void testNonpostfixCallExtraction() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $a = %foo{<selection>42.foo</selection>};");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $foo = 42.foo;\nmy $a = %foo{$foo};");
    }

    public void testNoExtractionForTypeInDeclaration() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my In<caret>t $a;");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$foo");
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, "Cannot refactor with this selection", () ->
            handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null)
        );
    }

    public void testNoExtractionForTypeInParameter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "when :(In<caret>t $a) {};");
        Perl6VariableExtractionHandlerMock handler = new Perl6VariableExtractionHandlerMock(null, "$foo");
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, "Cannot refactor with this selection", () ->
            handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null)
        );
    }
}