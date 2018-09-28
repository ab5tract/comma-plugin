package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class IntentionTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "testData/intention";
    }

    public void testZeroToN() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "for 0.<caret>.9 {}");
        IntentionAction intention = myFixture.findSingleIntention("Use simpler range syntax");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("Range1.p6");
    }

    public void testZeroToExclusiveN() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "for 0.<caret>.^10 {}");
        IntentionAction intention = myFixture.findSingleIntention("Use simpler range syntax");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("Range1.p6");
    }

    public void testZeroToVar() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "for 0.<caret>.^$n {}");
        IntentionAction intention = myFixture.findSingleIntention("Use simpler range syntax");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("Range2.p6");
    }

    public void testZeroToExclusiveVar() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "for 0.<caret>.$n-1 {}");
        IntentionAction intention = myFixture.findSingleIntention("Use simpler range syntax");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("Range2.p6");
    }

    public void testUndeclaredVariableAnnotatorReallyUndeclared() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "for 0.<caret>.($n-1) {}");
        IntentionAction intention = myFixture.findSingleIntention("Use simpler range syntax");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("Range2.p6");
    }

    public void testEVALCase1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = 5; EVAL $f<caret>oo;");
        IntentionAction intention = myFixture.findSingleIntention("Add MONKEY");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("Eval1.p6");
    }

    public void testEVALCase2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = 5; EVAL qq[$<caret>foo];");
        IntentionAction intention = myFixture.findSingleIntention("Add MONKEY");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("Eval2.p6");
    }

    public void testRoleMethodsStubbing() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role R { method foo($a) {...}; method bar($a) {...} };\nclas<caret>s C does R {\n}");
        IntentionAction intention = myFixture.findSingleIntention("Stub");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("MethodStubbing.p6");
    }
}
