package edument.rakuidea.actions;

import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;

public class EnterHandlerTest extends CommaFixtureTestCase {
    public void testPodContinuation() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "#| Foo!<caret>");
        myFixture.performEditorAction("EditorEnter");
        assertEquals("#| Foo!\n#| ", myFixture.getDocument(myFixture.getFile()).getText());
        assertEquals(11, myFixture.getCaretOffset());
    }

    public void testPodContinuationInMiddle() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, "#| Foo<caret> bar!");
        myFixture.performEditorAction("EditorEnter");
        assertEquals("#| Foo\n#| bar!", myFixture.getDocument(myFixture.getFile()).getText());
        assertEquals(10, myFixture.getCaretOffset());
    }
}
