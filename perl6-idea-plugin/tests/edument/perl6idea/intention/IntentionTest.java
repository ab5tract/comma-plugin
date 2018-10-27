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
                                  "role R { method foo($a) {...}; method bar($a) {...} };\nclass C d<caret>oes R {\n}");
        IntentionAction intention = myFixture.findSingleIntention("Stub");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("MethodStubbing.p6");
    }

    public void testRecursiveRoleMethodsStubbing() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role R0 { method base {...} }; role R does R0 { method foo($a) {...}; method bar($a) {...} };\nclass C<caret> does R {\n}");
        IntentionAction intention = myFixture.findSingleIntention("Stub");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("MethodStubbingRecursive.p6");
    }

    public void testMyVariableExport() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo is expo<caret>rt;");
        IntentionAction intention = myFixture.findSingleIntention("Change scope");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("our $foo is export;");
    }

    public void testRoleDoesClass() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class C {}; role A do<caret>es C {}");
        IntentionAction intention = myFixture.findSingleIntention("Replace \"does\"");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("class C {}; role A is<caret> C {}");
    }

    public void testPrivateMethodStubbing() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Bar { method a { self!k<caret>k; } }");
        IntentionAction intention = myFixture.findSingleIntention("Create");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResultByFile("PrivateMethodStubbing.p6", true);
    }

    public void testPrivateMethodStubbingWithoutEnclosingRoutine() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Bar { has $.foo = self!k<caret>k; } }");
        IntentionAction intention = myFixture.findSingleIntention("Create");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("class Bar { has $.foo = self!kk;\nmethod !kk() {}} }", true);
    }

    public void testPrivateMethodStubbingInNestedRoutines() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Bar { method a { sub foo { self!mm<caret>m; } } } }");
        IntentionAction intention = myFixture.findSingleIntention("Create");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("class Bar { method a { sub foo { self!mmm; } }\nmethod !mmm() {}} }", true);
    }

    public void testPrivateMethodStubbingSignatureGeneration() {
                myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Bar { method a { sub foo { my $var; my $bar; self!mm<caret>m(1, 1, [1], (2), named => 1, double => 2, double => 2, foo, foo(1), Bar.value(), $var, $bar); } } } }");
        IntentionAction intention = myFixture.findSingleIntention("Create");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("class Bar { method a { sub foo { my $var; my $bar; self!mmm(1, 1, [1], (2), named => 1, double => 2, double => 2, foo, foo(1), Bar.value(), $var, $bar); } }\nmethod !mmm($p1, $p2, @p1, @p2, $named, $double1, $double2, $foo1, $foo2, $value, $var, $bar) {}} }", true);
    }
}
