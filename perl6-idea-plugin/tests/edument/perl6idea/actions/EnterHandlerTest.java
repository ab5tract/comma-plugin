package edument.perl6idea.actions;

import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class EnterHandlerTest extends CommaFixtureTestCase {
    public void testPodContinuation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "#| Foo!<caret>");
        myFixture.performEditorAction("EditorEnter");
        assertEquals("#| Foo!\n#| ", myFixture.getDocument(myFixture.getFile()).getText());
        assertEquals(11, myFixture.getCaretOffset());
    }

    public void testPodContinuationInMiddle() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "#| Foo<caret> bar!");
        myFixture.performEditorAction("EditorEnter");
        assertEquals("#| Foo\n#| bar!", myFixture.getDocument(myFixture.getFile()).getText());
        assertEquals(10, myFixture.getCaretOffset());
    }
}
