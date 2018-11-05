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

    public void testUnitPrependingForNamedClass() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo {}; class C<caret>;");
        IntentionAction intention = myFixture.findSingleIntention("Add missing");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("sub foo {}; unit class C;");
    }

    public void testUnitRemovalForDefinedGrammar() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo {}; unit gra<caret>mmar A {};");
        IntentionAction intention = myFixture.findSingleIntention("Remove 'unit'");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("sub foo {}; grammar A {};");
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
        myFixture.checkResult("class Bar { method a { sub foo { my $var; my $bar; self!mmm(1, 1, [1], (2), named => 1, double => 2, double => 2, foo, foo(1), Bar.value(), $var, $bar); } }\nmethod !mmm($p1, $p2, @p1, @p2, $foo1, $foo2, $value, $var, $bar, :$named, :$double1, :$double2) {}} }", true);
    }

    public void testPrivateMethodStubbingSignatureGenerationForSingleArg() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "class Bar { method a { sub foo { self!mm<caret>m(1); } } } }");
        IntentionAction intention = myFixture.findSingleIntention("Create");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("class Bar { method a { sub foo { self!mmm(1); } }\nmethod !mmm($p) {}} }", true);
    }

    public void testPrivateMethodStubbingSignatureGenerationColonpair() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "class Bar { method a { sub foo { self!mm<caret>m: 1, 2; } } } }");
        IntentionAction intention = myFixture.findSingleIntention("Create");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("class Bar { method a { sub foo { self!mmm: 1, 2; } }\nmethod !mmm($p1, $p2) {}} }", true);
    }

    public void testPrivateMethodStubbingSignatureGenerationForSingleArgColonpair() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "class Bar { method a { sub foo { self!mm<caret>m: 1; } } } }");
        IntentionAction intention = myFixture.findSingleIntention("Create");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("class Bar { method a { sub foo { self!mmm: 1; } }\nmethod !mmm($p) {}} }", true);
    }

    public void testPrivateMethodStubbingSignatureGenerationForNamedParameters() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "class Bar { method a { my $a; my $b;  self!mm<caret>m(:$a, :b($b), :42c, :d, :!e, :$<f>); } }");
        IntentionAction intention = myFixture.findSingleIntention("Create");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("class Bar { method a { my $a; my $b;  self!mmm(:$a, :b($b), :42c, :d, :!e, :$<f>); }\nmethod !mmm(:$a, :$b, :$c, :$d, :$e, :$f) {}}", true);
    }

    public void testOrderOfNamedVariablesInCallIsFixed() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "class Bar { method a { self!mm<caret>m(:42c, 1, 2); } }");
        IntentionAction intention = myFixture.findSingleIntention("Create");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("class Bar { method a { self!mmm(:42c, 1, 2); }\nmethod !mmm($p1, $p2, :$c) {}}", true);
    }

    public void testConflictResolutionAfterMoveSolved() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "class Bar { method a { self!f<caret>oo(1, 2, c => 3, d => 4, 5); } }");
        IntentionAction intention = myFixture.findSingleIntention("Create");
        assertNotNull(intention);
        myFixture.launchAction(intention);
        myFixture.checkResult("class Bar { method a { self!foo(1, 2, c => 3, d => 4, 5); }\nmethod !foo($p1, $p2, $p3, :$c, :$d) {}}", true);
    }
}
