package edument.perl6idea.parameterInfo;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.testFramework.utils.parameterInfo.MockCreateParameterInfoContext;
import com.intellij.testFramework.utils.parameterInfo.MockParameterInfoUIContext;
import edument.perl6idea.Perl6ParameterInfoHandler;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6SubCall;

import java.util.function.Consumer;

public class Perl6ParameterInfoTest extends LightCodeInsightFixtureTestCase {
    public static final Perl6ParameterInfoHandler HANDLER = new Perl6ParameterInfoHandler();

    private void doTest(String text, Consumer<MockParameterInfoUIContext>... checks) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, text);
        MockCreateParameterInfoContext createContext = new MockCreateParameterInfoContext(getEditor(), getFile());
        Perl6SubCall owner = HANDLER.findElementForParameterInfo(createContext);
        HANDLER.showParameterInfo(owner, createContext);
        Object[] items = createContext.getItemsToShow();
        assertNotNull(items);
        assertTrue(items.length != 0);
        MockParameterInfoUIContext uiContext = new MockParameterInfoUIContext<>(owner);
        assertEquals(items.length, checks.length);
        for (int i = 0; i < items.length; i++) {
            Object item = items[i];
            HANDLER.updateUI((Perl6RoutineDecl)item, uiContext);
            checks[i].accept(uiContext);
        }
    }

    private static void assertParameterInfo(MockParameterInfoUIContext context, String text, int start, int end) {
        assertEquals(text, context.getText());
        assertEquals(start, context.getHighlightStart());
        assertEquals(end, context.getHighlightEnd());
    }

    public void testPosVsNamedSingle() {
        doTest("multi a($a) {}; multi a(:$foo) {}; a<caret>(",
               context -> assertParameterInfo(context, "$a", 0, 2),
               context -> assertParameterInfo(context, ":$foo", 0, 5)
        );
    }
}
