// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.actions;

import com.intellij.ide.actions.GotoRelatedSymbolAction;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.testFramework.LightProjectDescriptor;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RuleAndActionNavigationTest extends CommaFixtureTestCase {
    public void doTest(String text, int offset) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, text);
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
