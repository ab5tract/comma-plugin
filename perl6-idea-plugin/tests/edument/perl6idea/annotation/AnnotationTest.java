package edument.perl6idea.annotation;

import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class AnnotationTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/annotation";
    }

    public void testUndeclaredVariableAnnotatorReallyUndeclared() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <error descr=\"Variable $foo is not declared\">$foo</error>;");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredVariableAnnotatorNoErrorIfDeclared() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo; say $foo;");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredVariableAnnotatorDefaultsInOuterScopeOK() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say $_, $/, $!");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredVariableAnnotatorPostdeclaredSubOK() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say &a.arity; sub a { }");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredVariableAnnotatorUndeclaredSubCaught() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <error descr=\"Variable &a is not declared\">&a</error>.arity; sub ab { }; ab();");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredVariableAnnotatorPostdeclared() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <error descr=\"Variable $foo is not declared in this scope yet\">$foo</error>; my $foo = 42; say $foo");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredVariableAnnotatorNoErrorIfConstantDeclared() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my constant $foo = 42; say $foo;");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredVariableAnnotatorFinishPresent() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,"say $=finish;\n\n=begin finish\n\nfoo");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredVariableAnnotatorFinishIsNotPresent() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,"say <error descr=\"There is no =finish section in this file\">$=finish</error>;");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredVariableAnnotatorFinishPresentInBlock() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,"if 1 {\nsay $=finish;\n}\n=begin finish\n\nfoo");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredVariableAnnotatorRoleParameter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,"role A[$foo, @foo, :$bar] { method m() { $foo, @foo, $bar } }");
        myFixture.checkHighlighting();
    }

    public void testCursorAvailableInToken() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "grammar G { token TOP { x { say $¢ } } }");
        myFixture.checkHighlighting();
    }

    public void testFalsePositive1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,"my $x = 1, 2; my ($a) = $x; say $a");
        myFixture.checkHighlighting();
    }

    public void testAnonymousVariables() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,"my $; my @; my %; my &; say $; say @; say %; say &;");
        myFixture.checkHighlighting();
    }

    public void testDeclaredSubAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo() {};\nmy sub bar() {};\nfoo;\nbar()");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredSubAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Subroutine foo is not declared\">foo</error>;");
        myFixture.checkHighlighting();
    }

    public void testDeclaredSubAnnotatorWhenItIsReallyACoercion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $x = DateTime('2020-01-01');");
        myFixture.checkHighlighting(false, false, false, false);
    }

    public void testDeclaredAliasedCoreSubAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @a = [1,2], [3,4]; cross(@a)");
        myFixture.checkHighlighting();
    }

    public void testNoBogusSubAnnotationOnInterpolatedName() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say ::(\"Rakudo::Internals\").?LL-EXCEPTION;");
        myFixture.checkHighlighting();
    }

    public void testInfixBracketedInVariableIsNotConsideredUndeclared() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my &add = &[+]; say add(1,2)");
        myFixture.checkHighlighting();
    }

    public void testDeclaredOperatorNamesInVariables() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @ops = &infix:<+>, &prefix:<!>, &postfix:<++>, &postcircumfix:<[ ]>; say @ops");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredOperatorNamesInVariables() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <error descr=\"Variable &infix:<foo> is not declared\">&infix:<foo></error>;");
        myFixture.checkHighlighting();
    }

    public void testLeadingZeroAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <warning descr=\"Leading 0 does not indicate octal in Raku; use 0o755\">0755</warning>;");
        myFixture.checkHighlighting();
    }

    public void testMethodNotOnRangeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <warning descr=\"Precedence of ^ is looser than method call; please parenthesize\">^1.map(*.is-prime)</warning>;");
        myFixture.checkHighlighting();
    }

    public void testUnitKeywordAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Semicolon form of 'class' without 'unit' is illegal.\">class Foo;</error>");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot use 'unit' with block form of declaration\">unit class Foo</error>{}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "unit class Foo;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my");
        myFixture.checkHighlighting();
    }

    public void testEmptyNameVariableAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say $;");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredPrivateMethodAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role A { method !a($one) {$one} }; class B does A { method b { self<error descr=\"Private method !c is used, but not declared\">!c</error>(1); } }");
        myFixture.checkHighlighting();
    }

    public void testDeclaredPrivateMethodAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role A { method !a {} }; class B does A { method b { self!a; } }");
        myFixture.checkHighlighting();
    }

    public void testDeclaredExternalPrivateMethodAnnotator() throws InterruptedException {
        ensureModuleIsLoaded("NativeCall");
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use NativeCall; role A does NativeCall::Native { method !a {} }; class B does A { method b { self!setup; } }");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredAttributeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role A { has $!a; }; class B does A { method b { say <error descr=\"Attribute $!b is used, but not declared\">$!b</error>; } }");
        myFixture.checkHighlighting();
    }

    public void testDeclaredAttributeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role A { has $!a; }; class B does A { has $!b; method b { say $!a; say $!b; } }");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredMultiAttributeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class A { has ($!a, $!asdrf); method m() { $!a, $!asdrf } }");
        myFixture.checkHighlighting();
    }

    public void testDeclaredExternalAttributeAnnotator() throws InterruptedException {
        ensureModuleIsLoaded("NativeCall");
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use NativeCall; class A does NativeCall::Native { method b { say $!rettype; } }");
        myFixture.checkHighlighting();
    }

    public void testSignatureAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "multi sub encode-base64(Bool:D :$pad!, |c) { samewith(:pad(?$pad ?? '=' !! ''), |c) }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "multi sub MAIN('nuke', Bool :<weak_warning descr=\"Unused parameter\">$confirm</weak_warning>, *<weak_warning descr=\"Unused parameter\">@names</weak_warning> ($, *@)) {}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "sub a(Str :$foo, <warning descr=\"Explicit `?` on a named parameter $bar is redundant, as all nameds are optional by default\">Str :$bar?</warning>) { say $foo; say $bar; }; a;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "class A { has $!a; submethod BUILD(:$!a = 42, :$b!) { say $b; say $!a; }; }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "my (:%file, :%methods (:%over-documented, :%under-documented, :%introspection, *%)); %file; %methods; %over-documented; %under-documented; %introspection;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo($a?, <error descr=\"Cannot put positional parameter $b after an optional parameter\">$b</error>) { $a, $b }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo($a, *@b, <error descr=\"Cannot put positional parameter $c after a variadic parameter\">$c</error>) { $a, @b, $c }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo($a, *@b, <error descr=\"Cannot put optional parameter $c after a variadic parameter\">$c?</error>) { $a, @b, $c }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo(:$a, <error descr=\"Cannot put positional parameter $b after a named parameter\">$b</error>) { $a, $b }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo($a = 42, <error descr=\"Cannot put positional parameter $b after an optional parameter\">$b</error>) { $a, $b }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo($a, *@as, :$c!) { $a, @as, $c }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo($a, *@as, :$c) { $a, @as, $c }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo(*%h, :$c) { %h, $c }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub web(Str $cfg-filename, Str $model-filename, Str $tech-file?) is export { $cfg-filename, $model-filename, $tech-file }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "multi sub MAIN('web', ExistingFile $cfg-filename, ExistingFile $model-filename, Str $tech-file?) is export { $cfg-filename, $model-filename, $tech-file }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub MAIN(Admin, 'web') {}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo($a = 42, $bar? is copy) { $a, $bar }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo(<warning descr=\"Explicit `?` on a named parameter $bar is redundant, as all nameds are optional by default\">:$bar?</warning>) { $bar }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo(<warning descr=\"Explicit `!` on a positional parameter $foo is redundant, as all positional parameters are required by default\">$foo!</warning>, $bar) { $foo, $bar }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo(<warning descr=\"Explicit `?` on a parameter $foo with default is redundant, as all parameters with default value are optional by default\">$foo? = 42</warning>) { $foo }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo(<error descr=\"Parameter $foo has a default value and so cannot be required\">$foo! = 42</error>) { $foo }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub template-location(IO() $location, :$compile-all, :$test = { .IO.basename !~~ / ^ '.' / } --> Nil) is export { $location; $compile-all; $test; }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my <weak_warning descr=\"Unused variable\">$foo</weak_warning> = sub foo(:<weak_warning descr=\"Unused parameter\">$a</weak_warning>!) {}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my <weak_warning descr=\"Unused variable\">$bar</weak_warning> = -> :<weak_warning descr=\"Unused parameter\">$a</weak_warning>! {}");
        myFixture.checkHighlighting();
    }

    public void testOptionalParameterAfterDefaultWithReturnType() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub a($a = 5, $b? ) { $a, $b }");
        myFixture.checkHighlighting();
    }

    public void testRawWheneverAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error=descr=\"A whenever must be within a supply or react block\"whenever</error> $foo {}");
        myFixture.checkHighlighting();
    }

    public void testInfiniteRangeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "1..*");
        myFixture.checkHighlighting();
    }

    public void testIncompleteRangeAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say 1<error=\"The range operator must have a second argument\">..</error>;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say 1..42.Int;");
        myFixture.checkHighlighting();
    }

    public void testIncompleteRangeAnnotatorWithPrefixEnding() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my ($foo, $bar, $baz); $foo .. +($bar // $baz);");
        myFixture.checkHighlighting();
    }

    public void testLiteralRange() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "for 5..10 {}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $a; my $b; for $a..$b {}");
        myFixture.checkHighlighting();
    }

    public void testRangeWIthWhateverStarIsTooSmartForSimplification() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[0..*-31]");
        myFixture.checkHighlighting();
    }

    public void testRangeWithNewlineIsCompleted() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my <weak_warning descr=\"Unused variable\">$range</weak_warning> = <weak_warning descr=\"Range can be simplified\">0\n..\n1</weak_warning>");
        myFixture.checkHighlighting();
    }

    public void testZeroToNRange() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "for <weak_warning descr=\"Range can be simplified\">0..9</weak_warning> {}");
        myFixture.checkHighlighting();
    }

    public void testZeroToExclusiveNRange() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "for <weak_warning descr=\"Range can be simplified\">0..^10</weak_warning> {}");
        myFixture.checkHighlighting();
    }

    public void testZeroToVarRange() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $n = 5; for <weak_warning descr=\"Range can be simplified\">0..^$n</weak_warning> {}");
        myFixture.checkHighlighting();
    }

    public void testZeroToExclusiveVarRange() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $n = 5; for <weak_warning descr=\"Range can be simplified\">0..$n-1</weak_warning> {}");
        myFixture.checkHighlighting();
    }

    public void testZeroToExclusiveVarInParensRange() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $n = 5; for <weak_warning descr=\"Range can be simplified\">0..($n-1)</weak_warning> {}");
        myFixture.checkHighlighting();
    }

    public void testNullRegexAnnotator1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error=\"Empty regex is not allowed\">//</error>;");
        myFixture.checkHighlighting();
    }

    public void testNullRegexAnnotator2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "token foo<error=\"Empty regex is not allowed\">{}</error>;");
        myFixture.checkHighlighting();
    }

    public void testNullRegexAnnotator3() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "regex foo <error=\"Empty regex is not allowed\">{}</error>;");
        myFixture.checkHighlighting();
    }

    public void testNullRegexAnnotator4() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "rule foo <error=\"Empty regex is not allowed\">{}</error>;");
        myFixture.checkHighlighting();
    }

    public void testWheneverInReactAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo; react { whenever $foo {} }");
        myFixture.checkHighlighting();
    }

    public void testWheneverInSupplyAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo; supply { whenever $foo {} }");
        myFixture.checkHighlighting();
    }

    public void testRegexPositionalDeclAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a regex positional match variable\">my $0 = 42</error>;");
        myFixture.checkHighlighting();
    }

    public void testTypedRegexPositionalDeclAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a regex positional match variable\">my Int $0 = 42</error>;");
        myFixture.checkHighlighting();
    }

    public void testRegexNamedDeclScalarSigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a regex named match variable\">my $<foo> = 42</error>;");
        myFixture.checkHighlighting();
    }

    public void testRegexNamedDeclArraySigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a regex named match variable\">my @<foo> = 42</error>;");
        myFixture.checkHighlighting();
    }

    public void testRegexNamedDeclHashSigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a regex named match variable\">my %<foo> = 42</error>;");
        myFixture.checkHighlighting();
    }

    public void testContextualizerDeclScalarSigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a contextualizer\">my $('x') = 42</error>;");
        myFixture.checkHighlighting();
    }

    public void testContextualizerDeclArraySigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a contextualizer\">my @('x') = 42</error>;");
        myFixture.checkHighlighting();
    }

    public void testContextualizerDeclHashSigilAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Cannot declare a contextualizer\">my %('x') = 42</error>;");
        myFixture.checkHighlighting();
    }

    public void testUndeclarableAnnotatorUsesActualVariableDeclaration() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our token cidr { (\\d+) <?{ $0 <= 32 }> }");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredAnnotatorInMethodCall() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "token foo { (.) <.panic(\"Unknown escape \\\\$0\")> }");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredAnnotatorRegexVars() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "if <error descr=\"Variable $sub-key is not declared\">$sub-key</error> ~~ /^ <[\\w-]>+ $/ {<error descr=\"Variable $0 is not declared\">$0</error>}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "when m{ ^ ( '#' .+? ) \\s*? $ } { say $0; }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "unless m{ ^ ( '#' .+? ) \\s*? $ } {}; say $0;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "if True { m{ ^ ( '#' .+? ) \\s*? $ }; $0; }");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "/[ ('a') (\\d+) <?{ 1920 <= $1.tail <= 2020 }> ]/;");
        myFixture.checkHighlighting();
    }

    public void testUndeclaredAnnotatorRegexVarsCorrectComparisonIsUsed() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo($sub-key) { if $sub-key ~~ m:s/^ '{' (<[\\w-]>+)+ % ';' '}' $/ { $0 } elsif $sub-key ~~ /^ <[\\w-]>+ $/ {} }; foo('bar')");
        myFixture.checkHighlighting();
    }

    public void testRestrictUnitKeywordToMAINSubAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "<error=\"The unit sub syntax is only allowed for the sub MAIN\">unit</error> sub foo() {<warning descr=\"Useless use of value in sink (void) context\">}</warning>");
        myFixture.checkHighlighting();
    }

    public void testPermitUnitKeywordForMAINSubAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "unit sub MAIN() {}");
        myFixture.checkHighlighting();
    }

    public void testInfixAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $lc-and-trim := { $_ = .lc.trim }; say $lc-and-trim('x')");
        myFixture.checkHighlighting();
    }

    public void testEVALCase1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "EVAL \"5\";");
        myFixture.checkHighlighting();
    }

    public void testEVALCase2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "EVAL q[5];");
        myFixture.checkHighlighting();
    }

    public void testEVALCase3() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "EVAL q[$foo];");
        myFixture.checkHighlighting();
    }

    public void testEVALCase4() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = 5; <error descr=\"Cannot EVAL interpolated expression without MONKEY-SEE-NO-EVAL pragma\">EVAL qq[$foo]</error>;");
        myFixture.checkHighlighting();
    }

    public void testEVALCase5() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "EVAL qq[];");
        myFixture.checkHighlighting();
    }

    public void testMissingStubbedMethodFromSingleRole() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role R { method foo($a) {...}; method bar($a) {...} }; class <error descr=\"Composed roles require to implement methods: bar, foo\">C does R </error>{}");
        myFixture.checkHighlighting();
    }

    public void testMissingStubbedMethodsFromManyRoles() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role R { method foo($a) {...}; method bar($a) {...} }; role R2 { method baz {...} }; class <error descr=\"Composed roles require to implement methods: bar, foo, baz\">C does R does R2</error>{}");
        myFixture.checkHighlighting();
    }

    public void testStubbedMethodFromRoleImplementedAsAccessor() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role R { method baz {...}; method bar {...}; method foo {...} }; class <error descr=\"Composed roles require to implement methods: bar, baz\">C does R </error>{ my $.baz; has $.foo; has $!bar; method m() { $!bar } }");
        myFixture.checkHighlighting();
    }

    public void testMissingStubbedMethodsIncludeMulti() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role R { multi method foo($a) {...}; method bar($a) {...} }; class C does R { multi method foo($a) { $a }; multi method foo(@b) { @b }; method bar($a) {...} }");
        myFixture.checkHighlighting();
    }

    public void testMissingStubbedMethodDoNotIncludeFilledOnes1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role R1 { method m {...} }; role R2 does R1 { method m {...} }; class <error descr=\"Composed roles require to implement methods: m\">C does R2 </error>{}");
        myFixture.checkHighlighting();
    }

    public void testMissingStubbedMethodDoNotIncludeFilledOnes2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role R1 { method m {...} }; role R2 { method m {} }; class C does R1 does R2 {}");
        myFixture.checkHighlighting();
    }

    public void testMissingStubbedMethodDoNotIncludeFilledOnes3() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role R1 { method m {...} }; role R2 { method m {} }; class C does R2 does R1 {}");
        myFixture.checkHighlighting();
    }

    public void testMissingStubbedMethodsCountMultidecls() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role R { method m {...}; method b {...}; }; class C does R { has ($.m, $.b); }");
        myFixture.checkHighlighting();
    }

    public void testMissingStubbedMethodsHandlesTrait() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role R { method foo {...}; method bar {...}; method baz {...} }; class Impl { method foo {} }; class C does R { has Impl $.impl handles <foo bar>; has Int $.foo handles <baz> }");
        myFixture.checkHighlighting();
    }

    public void testMyScopedVariableExportAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"`my` scoped variable cannot be exported\">my $var is export</error>;");
        myFixture.checkHighlighting();
    }

    public void testRoleDoesClassAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class C {}; role A <error descr=\"Role cannot compose a class\">does C</error> {}");
        myFixture.checkHighlighting();
    }

    public void testClassDoesClassAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class C {}; class A <error descr=\"Class cannot compose a class\">does C</error> {}");
        myFixture.checkHighlighting();
    }

    public void testClassDoesClassAlsoAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class C {}; class D { also <error descr=\"Class cannot compose a class\">does C</error> }");
        myFixture.checkHighlighting();
    }

    public void testRoleDoesClassAlsoAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class C {}; role D { also <error descr=\"Role cannot compose a class\">does C</error> }");
        myFixture.checkHighlighting();
    }

    public void testNormalComposition1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role R {}; role A does C {}");
        myFixture.checkHighlighting();
    }

    public void testNormalComposition2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role R {}; class A does C {}");
        myFixture.checkHighlighting();
    }

    public void testNormalInheritance1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class C {}; class A is C {}");
        myFixture.checkHighlighting();
    }

    public void testNormalInheritance2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class C {}; role A is C {}");
        myFixture.checkHighlighting();
    }

    public void testTrustedMethodIsCountedAsDeclarted() {
        myFixture.configureByFile("TrustedClass.pm6");
        myFixture.checkHighlighting();
    }

    public void testOOMonitors() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "monitor <error descr=\"Cannot use monitor type package without OO::Monitors module being included\">LongName::Name</error> {}");
        myFixture.checkHighlighting();
    }

    public void testFromPerl5ModuleParens() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use Foo::Bar:from('Perl5')");
        myFixture.checkHighlighting();
    }

    public void testFromPerl5ModuleAngles() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use Foo::Bar:from<Perl5>");
        myFixture.checkHighlighting();
    }

    public void testSigspaceAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "grammar G { rule foo { abc<info desc=\"Implicit <.ws> call\"> </info>def<info desc=\"Implicit <.ws> call\"> </info>} }");
        myFixture.checkHighlighting(false, true, false, true);
    }

    public void testPackageDeclAnnotator1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "package Foo {}");
        myFixture.checkHighlighting();
    }

   public void testPackageDeclAnnotator2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "module Foo {}");
        myFixture.checkHighlighting();
    }

   public void testPackageDeclAnnotator3() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "module Foo <error descr=\"module cannot compose a role\">does A</error> {}");
        myFixture.checkHighlighting();
    }

   public void testPackageDeclAnnotator4() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "module Foo <error descr=\"module cannot inherit a class\">is A</error> {}");
        myFixture.checkHighlighting();
    }

   public void testPackageDeclAnnotator5() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "module Foo <error descr=\"module cannot compose a role\">does A</error> <error descr\"module cannot inherit a class\">is A</error> {}");
        myFixture.checkHighlighting();
    }

   public void testPackageDeclAnnotator6() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "package Foo <error descr=\"package cannot compose a role\">does A</error> {}");
        myFixture.checkHighlighting();
    }

   public void testPackageDeclAnnotator7() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "package Foo <error descr=\"package cannot inherit a class\">is A</error> {}");
        myFixture.checkHighlighting();
    }

   public void testPackageDeclAnnotator8() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "package Foo <error descr=\"package cannot compose a role\">does A</error> <error descr\"package cannot inherit a class\">is A</error> {}");
        myFixture.checkHighlighting();
    }

    public void testPackageDeclAlsoTraitAnnotator() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "package Foo { also <error descr=\"package cannot compose a role\">does A</error> }");
        myFixture.checkHighlighting();
    }

    public void testMonitorAnnotatorOnEmptyNameCase() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use v6.d.PREVIEW; monitor <error descr=\"Cannot use monitor type package without OO::Monitors module being included\">Bar</error> {}");
        myFixture.checkHighlighting();
    }

    public void testCompletelyFineReturn() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo() { return 42 }; foo();");
        myFixture.checkHighlighting();
    }

    public void testReturnOutsideOfRoutineListOp() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say 42; <error descr=\"Return outside of routine\">return 100</error>;");
        myFixture.checkHighlighting();
    }

    public void testReturnOutsideOfRoutineFunctionCall() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say 42; <error descr=\"Return outside of routine\">return(100)</error>;");
        myFixture.checkHighlighting();
    }

    public void testReturnInStartBlock() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "await start { <error descr=\"Cannot use return to produce a result in a start block\">return 100</error>; }");
        myFixture.checkHighlighting();
    }

    public void testReturnInSupplyBlock() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $s = supply { <error descr=\"Cannot use return to exit a supply block\">return 100</error>; }");
        myFixture.checkHighlighting();
    }

    public void testReturnInReactBlock() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "react { <error descr=\"Cannot use return to exit a react block\">return 100</error>; }");
        myFixture.checkHighlighting();
    }

    public void testReturnInWheneverBlock() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "react { whenever Supply.interval(1) { <error descr=\"Cannot use return in a whenever block\">return 100</error>; } }");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingParenFunctionCall() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say<error descr=\"Missing closing )\">(</error>42;");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingParenMethodCall() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "$*OUT.say<error descr=\"Missing closing )\">(</error>42");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingParenExpression() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say <error descr=\"Missing closing )\">(</error>42 + (4 * 3);");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingParenLoop() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "loop <error descr=\"Missing closing )\">(</error>my $i = 0; $i < 10; $i++ { }");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingParenVarDecl() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my <error descr=\"Missing closing )\">(</error>$, $");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingParenSignature() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo<error descr=\"Missing closing )\">(</error>$, { }; foo(4)");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingParenCall() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $a = { .say }; $a<error descr=\"Missing closing )\">(</error>42");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingArrayComposer() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @a = <error descr=\"Missing closing ]\">[</error>1,2,3");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingArrayIndexer() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @a = 1,2,3; say @a<error descr=\"Missing closing ]\">[</error>1;");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingBlockoid() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo <error descr=\"Missing closing }\">{</error> say 42;");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingRegexGroup() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say 'xxx' ~~ /a <error descr=\"Missing closing ]\">[</error> b | c /;");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingRegexAssertion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say 'xxx' ~~ /a <error descr=\"Missing closing >\"><</error>ident /;");
        myFixture.checkHighlighting();
    }

    public void testMissingClosingRegexCapture() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say 'xxx' ~~ /a <error descr=\"Missing closing )\">(</error> b | c /;");
        myFixture.checkHighlighting();
    }

    public void testColonPairSimplification() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = 5; sub a(:$foo) { $foo }; a(<weak_warning descr=\"Pair literal can be simplified\">:foo($foo)</weak_warning>)");
        myFixture.checkHighlighting();
    }

    public void testFatArrowSimplification() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = 5; <weak_warning descr=\"Pair literal can be simplified\">foo => $foo</weak_warning>");
        myFixture.checkHighlighting();
    }

    public void testColonPairWithBlockExpression() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = 5; :foo{$foo}");
        myFixture.checkHighlighting();
    }

    public void testWhileOne() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<weak_warning descr=\"Idiomatic 'loop' construction can be used instead\">while</weak_warning> 1 {}");
        myFixture.checkHighlighting();
    }

    public void testWhileTrue() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<weak_warning descr=\"Idiomatic 'loop' construction can be used instead\">while</weak_warning> True {}");
        myFixture.checkHighlighting();
    }

    public void testWhileCondition() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "while <error descr=\"Variable $foo is not declared\">$foo</error> != 10 {}");
        myFixture.checkHighlighting();
    }

    public void testWithAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<weak_warning descr=\"'with' construction can be used instead\">if 5.defined</weak_warning> {}");
        myFixture.checkHighlighting();
    }

    public void testWithoutAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<weak_warning descr=\"'without' construction can be used instead\">unless 5.defined</weak_warning> {}");
        myFixture.checkHighlighting();
    }

    public void testMultiWithAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<weak_warning descr=\"'with' construction can be used instead\">if 5.defined</weak_warning> {} <weak_warning descr=\"'with' construction can be used instead\">elsif 4.defined</weak_warning> {} else {}");
        myFixture.checkHighlighting();
    }

    public void testGrepFirstWhateverStarAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[Nil, Nil, 42, Nil]<weak_warning descr=\"Can be simplified into a single first method call\">.grep(*.defined).first</weak_warning>.say");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[Nil, Nil, 42, Nil]<weak_warning descr=\"Can be simplified into a single first method call\">.grep(*.defined).first()</weak_warning>.say()");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[Nil, Nil, 42, Nil].foo-call<weak_warning descr=\"Can be simplified into a single first method call\">.grep(*.defined).first</weak_warning>.say");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[Nil, Nil, 42, Nil].foo-call<weak_warning descr=\"Can be simplified into a single first method call\">.grep(*.defined).first()</weak_warning>.say()");
        myFixture.checkHighlighting();
    }

    public void testGrepFirstBlockAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[Nil, Nil, 42, Nil]<weak_warning descr=\"Can be simplified into a single first method call\">.grep({ $_.foo }).first</weak_warning>.say");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[Nil, Nil, 42, Nil]<weak_warning descr=\"Can be simplified into a single first method call\">.grep({ $_.foo }).first()</weak_warning>.say()");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[Nil, Nil, 42, Nil].foo-call<weak_warning descr=\"Can be simplified into a single first method call\">.grep({ .foo }).first</weak_warning>.say");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[Nil, Nil, 42, Nil].foo-call<weak_warning descr=\"Can be simplified into a single first method call\">.grep({ .foo }).first()</weak_warning>.say()");
        myFixture.checkHighlighting();

        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, ".grep(5 ~~ 10).first");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, ".grep(*.defined).first(*.bar)");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @a = (1..*)<weak_warning descr=\"Can be simplified into a single first method call\">.grep(* > 2).first</weak_warning>; say @a");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @a = (1..*)<weak_warning descr=\"Can be simplified into a single first method call\">.grep({ $_ > 2 }).first</weak_warning>; say @a");
        myFixture.checkHighlighting();
    }

    public void testSubmethodBUILDAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class A { <warning descr=\"BUILD should be declared as a submethod\">method</warning> BUILD {}; submethod BUILD {} }; class B { <warning descr=\"TWEAK should be declared as a submethod\">method</warning> TWEAK {}; submethod TWEAK {} }; sub <weak_warning descr=\"Unused subroutine\">BUILD</weak_warning> {}; sub <weak_warning descr=\"Unused subroutine\">TWEAK</weak_warning> {};");
        myFixture.checkHighlighting();
    }

    public void testEmptyInitializeAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @a = <weak_warning descr=\"Initialization of empty Array is redundant\">[]</weak_warning>; say @a");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @a = <weak_warning descr=\"Initialization of empty Array is redundant\">()</weak_warning>; say @a");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my %a = <weak_warning descr=\"Initialization of empty Hash is redundant\">()</weak_warning>; say %a");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my %a = <weak_warning descr=\"Initialization of empty Hash is redundant\">{}</weak_warning>; say %a");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @a = [1,2,3]; my %b = (1,2); say @a, %b");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @a = (1); my %b = {1}; say @a, %b");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $a = []; say $a");
        myFixture.checkHighlighting();
    }

    public void testPerl6ExecutableAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "say <warning descr=\"If the Raku executable is meant, consider using the $*EXECUTABLE.absolute() call that supports many platforms (e.g. GNU/Linux, Windows, etc.)\">'perl6'</warning>; run <warning descr=\"If the Raku executable is meant, consider using the $*EXECUTABLE.absolute() call that supports many platforms (e.g. GNU/Linux, Windows, etc.)\">'perl6'</warning>; run <warning descr=\"If the Raku executable is meant, consider using the $*EXECUTABLE.absolute() call that supports many platforms (e.g. GNU/Linux, Windows, etc.)\">\"perl6\"</warning>;");
        myFixture.checkHighlighting();
    }

    public void testListAssignmentAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my (@a, $x); say @a, $x");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my (@a, $x) := 4, (1,2,3); say @a, $x");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my ($x, @a) = 4, (1,2,3); say @a, $x");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my (<warning descr=\"Array slurps everything from assignment\">@a</warning>, $x) = (1,2,3), 4; say @a, $x");
        myFixture.checkHighlighting();
    }

    public void testReturnFromNilSubroutineAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub a(--> Nil) { if True { return; } }; a();");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub a(--> Nil) { <error descr=\"A value is returned from subroutine returning Nil\">return 42</error>; }; a();");
        myFixture.checkHighlighting();
    }

    public void testUnusedSimpleLexicalAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "my <weak_warning descr=\"Unused variable\">$x</weak_warning>; say 42;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "sub a() { my <weak_warning descr=\"Unused variable\">$x</weak_warning>; say 42; }; a();");
        myFixture.checkHighlighting();
    }

    public void testUnusedLexicalAnnotationOnlyCoversVariableName() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "my <weak_warning descr=\"Unused variable\">$x</weak_warning> = 99; say 42;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "sub a() { my <weak_warning descr=\"Unused variable\">$x</weak_warning> = 100; say 42; }; a();");
        myFixture.checkHighlighting();
    }

    public void testUnusedCallableLexicalAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "my <weak_warning descr=\"Unused variable\">&unused</weak_warning>; my &used = -> {}; used();");
        myFixture.checkHighlighting();
    }

    public void testUnusedLexicalMultipleDeclarationAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "my ($a, <weak_warning descr=\"Unused variable\">$b</weak_warning>); $a = 100; say $a;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "sub a() { my (<weak_warning descr=\"Unused variable\">$a</weak_warning>, $b); $b = 100; say $b; }; a();");
        myFixture.checkHighlighting();
    }

    public void testUnusedRoutineParameter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "sub foo($a, <weak_warning descr=\"Unused parameter\">$b</weak_warning>) { return $a; }; say foo(1, 2);");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                  "sub foo(&a, <weak_warning descr=\"Unused parameter\">&b</weak_warning>) { a() }; say foo({ 1 }, { 2 });");
        myFixture.checkHighlighting();
    }

    public void testUnusedBlockParameter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "my &foo = -> $a, <weak_warning descr=\"Unused parameter\">$b</weak_warning> { $a }; say foo(1, 2);");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "my &foo = -> &a, <weak_warning descr=\"Unused parameter\">&b</weak_warning> { a() }; say foo({ 1 }, { 2 });");
        myFixture.checkHighlighting();
    }

    public void testUnusedAttribute() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "class MyAttrsClass {\n" +
              "    has $!used;\n" +
              "    has <weak_warning descr=\"Unused attribute\">$!unused</weak_warning>;\n" +
              "    has ($!used-g, <weak_warning descr=\"Unused attribute\">$!unused-g</weak_warning>);\n" +
              "    method m() { $!used, $!used-g }\n" +
              "}");
        myFixture.checkHighlighting();
    }

    public void testImplicitUsesOfMatchVariable() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "class Actions {\n" +
                "    method a1(<weak_warning descr=\"Unused parameter\">$/</weak_warning>) {}\n" +
                "    method a2($/) { make $0.ast; }\n" +
                "    method a3($/) { make $<foo>.ast; }\n" +
                "    method a4($/) { make ~$/; }\n" +
                "}");
        myFixture.checkHighlighting();
    }

    public void testImplicitUsesOfTopicVariable() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "sub topic-unused(<weak_warning descr=\"Unused parameter\">$_</weak_warning>, $x) {\n" +
                "    given $x { when 1 { return 99 } }\n" +
                "    42 + $x.abs\n" +
                "}\n" +
                "sub topic-user-a($_) {\n" +
                "    when 42 { return \"answer\"; }\n" +
                "}\n" +
                "sub topic-user-b(Int $_) {\n" +
                "    .abs\n" +
                "}\n" +
                "sub topic-user-c(Int $_) {\n" +
                "    .abs.sin\n" +
                "}\n" +
                "sub topic-user-d(Int $_ is rw) {\n" +
                "    .=abs\n" +
                "}\n" +
                "topic-unused(1, -2), topic-user-a(2), topic-user-b(3),\n" +
                "        topic-user-c(4), topic-user-d($ = 5)");
        myFixture.checkHighlighting();
    }

    public void testUnusedPrivateMethod() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "class MyPrivateMethClass {\n" +
              "    method pub() { self!used }\n" +
              "    method !used() {}\n" +
              "    method <weak_warning descr=\"Unused private method\">!unused</weak_warning>() {}\n" +
              "}");
        myFixture.checkHighlighting();
    }

    public void testUnusedLexicalRoutine() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "sub used(&x) { x() }\n" +
              "sub <weak_warning descr=\"Unused subroutine\">unused</weak_warning>() {}\n" +
              "used(sub used-as-argument() {});");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "my sub used(&x) { x() }\n" +
              "my sub <weak_warning descr=\"Unused subroutine\">unused</weak_warning>() {}\n" +
              "used(my sub used-as-argument() {});");
        myFixture.checkHighlighting();
    }

    public void testDoesNotRecurse() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "(with <error descr=\"Variable $exclude is not declared\">$exclude</error> { 1 ~~ $_ }), (with <error descr=\"Variable $only-dir is not declared\">$only-dir</error> { 3 ~~ $_ })"
        );
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "(with <error descr=\"Variable $a is not declared\">$a</error> { 42 !~~ $_}), (with <error descr=\"Variable $b is not declared\">$b</error> { 42 ~~ $_});"
        );
        myFixture.checkHighlighting();
    }

    public void testSelfAvailabilityAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "say <error descr=\"No invocant is available here\">self</error>;\n" +
              "say <error descr=\"No invocant is available here\">$.a</error>;\n" +
              "class C {\n" +
              "    say <error descr=\"No invocant is available here\">self</error>;\n" +
              "    say <error descr=\"No invocant is available here\">$.a</error>;\n" +
              "    has $.a;\n" +
              "    has $.b = <error descr=\"Virtual method calls are not allowed on partially constructed objects\">$.a</error>;\n" +
              "    has $.c = self.a;\n" +
              "    method ok() {\n" +
              "        $.a, self, sub { $.a, self }\n" +
              "    }\n" +
              "    submethod partly-ok() {\n" +
              "        <error descr=\"Virtual method calls are not allowed on partially constructed objects\">$.a</error>, self, sub { <error descr=\"Virtual method calls are not allowed on partially constructed objects\">$.a</error>, self }\n" +
              "    }\n" +
              "    sub not-ok() {\n" +
              "        <error descr=\"No invocant is available here\">$.a</error>, <error descr=\"No invocant is available here\">self</error>\n" +
              "    }\n" +
              "}");
        myFixture.checkHighlighting();
    }

    public void testSelfAvailabilityInRegexDecl() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "grammar G {\n" +
              "    method m() {\n" +
              "        self\n" +
              "    }\n" +
              "    token t {\n" +
              "        x { self.m }\n" +
              "    }\n" +
              "}");
        myFixture.checkHighlighting();
    }

    public void testUselessMethodDeclarationAnnotation() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "method <warning descr=\"Useless declaration of a method outside of any package\">outside-of-class</warning>() {}\n" +
              "submethod <warning descr=\"Useless declaration of a method outside of any package\">outside-of-class-s</warning>() {}\n" +
              "package p {\n" +
              "    method <warning descr=\"Useless declaration of a method in a package\">in-a-package</warning>() {}\n" +
              "    submethod <warning descr=\"Useless declaration of a method in a package\">in-a-package-s</warning>() {}\n" +
              "}\n" +
              "module m {\n" +
              "    method <warning descr=\"Useless declaration of a method in a module\">in-a-module</warning>() {}\n" +
              "    submethod <warning descr=\"Useless declaration of a method in a module\">in-a-module-s</warning>() {}\n" +
              "}");
    }

    public void testReadOnlyScalarParameterAssignmentInSub() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "sub foo($a, $b is copy, $c is rw) {\n" +
              "    <error descr=\"Cannot assign to a readonly parameter\">$a = 100</error>;\n" +
              "    $b = 200;\n" +
              "    $c = 300;\n" +
              "\n" +
              "    <error descr=\"Cannot assign to a readonly parameter\">$a += $b</error>;\n" +
              "    $b += $c;\n" +
              "    $c += $a;\n" +
              "\n" +
              "    <error descr=\"Cannot assign to a readonly parameter\">$a++</error>;\n" +
              "    $b++;\n" +
              "    $c++;\n" +
              "\n" +
              "    <error descr=\"Cannot assign to a readonly parameter\">++$a</error>;\n" +
              "    ++$b;\n" +
              "    ++$c;\n" +
              "\n" +
              "    <error descr=\"Cannot assign to a readonly parameter\">$a.=sin</error>;\n" +
              "    $b.=sin;\n" +
              "    $c.=sin;\n" +
              "\n" +
              "    <error descr=\"Cannot assign to a readonly parameter\">$a .= sin</error>;\n" +
              "    $b .= sin;\n" +
              "    $c .= sin;\n" +
              "}\n" +
              "foo($, $, $);");
        myFixture.checkHighlighting();
    }

    public void testReadOnlyScalarParameterAssignmentWithPointyBlocks() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "for [1..10] -> $x {\n" +
              "    <error descr=\"Cannot assign to a readonly parameter\">$x++</error>;\n" +
              "}\n" +
              "for [1..10] <-> $x {\n" +
              "    $x++;\n" +
              "}");
        myFixture.checkHighlighting();
    }

    public void testCallArityMismatchAnnotating() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub ssss(:$named, :$shamed = 'not set') { $named; $shamed; }; ssss(:5named); ssss(:5named, :5shamed);");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo($a, $b, $c) { say $a + $b + $c }; my @a = 1, 2; foo(0, |@a);");
        myFixture.checkHighlighting();
        myFixture.configureByFile("CallArity.pm6");
        myFixture.checkHighlighting();
        myFixture.configureByFile("CallArityExtended.pm6");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "42.perl;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<error descr=\"Not enough positional arguments\">open</error>;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "open 'foo';");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $err = 42; run 'curl', 'foo', :!out, :$err;");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "run 'curl', 'foo', out => 42, :err(42);");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $a; $a.emit(42);");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo($a) is export { $a.emit(42) }");
        myFixture.checkHighlighting();
    }

    public void testCallArityMismatchAnnotatingOnAccessorCall() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "class C {\n" +
            "    my enum Context <Definition>;\n" +
            "    method completion(Int $in-level, Str $key, %params, Bool :$defn = False --> Str) {\n" +
            "        %params && $defn ?? ~$in-level !! $key\n" +
            "    }\n" +
            "    method another(Context $context, $in-level) {\n" +
            "        $.completion($in-level, ‘zero’, %( ), :defn($context == Definition))\n" +
            "    }\n" +
            "}");
        myFixture.checkHighlighting();
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "class C {\n" +
            "    my enum Context <Definition>;\n" +
            "    method completion(Int $in-level, Str $key, %params, Bool :$defn = False --> Str) {\n" +
            "        %params && $defn ?? ~$in-level !! $key\n" +
            "    }\n" +
            "    method another($in-level) {\n" +
            "        $.completion(<error descr=\"Not enough positional arguments\">$in-level, ‘zero’</error>)\n" +
            "    }\n" +
            "}");
        myFixture.checkHighlighting();
    }

    public void testUnknownRegexModifier() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "my $x = /<error descr=\"Unrecognized regex modifier\">:foo</error> 1234 /;\n" +
              "my $y = / <error descr=\"Unrecognized regex modifier\">:!bar</error> /;");
        myFixture.checkHighlighting();
    }

    public void testDuplicatedBranch() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "sub ib0($x, $y) is export {\n" +
            "    if $x {\n" +
            "        1\n" +
            "    }\n" +
            "    elsif $y {\n" +
            "        2\n" +
            "    }\n" +
            "    elsif <warning descr=\"An identical condition appears in a previous branch\">$x</warning> {\n" +
            "        3\n" +
            "    }\n" +
            "    else {\n" +
            "        0\n" +
            "    }\n" +
            "}");
        myFixture.checkHighlighting();

        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "sub ib1($x, $y) is export {\n" +
            "    if $x == $y {\n" +
            "        1\n" +
            "    }\n" +
            "    elsif <warning descr=\"An identical condition appears in a previous branch\">$x  == $y</warning> {\n" +
            "        2\n" +
            "    }\n" +
            "    else {\n" +
            "        0\n" +
            "    }\n" +
            "}");
        myFixture.checkHighlighting();

        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "sub ib2($x) is export {\n" +
            "    if $x {\n" +
            "        2\n" +
            "    }\n" +
            "    orwith $x {\n" +
            "        1\n" +
            "    }\n" +
            "    else {\n" +
            "        0\n" +
            "    }\n" +
            "}");
        myFixture.checkHighlighting();

        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "sub ib3($x, $y) is export {\n" +
            "    if $x == $y {\n" +
            "        1\n" +
            "    }\n" +
            "    elsif <warning descr=\"An identical condition appears in a previous branch\">$y == $x</warning> {\n" +
            "        2\n" +
            "    }\n" +
            "    else {\n" +
            "        0\n" +
            "    }\n" +
            "}");

        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "sub ib4($x, $y) is export {\n" +
            "    if $x < $y {\n" +
            "        1\n" +
            "    }\n" +
            "    elsif <warning descr=\"An identical condition appears in a previous branch\">$x > $y</warning> {\n" +
            "        2\n" +
            "    }\n" +
            "    else {\n" +
            "        0\n" +
            "    }\n" +
            "}");
        myFixture.checkHighlighting();

        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "sub ib5($x, $y) is export {\n" +
            "    if $x ≅ $y {\n" +
            "        1\n" +
            "    }\n" +
            "    elsif <warning descr=\"An identical condition appears in a previous branch\">$x =~= $y</warning> {\n" +
            "        2\n" +
            "    }\n" +
            "    else {\n" +
            "        0\n" +
            "    }\n" +
            "}");
        myFixture.checkHighlighting();
    }

    public void testDeprecatedSub() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
              "sub foo-a() is DEPRECATED {}\n" +
              "sub foo-b() is DEPRECATED('bar') {}\n" +
              "sub foo-c() {}\n" +
              "<warning descr=\"foo-a is deprecated\">foo-a</warning>();\n" +
              "<warning descr=\"foo-b is deprecated; use bar\">foo-b</warning>();\n" +
              "foo-c();");
        myFixture.checkHighlighting();
    }

    public void testDeprecatedMethod() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "class Testing {\n" +
            "    method foo-a() is DEPRECATED {}\n" +
            "    method foo-b() is DEPRECATED('bar') {}\n" +
            "    method foo-c() {}\n" +
            "}\n" +
            "Testing.<warning descr=\"foo-a is deprecated\">foo-a</warning>();\n" +
            "Testing.<warning descr=\"foo-b is deprecated; use bar\">foo-b</warning>();\n" +
            "Testing.foo-c();");
        myFixture.checkHighlighting();
    }

    public void testDubiousNonHashes() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
            "given 42 {\n" +
            "    my $x = 99;\n" +
            "    my $y = 101;\n" +
            "    say {};\n" +
            "    say { ; };\n" +
            "    say { \"I'm a closure\" };\n" +
            "    say { :a };\n" +
            "    say { a => 66 };\n" +
            "    say { :$x };\n" +
            "    say { :a, :b };\n" +
            "    say { a => 66, b => 666 };\n" +
            "    say { :$x, :$y };\n" +
            "    say { .foo };\n" +
            "    say { $_ };\n" +
            "    say <weak_warning desrc=\"This will be taken as a block, not as a hash as may have been intended\">{ :$^a }</weak_warning>;\n" +
            "    say <weak_warning desrc=\"This will be taken as a block, not as a hash as may have been intended\">{ :a, :b($_) }</weak_warning>;\n" +
            "    say <weak_warning desrc=\"This will be taken as a block, not as a hash as may have been intended\">{ a => 1, b => $_ }</weak_warning>;\n" +
            "}");
        myFixture.checkHighlighting();
    }

    public void testTopLevelUnused() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "say 0; <warning descr=\"Useless use of value in sink (void) context\">0;</warning> say 0;");
        myFixture.checkHighlighting();
    }

    public void testBlockUnused() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "our sub foo() { say 0; <warning descr=\"Useless use of value in sink (void) context\">0;</warning> say 0; }");
        myFixture.checkHighlighting();
    }

    public void testLastTopLevel() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "<warning descr=\"Useless use of value in sink (void) context\">0;</warning>");
        myFixture.checkHighlighting();
    }

    public void testReturnNotSpecified() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "our sub foo() { 0; }");
        myFixture.checkHighlighting();
    }

    public void testReturnNil() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "our sub foo(--> Nil) { <warning descr=\"Useless use of value in sink (void) context\">0;</warning> }");
        myFixture.checkHighlighting();
    }

    public void testCoreAnnotatedAsPure() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "<warning descr=\"Useless use of value in sink (void) context\">0 + 0;</warning>");
        myFixture.checkHighlighting();
    }

    public void testMultiNotAnnotatedAsPure() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "multi infix:<+>($, $) {};\n" +
                                  "0 + 0;");
        myFixture.checkHighlighting();
    }

    public void testSubAnnotatedAsPure() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "multi infix:<+>($, $) is pure {};\n" +
                                  "<warning descr=\"Useless use of value in sink (void) context\">0 + 0;</warning>");
        myFixture.checkHighlighting();
    }

    public void testProtoAnnotatedAsPure() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "proto infix:<+>($, $) is pure {}\n" +
                                  "multi infix:<+>($, $) {}\n" +
                                  "<warning descr=\"Useless use of value in sink (void) context\">0 + 0;</warning>");
        myFixture.checkHighlighting();
    }

    public void testProtoNotAnnotatedAsPure() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "proto infix:<+>($, $) {}\n" +
                                  "multi infix:<+>($, $) {}\n" +
                                  "0 + 0;");
        myFixture.checkHighlighting();
    }

    public void testHyphenInCharacterClass() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "/<[<warning descr=\"A hyphen is used in a character class, maybe '..' was intended to denote a range? Otherwise a hyphen should be at the end ofthe character class.\">-</warning>a..b<warning descr=\"A hyphen is used in a character class, maybe '..' was intended to denote a range? Otherwise a hyphen should be at the end ofthe character class.\">-</warning>cd-]>/"
        );
        myFixture.checkHighlighting();
    }
}
