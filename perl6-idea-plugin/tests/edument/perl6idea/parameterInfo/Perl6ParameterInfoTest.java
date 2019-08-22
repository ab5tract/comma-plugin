package edument.perl6idea.parameterInfo;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.testFramework.utils.parameterInfo.MockCreateParameterInfoContext;
import com.intellij.testFramework.utils.parameterInfo.MockParameterInfoUIContext;
import edument.perl6idea.Perl6ParameterInfoHandler;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.psi.P6CodeBlockCall;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6SubCall;

import java.util.function.Consumer;

public class Perl6ParameterInfoTest extends LightCodeInsightFixtureTestCase {
    public static final Perl6ParameterInfoHandler HANDLER = new Perl6ParameterInfoHandler();

    private void doTest(String text, String args, Consumer<MockParameterInfoUIContext>... checks) {
        StringBuilder builder = new StringBuilder();
        for (String signature : text.split(" \\|\\|\\| "))
            builder.append(String.format("multi a(%s); ", signature));
        builder.append("a(").append(args).append("<caret>");
        doTest(builder.toString(), checks);
    }

    private void doTest(String text, Consumer<MockParameterInfoUIContext>... checks) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, text);
        MockCreateParameterInfoContext createContext = new MockCreateParameterInfoContext(getEditor(), getFile());
        P6CodeBlockCall owner = HANDLER.findElementForParameterInfo(createContext);
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

    private static void assertParameterInfo(MockParameterInfoUIContext context, boolean isEnabled, String text, int start, int end) {
        assertEquals(text, context.getText());
        assertEquals(isEnabled, context.isUIComponentEnabled());
        assertEquals(start, context.getHighlightStart());
        assertEquals(end, context.getHighlightEnd());
    }

    public void testPosVsNamedSingle() {
        doTest("$a ||| :$foo", "",
               context -> assertParameterInfo(context,true, "$a", 0, 2),
               context -> assertParameterInfo(context, true, ":$foo", 0, 5));
    }

    public void testSingleArg() {
        doTest("$a ||| :$b", "42",
               context -> assertParameterInfo(context, true, "$a", 0, 0),
               context -> assertParameterInfo(context, false, ":$b", 0, 0));
    }

    public void testNamedIsAnticipated() {
        doTest("$a ||| $a, :$b", "42",
               context -> assertParameterInfo(context, true, "$a", 0, 0),
               context -> assertParameterInfo(context, true, "$a, :$b", 4, 7));
    }

    public void testOptionalIsAnticipated() {
        doTest("$a ||| $a, $b?", "42",
               context -> assertParameterInfo(context, true, "$a", 0, 0),
               context -> assertParameterInfo(context, true, "$a, $b?", 4, 7));
    }

    public void testSlurpyIsAnticipated() {
        doTest("$a, *@b ||| $a, $b", "42, 43, 44",
               context -> assertParameterInfo(context, true, "$a, *@b", 4, 7),
               context -> assertParameterInfo(context, false, "$a, $b", 0, 0));
    }

    public void testMethodParameterInfo() {
        doTest("class A { multi method a($a) {}; multi method a($a, :$foo) {}; multi method a(:$best) { self.a(:!best<caret> } }; ",
               context -> assertParameterInfo(context, false, "$a",0, 0),
               context -> assertParameterInfo(context, false, "$a, :$foo",0, 0),
               context -> assertParameterInfo(context, true, ":$best",0, 0));
    }
}
