package edument.rakuidea.actions;

import com.intellij.ide.actions.GotoRelatedSymbolAction;
import com.intellij.navigation.GotoRelatedItem;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;

import java.util.List;

public class RuleAndActionNavigationTest extends CommaFixtureTestCase {
    public void doTest(String text, int offset) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, text);
        List<GotoRelatedItem> items = GotoRelatedSymbolAction.getItems(myFixture.getFile(), myFixture.getEditor(), null);
        assertEquals(1, items.size());
        assertEquals(offset, items.get(0).getElement().getTextOffset());
    }

    public void testGoingToActionFromRule() {
        doTest("grammar G { rule TOP { <caret> } }; class G { method TOP($/) {} }", 46);
    }

    public void testGoingToRuleFromAction() {
        doTest("grammar G { rule TOP { x } }; class G { method TOP($/) {<caret>} }", 17);
    }

    public void testGoingToActionFromRuleWithLongname() {
        doTest("grammar G { token foo:sym<bar> { <caret> } }; class G { method foo:sym<bar>($/) {} }", 56);
    }

    public void testGoingToRuleFromActionWithLongname() {
        doTest("grammar G { token foo:sym<bar> { x } }; class G { method foo:sym<bar>($/) {<caret>} }", 18);
    }
}
