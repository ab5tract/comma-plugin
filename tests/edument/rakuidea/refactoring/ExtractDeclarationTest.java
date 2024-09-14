package edument.rakuidea.refactoring;

import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.testFramework.UsefulTestCase;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.RakuConstantExtractionHandlerMock;
import edument.rakuidea.RakuVariableExtractionHandlerMock;
import edument.rakuidea.filetypes.RakuScriptFileType;
import edument.rakuidea.refactoring.introduce.variable.RakuIntroduceVariableHandler;

public class ExtractDeclarationTest extends CommaFixtureTestCase {
    public void testExpressionVariableExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "say pi; say 1<selection>0 + 5</selection>0; say 10 + 50;");
        RakuIntroduceVariableHandler handler = new RakuVariableExtractionHandlerMock(null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("say pi;\nmy $foo = 10 + 50;\nsay $foo;\nsay $foo;");
    }

    public void testExpressionVariableExtractionFromCursor() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "foo(\"st<selection>ring-v</selection>alue\");");
        RakuIntroduceVariableHandler handler = new RakuVariableExtractionHandlerMock(null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $foo = \"string-value\";\nfoo($foo);");
    }

    public void testExpressionConstantExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "say pi; say 1<selection>0 + 5</selection>0; say 10 + 50;");
        RakuConstantExtractionHandlerMock handler = new RakuConstantExtractionHandlerMock(null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("say pi;\nmy constant $foo = 10 + 50;\nsay $foo;\nsay $foo;");
    }

    public void testStatementVariableExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>(^10).roll</selection>;");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $foo = (^10).roll;");
    }

    public void testStatementVariableExtractionFull() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>(^10).roll;</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = (^10).roll;");
    }

    public void testStatementConstantExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>(^10).roll;</selection>");
        RakuConstantExtractionHandlerMock handler = new RakuConstantExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my constant $bar = (^10).roll;");
    }

    public void testWhitespaceIsHandled() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>if True { say 10 } else { say 'no' }   </selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do if True { say 10 } else { say 'no' };");
    }

    public void testIfStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>if True { say 10 } else { say 'no' }</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do if True { say 10 } else { say 'no' };");
    }

    public void testUnlessStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>unless False { say 10 }</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do unless False { say 10 };");
    }


    public void testWithStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>with $foo { say 10 };</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do with $foo { say 10 };");
    }

    public void testWithoutStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>without $foo { say 10 };</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do without $foo { say 10 };");
    }

    public void testWhenStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>when $foo eq 50 { 10 };</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do when $foo eq 50 { 10 };");
    }

    public void testForStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>for 1..3 { 10 }</selection>;");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do for 1 .. 3 { 10 };");
    }

    public void testGivenStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>given $foo { when 1 { say 10 } }</selection>;");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do given $foo { when 1 { say 10 } };");
    }

    public void testLoopStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>loop (my $i = 0; $i < 10; $i++) { say $i; }</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do loop (my $i = 0; $i < 10; $i++) { say $i; };");
    }

    public void testWhileStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>while $foo != 0 { say 10 };</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do while $foo != 0 { say 10 };");
    }

    public void testUntilStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>until $foo eq 'Foo' { say 10 };</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do until $foo eq 'Foo' { say 10 };");
    }

    public void testRepeatStatementExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>repeat { say 10 } until True</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = do repeat { say 10 } until True;");
    }

    public void testCorrectAnchorSelection() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "say 3 * (<selection>10 + 10</selection>);");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $bar = 10 + 10;\nsay 3 * ($bar);");
    }

    public void testPhaserExtractionFailing() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "<selection>BEGIN { say 10; }</selection>");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        UsefulTestCase.assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () -> {
            handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        });
    }

    public void testImportsExtractionFailing() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "use <selection>Foo::Bar</selection>;");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$bar");
        UsefulTestCase.assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, () -> {
            handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        });
    }

    public void testNonpostfixCallExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "my $a = %foo{<selection>42.foo</selection>};");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$foo");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $foo = 42.foo;\nmy $a = %foo{$foo};");
    }

    public void testNoExtractionForTypeInDeclaration() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "my In<caret>t $a;");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$foo");
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, "Cannot refactor with this selection", () ->
            handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null)
        );
    }

    public void testNoExtractionForTypeInParameter() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "when :(In<caret>t $a) {};");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$foo");
        assertThrows(CommonRefactoringUtil.RefactoringErrorHintException.class, "Cannot refactor with this selection", () ->
            handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null)
        );
    }

    public void testLiteralExtraction() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "say <selection>42</selection>; say 42;");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$foo");
        handler.replaceAll = false;
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $foo = 42;\nsay $foo;\nsay 42;");
    }

    public void testExtractMethodCall() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "1.is-pr<caret>ime;");
        RakuVariableExtractionHandlerMock handler = new RakuVariableExtractionHandlerMock(null, "$result");
        handler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
        myFixture.checkResult("my $result = 1.is-prime;");
    }
}