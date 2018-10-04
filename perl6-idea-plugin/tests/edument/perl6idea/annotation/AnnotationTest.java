package edument.perl6idea.annotation;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.sdk.Perl6SdkType;

public class AnnotationTest extends LightCodeInsightFixtureTestCase {
    private Sdk testSdk;

    @Override
    protected String getTestDataPath() {
        return "testData/annotation";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ApplicationManager.getApplication().runWriteAction(() -> {
            String homePath = Perl6SdkType.getInstance().suggestHomePath();
            assertNotNull("Found a perl6 in path to use in tests", homePath);
            testSdk = SdkConfigurationUtil.createAndAddSDK(homePath, Perl6SdkType.getInstance());
            ProjectRootManager.getInstance(myModule.getProject()).setProjectSdk(testSdk);
        });
    }

    @Override
    protected void tearDown() throws Exception {
        SdkConfigurationUtil.removeSdk(testSdk);
        super.tearDown();
    }

    public void testUndeclaredVariableAnnotatorReallyUndeclared() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <error descr=\"Variable $foo is not declared\">$foo</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testUndeclaredVariableAnnotatorNoErrorIfDeclared() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo; say $foo;");
        myFixture.checkHighlighting(false, false, true, false);
    }

    public void testUndeclaredVariableAnnotatorDefaultsInOuterScopeOK() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say $_, $/, $!");
        myFixture.checkHighlighting(false, false, true, false);
    }

    public void testUndeclaredVariableAnnotatorPostdeclaredSubOK() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say &a.arity; sub a { }");
        myFixture.checkHighlighting(false, false, true, false);
    }

    public void testUndeclaredVariableAnnotatorUndeclaredSubCaught() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <error descr=\"Variable &a is not declared\">&a</error>.arity; sub ab { }");
        myFixture.checkHighlighting(false, false, true, false);
    }

    public void testUndeclaredVariableAnnotatorPostdeclared() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <error descr=\"Variable $foo is not declared in this scope yet\">$foo</error>; my $foo = 42");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testUndeclaredVariableAnnotatorNoErrorIfConstantDeclared() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my constant $foo = 42; say $foo;");
        myFixture.checkHighlighting(false, false, true, false);
    }

    public void testUndeclaredVariableAnnotatorFinishPresent() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,"say $=finish;\n\n=begin finish\n\nfoo");
        myFixture.checkHighlighting(false, false, true, false);
    }

    public void testUndeclaredVariableAnnotatorFinishIsNotPresent() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,"say <error descr=\"There is no =finish section in this file\">$=finish</error>;");
        myFixture.checkHighlighting(false, false, true, false);
    }

    public void testUndeclaredVariableAnnotatorFinishPresentInBlock() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,"if 1 {\nsay $=finish;\n}\n=begin finish\n\nfoo");
        myFixture.checkHighlighting(false, false, true, false);
    }

    public void testDeclaredSubAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo() {};\nmy sub bar() {};\nfoo;\nbar()");
        myFixture.checkHighlighting(false, false, true, false);
    }

    public void testUndeclaredSubAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Subroutine foo is not declared\">foo</error>;");
        myFixture.checkHighlighting(false, false, true, false);
    }

    public void testLeadingZeroAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <warning descr=\"Leading 0 does not indicate octal in Perl 6; use 0o755\">0755</warning>;");
        myFixture.checkHighlighting(true, false, false);
    }

    public void testMethodNotOnRangeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <warning descr=\"Precedence of ^ is looser than method call; please parenthesize\">^1.map(*.is-prime)</warning>;");
        myFixture.checkHighlighting(true, false, false);
    }

    public void testUnitKeywordAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo<error descr=\"Semicolon form of 'class' without 'unit' is illegal.\">;</error>");
        myFixture.checkHighlighting(false, false, true, true);
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot use 'unit' with block form of declaration\">unit</error> class Foo{}");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testEmptyNameVariableAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say $;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testUndeclaredPrivateMethodAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role A { method !a($one) {} }; class B does A { method b { self<error descr=\"Private method !c is used, but not declared\">!c</error>(1); } }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testDeclaredPrivateMethodAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role A { method !a {} }; class B does A { method b { self!a; } }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testDeclaredExternalPrivateMethodAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use NativeCall; role A does NativeCall::Native { method !a {} }; class B does A { method b { self!setup; } }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testUndeclaredAttributeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role A { has $!a; }; class B does A { method b { say <error descr=\"Attribute $!b is used, but not declared\">$!b</error>; } }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testDeclaredAttributeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role A { has $!a; }; class B does A { has $!b; method b { say $!a; say $!b; } }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testDeclaredExternalAttributeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use NativeCall; class A does NativeCall::Native { method b { say $!setup; } }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testSignature1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo($a?, <error descr=\"Cannot put positional parameter $b after an optional parameter\">$b</error>) { }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testSignature2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo($a, *@b, <error descr=\"Cannot put positional parameter $c after a variadic parameter\">$c</error>) { }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testSignature3() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo($a, *@b, <error descr=\"Cannot put optional parameter $c after a variadic parameter\">$c?</error>) { }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testSignature4() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo(:$a, <error descr=\"Cannot put positional parameter $b after a named parameter\">$b</error>) { }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testSignature5() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo($a = 42, <error descr=\"Cannot put positional parameter $b after an optional parameter\">$b</error>) { }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testSignature6() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo($a, *@as, :$c!) {}");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testSignature7() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo($a, *@as, :$c) {}");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testSignature8() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo(*%h, :$c) {}");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testSignature9() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub web(Str $cfg-filename, Str $model-filename, Str $tech-file?) is export {}");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testSignature10() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "multi sub MAIN('web', ExistingFile $cfg-filename, ExistingFile $model-filename, Str $tech-file?) is export {}");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testRawWheneverAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error=descr=\"A whenever must be within a supply or react block\"whenever</error> $foo {}");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testInfiniteRangeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "1..*");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testIncompleteRangeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say 1<error=\"The range operator must have a second argument\">..</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testNullRegexAnnotator1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error=\"Empty regex is not allowed\">//</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testNullRegexAnnotator2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "token foo<error=\"Empty regex is not allowed\">{}</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testNullRegexAnnotator3() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "regex foo <error=\"Empty regex is not allowed\">{}</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testNullRegexAnnotator4() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "rule foo <error=\"Empty regex is not allowed\">{}</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testWheneverInReactAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "react { whenever $foo {} }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testWheneverInSupplyAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "supply { whenever $foo {} }");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testRegexPositionalDeclAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a regex positional match variable\">my $0 = 42</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testTypedRegexPositionalDeclAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a regex positional match variable\">my Int $0 = 42</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testRegexNamedDeclScalarSigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a regex named match variable\">my $<foo> = 42</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testRegexNamedDeclArraySigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a regex named match variable\">my @<foo> = 42</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testRegexNamedDeclHashSigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a regex named match variable\">my %<foo> = 42</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testContextualizerDeclScalarSigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a contextualizer\">my $('x') = 42</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testContextualizerDeclArraySigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a contextualizer\">my @('x') = 42</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testContextualizerDeclHashSigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a contextualizer\">my %('x') = 42</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testRestrictUnitKeywordToMAINSubAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error=\"The unit sub syntax is only allowed for the sub MAIN\">unit</error> sub foo() {}");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testPermitUnitKeywordForMAINSubAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "unit sub MAIN() {}");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testInfixAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $lc-and-trim := { $_ = .lc.trim };");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testEVALCase1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "EVAL \"5\";");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testEVALCase2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "EVAL q[5];");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testEVALCase3() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = 5; EVAL q[$foo];");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testEVALCase4() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = 5; EVAL <error descr=\"Cannot EVAL interpolated expression without MONKEY-SEE-NO-EVAL pragma\">qq[$foo]</error>;");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testEVALCase5() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "EVAL qq[];");
        myFixture.checkHighlighting(false, false, true, true);
    }

    public void testTrustedMethodIsCountedAsDeclarted() {
        myFixture.configureByFile("TrustedClass.pm6");
        myFixture.checkHighlighting();
    }
}
