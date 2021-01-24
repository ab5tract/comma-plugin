package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

import java.util.List;

public class MethodCompletionTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/completion";
    }

    private List<String> complete(boolean isNull) {
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        if (isNull)
            assertNullOrEmpty(methods);
        else
            assertNotNull(methods);
        return methods;
    }

    private void doTestContainsAll(String text, String... contains) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, text);
        List<String> methods = complete(false);
        assertContainsElements(methods, contains);
    }

    private void doTestContainsAllTwoFiles(String fileA, String fileB, String... contains) {
        myFixture.configureByFiles(fileA, fileB);
        List<String> methods = complete(false);
        assertContainsElements(methods, contains);
    }

    private void doTestNotContainsAll(String text, String... contains) {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, text);
        List<String> methods = complete(false);
        assertDoesntContain(methods, contains);
    }

    public void testMethodOnSelfCompletion() {
        doTestContainsAll("class Foo { method a{}; method b{ self.<caret> } }", ".a", ".b");
    }

    public void testMethodOnSelfFromRoleCompletion() {
        doTestContainsAll("role Foo { method a {} }; class Bar does Foo { method b{ self.<caret> } }", ".a", ".b");
    }

    public void testMethodOnSelfFromParent() {
        doTestContainsAll("class Foo { method a {} }; class Bar is Foo { method b{ self.<caret> } }", ".a", ".b");
    }

    public void testMethodOnSelfFromOuterParent() {
        doTestContainsAllTwoFiles("IdeaFoo/Bar7.pm6", "IdeaFoo/Baz.pm6", ".visible");
    }

    public void testMethodOnSelfFromParentsRole() {
        doTestContainsAll("role Role { method role {} }; class Foo does Role { method a {} }; class Bar is Foo { method b{ self.<caret> } }",
                          ".a", ".b", ".role");
    }

    public void testMethodOnSelfFromAnyInheritance() {
        doTestContainsAll("class Foo { method foo { self.<caret> } }", ".sink", ".minpairs");
    }

    public void testMethodOnTypeNameOuterFileCompletion() {
        doTestContainsAllTwoFiles("IdeaFoo/Bar4.pm6", "IdeaFoo/Baz.pm6", ".visible");
    }

    public void testMethodOnLongTypeNameOuterFileCompletion() {
        doTestContainsAllTwoFiles("IdeaFoo/Bar5.pm6", "IdeaFoo/Baz.pm6", ".visible");
    }

    public void testMethodOnTypeNameCompletion() {
        doTestContainsAll("class Foo { method a{}; method b{ Foo.<caret> } }", ".a", ".b");
    }

    public void testMethodOnTypeNameFromRoleCompletion() {
        doTestContainsAll("role Foo { method a {} }; class Bar does Foo { method b{ Bar.<caret> } }", ".a", ".b");
    }

    public void testMethodOnTypeNameFromParent() {
        doTestContainsAll("class Foo { method a {} }; class Bar is Foo { method b{ Bar.<caret> } }", ".a", ".b");
    }

    public void testMethodOnTypeNameFromParentsRole() {
        doTestContainsAll("role Role { method role {} }; class Foo does Role { method a {} }; class Bar is Foo { method b{ Bar.<caret> } }",
                          ".a", ".b", ".role");
    }

    public void testMethodOnTypeNameFromAnyInheritance() {
        doTestContainsAll("class Foo { method foo { Foo.<caret> } }", ".sink", ".minpairs");
    }

    public void testMethodOnTypeFromCORE() {
        doTestContainsAll("Int.<caret>", ".Range");
    }

    public void testMethodOnTypeFromModule() throws InterruptedException {
        ensureModuleIsLoaded("NativeCall");
        doTestContainsAll("use NativeCall; Pointer.<caret>", ".of");
    }

    public void testPrivateMethodCompletion() {
        doTestContainsAll("class Foo { method !a{}; method !b{ self!<caret> } }", "!a", "!b");
    }

    public void testPrivateMethodFromRoleCompletion() {
        doTestContainsAll("role Bar { method !bar {}; }; class Foo does Bar { method !a{ self!<caret> } }", "!a", "!bar");
    }

    public void testPrivateMethodFromOuterRoleCompletion() {
        doTestContainsAllTwoFiles("IdeaFoo/Bar6.pm6", "IdeaFoo/Baz.pm6", "!private");
    }

    public void testPrivateMethodFromNestedRoleCompletion() {
        doTestContainsAll("role Baz { method !baz {} }; role Bar does Baz { method !bar {} }; class Foo does Bar { method !a { self!<caret> } }",
                          "!a", "!bar");
    }

    public void testPrivateMethodFromExternalRoleCompletion() throws InterruptedException {
        ensureModuleIsLoaded("NativeCall");
        doTestContainsAll("use NativeCall; role Foo does NativeCall::Native { method bar { self!<caret> } }", "!setup");
    }

    public void testCorrectImportGathering() throws InterruptedException {
        ensureModuleIsLoaded("NativeCall");
        // We don't get methods from NativeCall in another block, so `!setup` is not available
        doTestNotContainsAll("class Foo { { use NativeCall; }; class Bar does NativeCall::Native { method !b {}; method !a { self!<caret> } } }", "!setup");
    }

    public void testUnknownTypeHasAnyMuMethods() {
        doTestContainsAll("UnknownTypeName.<caret>", ".note", ".reduce", ".return-rw");
    }

    public void testSubmethodCompletion() {
        doTestContainsAll("class Foo { submethod subm {}; method foo { self.<caret> } }", ".foo", ".subm");
    }

    public void testSubmethodFromParent() {
        doTestNotContainsAll("class Base { submethod subm {} }; class Foo is Base { method foo { self.<caret> } }", ".subm");
    }

    public void testSubmethodFromRole() {
        doTestContainsAll("role Base { submethod subm {} }; class Foo does Base { method foo { self.<caret> } }", ".subm");
    }

    public void testSubmethodCalledFromOutside() {
        doTestContainsAll("class Foo { submethod foo {}; method bar {} }; Foo.<caret>", ".foo", ".foo");
    }

    public void testNarrowingOfPrivateMethods() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { method !bar {}; method !foo { self!<caret> } }");
        List<String> methods = complete(false);
        assertTrue(methods.stream().allMatch(p -> p.startsWith("!")));
    }

    public void testAccessors1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { has $!foo; method !a { self!<caret> } }");
        complete(true);
    }

    public void testAccessors2() {
        doTestContainsAll("class Foo { has $.foo; method a { self.<caret> } }", ".foo");
    }

    public void testAccessors3() {
        doTestContainsAll("class Foo { has $.foo; method !x() { }; method !a { self!<caret> } }", "!a", "!x");
    }

    public void testMethodsFromParametrizedRole() throws InterruptedException {
        ensureModuleIsLoaded("NativeCall");
        doTestContainsAll("use NativeCall; role Foo does NativeCall::Native[Foo, lib-path] { method bar { self!<caret> } }", "!setup");
    }

    public void testPrivateGettersInChildClasses() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Bar { has $.bar }; class Foo is Bar { method a { $!<caret> } }");
        complete(true);
    }

    public void testPublicGettersInChildClasses() {
        doTestContainsAll("class Bar { has $.bar }; class Foo is Bar { method a { self.ba<caret> } }", ".bar");
    }

    public void testParentPrivateMethodIsPrivate() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Bar { method !private {}; }; class Foo is Bar { method a { self!<caret> } }");
        complete(true);
    }

    public void testParentPrivateAttributeIsPrivate() {
        myFixture
            .configureByText(Perl6ScriptFileType.INSTANCE, "class Bar { has $!private; }; class Foo is Bar { method a { $!<caret> } }");
        complete(true);
    }

    public void testCOREClassMethodCompletion() {
        doTestContainsAll("class C is IO::Socket::Async { method a { self.<caret> } }", ".listen");
    }

    // Literal cases test
    public void testIntCompletion() {
        doTestContainsAll("1.<caret>", ".acosh", ".abs");
    }

    public void testRatCompletion() {
        doTestContainsAll("1.15.<caret>", ".denominator", ".polymod");
    }

    public void testNumCompletion() {
        doTestContainsAll("1e5.<caret>", ".is-prime", ".abs");
    }

    public void testStrCompletion() {
        doTestContainsAll("'Foo'.<caret>", ".match", ".samespace");
    }

    public void testComplexCompletion() {
        // FIXME `1i` is parsed as Int, not as Complex
        doTestContainsAll("1.<caret>", ".acosh", ".abs");
    }

    public void testArrayCompletion() {
        doTestContainsAll("[1, 2, 3].<caret>", ".reification-target", ".prepend");
    }

    public void testArrayEnclosedCompletion() {
        doTestContainsAll("([1, 2, 3]).<caret>", ".reification-target", ".prepend");
    }

    public void testCaptureCompletion() {
        doTestContainsAll("\\(1).<caret>", ".from-args", ".elems");
    }

    public void testColonPairCompletion() {
        doTestContainsAll(":foo.<caret>", ".freeze", ".antipair");
    }

    public void testGatherCompletion() {
        doTestContainsAll("gather {}.<caret>", ".iterator", ".lazy");
    }

    public void testStartCompletion() {
        doTestContainsAll("start {}.<caret>", ".kept", ".broken");
    }

    public void testSupplyCompletion() {
        doTestContainsAll("supply {}.<caret>", ".live", ".on-demand");
    }

    public void testVersionCompletion() {
        doTestContainsAll("(v1).<caret>", ".parts", ".plus");
    }

    public void testSpecialVariable1() {
        doTestContainsAll("$/.<caret>", ".made", ".prematch");
    }

    public void testSpecialVariable2() {
        doTestContainsAll("$3.<caret>", ".made", ".prematch");
    }

    public void testSpecialVariable3() {
        doTestContainsAll("$!.<caret>", ".resume", ".backtrace");
    }

    public void testSpecialVariable4() {
        doTestContainsAll("$<foo>.<caret>", ".made", ".prematch");
    }

    public void testTypedVar() {
        doTestContainsAll("my Exception $foo; $foo.<caret>", ".resume", ".backtrace");
    }

    public void testTypedVarWithSmiley() {
        doTestContainsAll("my Exception:D $foo; $foo.<caret>", ".resume", ".backtrace");
    }

    public void testTypedParameter() {
        doTestContainsAll("sub foo(Exception $foo) { $foo.<caret> }", ".resume", ".backtrace");
    }

    public void testTypedParameterWithSmiley() {
        doTestContainsAll("sub foo(Exception:D $foo) { $foo.<caret> }", ".resume", ".backtrace");
    }

    public void testTypedParameterWithCoercion() {
        doTestContainsAll("sub foo(Exception(Str) $foo) { $foo.<caret> }", ".resume", ".backtrace");
    }

    public void testTypedArrayVar() {
        doTestContainsAll("my @foo; @foo.<caret>", ".reification-target");
    }

    public void testTypedListParameter() {
        doTestContainsAll("sub foo(@foo) { @foo.<caret> }", ".reification-target");
    }

    public void testTypedHashVar1() {
        doTestContainsAll("my %foo; %foo.<caret>", ".dynamic");
    }
    public void testTypedHashVar2() {
        doTestNotContainsAll("my %foo; %foo.<caret>", ".reification-target");
    }

    public void testTypedMapParameter1() {
        doTestContainsAll("sub foo(%foo) { %foo.<caret> }",".IterationBuffer");
    }
    public void testTypedMapParameter2() {
        doTestNotContainsAll("sub foo(%foo) { %foo.<caret> }", ".reification-target");
    }

    public void testNewMethodOnTypeObjectCompletion() {
        doTestContainsAll("Int.new.<caret>", ".acos");
    }

    public void testAssignmentInference() {
        doTestContainsAll("my $foo = Int.new; $foo.<caret>", ".acos");
    }

    public void testAssignmentInferenceWithArguments() {
        doTestContainsAll("my $foo = Int.new('Non-existent argument'); $foo.<caret>", ".acos");
    }

    public void testLocalClassCase() {
        doTestContainsAll("class Cow { method moo {} }; my Cow $c .= new; $c.<caret>;", ".moo");
    }

    public void testBuiltInRegexCompletion() {
        doTestContainsAll("grammar A { rule a { <<caret> } }", "alpha");
    }

    public void testOwnRegexCompletion() {
        doTestContainsAll("grammar A { rule rule-a { <<caret> } }", "rule-a");
    }

    public void testInheritedRegexCompletion() {
        doTestContainsAll("grammar B { regex regex-a {''} }; grammar A  is B{ rule rule-a { <<caret> } }", "regex-a");
    }

    public void testMethodAsARuleInGrammarCompletion1() {
        doTestContainsAll("grammar B { method panic() {}; regex regex-a { <.<caret> } }", "panic");
    }

    public void testMethodAsARuleInGrammarCompletion2() {
        doTestContainsAll("grammar B { method panic() {}; regex regex-a { <.<caret> } }", "ast");
    }

    public void testGrammarFromSelfHasCursorMethods() {
        doTestContainsAll("grammar B { method panic() {}; method foo() { self.<caret> }; regex regex-a { <?> } }", ".ast", ".panic");
    }

    public void testGeneralizedMethodInferenceOnKeyword() {
        doTestContainsAll("my $foo = start {}; $foo.<caret>", ".keep");
    }

    public void testGeneralizedMethodInferenceOnLiteral() {
        doTestContainsAll("my $foo = 50; $foo.<caret>", ".acos");
    }

    public void testInferenceOnMultiPartTypeName() {
        doTestContainsAll("class Foo::Bar { method foo-bar {} }; Foo::Bar.<caret>", ".foo-bar");
    }

    public void testInferenceOnMultiPartTypedVar() {
        doTestContainsAll("class Foo::Bar { method foo-bar {} }; my Foo::Bar $foo = Foo::Bar.new; $foo.<caret>", ".foo-bar");
    }

    public void testAnnotatedTypeOverridesAssigned() {
        doTestContainsAll("my Int $foo = Str.new; $foo.<caret>", ".acos");
    }

    public void testRolesCompositionIsFlattened1() {
        doTestContainsAll("role A { has $!foo = 5 }; role B does A {}; class C does B { method a { say $!<caret> } }", "$!foo");
    }

    public void testRolesCompositionIsFlattened2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role A { has $!foo = 5 }; role B does A { method a { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        assertNull(myFixture.getLookupElementStrings());
    }

    public void testCompletionOfMultiMethodByType() {
        doTestContainsAll("class T { multi method test-me {}; multi method test-me {} }; sub foo(T $t) { $t.test-m<caret> };", ".test-me");
    }

    public void testCompletionOfSubsetExternalType() {
        doTestContainsAll("subset Frame of Backtrace::Frame; my Frame $frame; $frame.<caret>", ".is-hidden", ".is-setting");
    }

    public void testCompletionOfSubsetLocalType() {
        doTestContainsAll("class Local { method aaaa {}; method bbbbbbb {}; }; subset Frame of Local; my Frame $frame; $frame.<caret>",
                          ".aaaa", ".bbbbbbb");
    }

    public void testCompletionOfSubsetUndefinedType() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "subset Frame where * > 0; my Frame $frame; $frame.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.size() > 0);
    }

    public void testPackageWithoutNameCompletion() {
        doTestContainsAll("class { method b {}; method a { self.<caret> } }", ".a", ".b");
    }

    public void testRequireDoesNotThrow() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "require Test.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.size() > 0);
    }

    public void testReturnConstraint1() {
        doTestContainsAll("class A { method b(--> A) {}; method a(--> A) { self.a.<caret> } }", ".a", ".b");
    }

    public void testReturnConstraint2() {
        doTestContainsAll("class A { method b(--> A) {}; method a(--> A) { self.a.b.<caret> } }", ".a", ".b");
    }

    public void testReturnTrait1() {
        doTestContainsAll("class A { method b returns A {}; method a returns A { self.a.<caret> } }", ".a", ".b");
    }

    public void testReturnTrait2() {
        doTestContainsAll("class A { method b returns A {}; method a returns A { self.a.b.<caret> } }", ".a", ".b");
    }

    public void testMethodReturnTypeExternal() {
        doTestContainsAll("class A { method a returns A {}; method mmmmm {} }; A.a.<caret>", ".a", ".mmmmm");
    }

    public void testMethodReturnTypeWithParentheses() {
        doTestContainsAll("class A { method a(Int $foo) returns A { A.new; }; method mmmmm {} }; A.a(42).<caret>", ".a", ".mmmmm");
    }

    public void testSubReturnType() {
        doTestContainsAll("class C { method mmmm(--> C) { } }; sub foo(--> C) { C.new }; foo.<caret>", ".mmmm");
    }

    public void testNoExceptionForOf() {
        doTestContainsAll("class C { method mmmm() of C { } }; C.mmmm.<caret>", ".mmmm");
    }

    public void testReturnTypeNotSpecified() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "class C { method mmmm { } }; C.mmmm.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        assertNoThrowable(() -> myFixture.getLookupElementStrings());
    }

    public void testPrivateMethodReturnType() {
        doTestContainsAll("class C { method !bbbb {}; method !mmmm(--> C) { self!<caret> } }; >", "!mmmm", "!bbbb");
    }

    public void testAttributeTypeUsageAsCallReturn() {
        doTestContainsAll("class C { has C $.left; method mmm(--> C) { $.left.<caret> } }", ".mmm", ".left");
    }

    public void testTypedAttributeTypeUsageAsCallReturn() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class C { has C $.left; method mmm(--> C) { self.left.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        assertNoThrowable(() -> {
            List<String> methods = myFixture.getLookupElementStrings();
            assertTrue(methods.contains(".left"));
        });
    }

    public void testTypelessAttributeInference() {
        doTestNotContainsAll("class C { has $.a; method mmm(--> C) { self.a.<caret> } }", ".STORE_AT_KEY", ".returns");
    }

    public void testTypelessArrayAttributeInference() {
        doTestContainsAll("class C { has @.a; method mmm(--> C) { self.a.<caret> } }", ".FLATTENABLE_LIST");
    }

    public void testTypelessHashAttributeInference() {
        doTestContainsAll("class C { has %.a; method mmm(--> C) { self.a.<caret> } }", ".EXISTS-KEY");
    }

    public void testTypelessCallableAttributeInference() {
        doTestContainsAll("class C { has &.a; method mmm(--> C) { self.a.<caret> } }", ".returns");
    }

    public void testEnumValueCompletion() {
        doTestContainsAll("enum A <One Two>; say Two.<caret>", ".enums", ".acos");
    }

    public void testFullNameEnumValueCompletion() {
        doTestContainsAll("enum A <One Two>; say A::Two.<caret>", ".enums", ".acos");
    }

    public void testDynamicVariableMethodCompletion() {
        doTestContainsAll("$*COLLATION.<caret>", ".secondary", ".set");
    }

    public void testRecursiveVariableCompletion() {
        assertNoThrowable(() -> {
            myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                      "my $foo = $foo.<caret>");
            myFixture.complete(CompletionType.BASIC, 1);
        });
    }

    public void testRecursiveClassDefCompletion() {
        assertNoThrowable(() -> {
            myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo is Foo {}; Foo.<caret>");
            myFixture.complete(CompletionType.BASIC, 1);
        });
        assertNoThrowable(() -> {
            myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo does Foo {}; Foo.<caret>");
            myFixture.complete(CompletionType.BASIC, 1);
        });
    }

    public void testPrivateMethodsAreNotVisibleFromOutside() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class A { method !test2 {}; method !test {} }; A!<caret>;");
        complete(true);
    }

    public void testPrivateMethodsAreNotLeakedIntoLexicalClasses() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class A { method !test {}; my class B { method a { self!<caret> } } }");
        complete(true);
    }

    public void testAccessorsPrivacy1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class A { has $.test; my class B { method a { self!te<caret> } } }");
        complete(true);
    }

    public void testAccessorsPrivacy2() {
        doTestNotContainsAll("class A { has $.test; my class B { method a { self.te<caret> } } }", ".test");
    }

    public void testReturnTypeBasedExternal() {
        doTestContainsAll("42.abs.<caret>", ".polymod", ".chr");
    }

    public void testCompletionIsInOrder() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class A { method mmm {} }; class B is A { method m {} }; B.<caret>");
        List<String> methods = complete(false);
        assertEquals(".m", methods.get(0));
        assertEquals(".mmm", methods.get(1));
    }

    public void testNativeTypesCompletionDoNotThrow() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                "my atomicint $t; $t.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        assertNoThrowable(() -> myFixture.getLookupElementStrings());
    }

    public void testMethodOnExplicitlyTypedTopicCompletion() {
        doTestContainsAll("class Foo { has $.aaa; has $.bbb; }\n" +
                          "sub foo(Foo $_) { .<caret> }",
                          ".aaa", ".bbb");
    }

    public void testMethodOnTopicSetByGivenCompletion() {
        doTestContainsAll("class Foo { has $.aaa; has $.bbb; }\n" +
                        "sub foo() { given Foo.new { .<caret> } }",
                ".aaa", ".bbb");
    }

    public void testMethodOnTopicSetByWithCompletion() {
        doTestContainsAll("class Foo { has $.aaa; has $.bbb; }\n" +
                        "sub foo() { with Foo.new { .<caret> } }",
                ".aaa", ".bbb");
    }

    public void testMethodOnTopicSetByWithoutCompletion() {
        doTestContainsAll("class Foo { has $.aaa; has $.bbb; }\n" +
                        "sub foo() { without Foo.new { .<caret> } }",
                ".aaa", ".bbb");
    }

    public void testMethodOnTopicSetByGivenNotHiddenBySignaturedGivenCompletion() {
        doTestContainsAll("class Foo { has $.aaa; has $.bbb; }\n" +
                        "sub foo() { given Foo.new { given 42 -> $x { .<caret> } } }",
                ".aaa", ".bbb");
    }

    public void testMethodOnTopicSetByGivenNotHiddenBySignaturedWithCompletion() {
        doTestContainsAll("class Foo { has $.aaa; has $.bbb; }\n" +
                        "sub foo() { given Foo.new { with 42 -> $x { .<caret> } } }",
                ".aaa", ".bbb");
    }

    public void testMethodOnTopicSetByGivenNotHiddenBySignaturedWithoutCompletion() {
        doTestContainsAll("class Foo { has $.aaa; has $.bbb; }\n" +
                        "sub foo() { given Foo.new { without 42 -> $x { .<caret> } } }",
                ".aaa", ".bbb");
    }

    public void testMethodOnTopicSetByCatchCompletion() {
        doTestContainsAll("CATCH { default { .<caret> } }",
                ".resume");
    }

    public void testMethodOnTopicSetByControlCompletion() {
        doTestContainsAll("CONTROL { default { .<caret> } }",
                ".resume");
    }

    public void testMethodOnTopicCompletionRefinedByWhen() {
        doTestContainsAll("CATCH { when X::NYI { .<caret> } }",
                ".resume", ".feature");
    }

    public void testDeferredDefinition() {
        doTestContainsAll("class Foo {...}; Foo.m<caret>; class Foo { method mm {}; method mmm {}; method mmmm {} }",
                          ".mm", ".mmm", ".mmmm");
    }

    public void testTypedArrayForLoopTopicVariable() {
        doTestContainsAll(
            "class Pivo {\n" +
            "    has Str $.brewery;\n" +
            "    has Int $.ibu;\n" +
            "}\n" +
            "my Pivo @piva;\n" +
            "for @piva {\n" +
            "    .<caret>\n" +
            "}",
  ".brewery", ".ibu");
    }

    public void testTypedArrayForLoopTopicParameter() {
        doTestContainsAll(
            "class Pivo {\n" +
            "    has Str $.brewery;\n" +
            "    has Int $.ibu;\n" +
            "}\n" +
            "sub param(Pivo @piva) {\n" +
            "    for @piva {\n" +
            "        .<caret>\n" +
            "    }\n" +
            "}",
            ".brewery", ".ibu");
    }

    public void testTypedArrayForLoopTopicAttribute() {
        doTestContainsAll(
            "class Pivo {\n" +
            "    has Str $.brewery;\n" +
            "    has Int $.ibu;\n" +
            "}\n" +
            "class Store {\n" +
            "    has Pivo @.piva;\n" +
            "    method m() {\n" +
            "        for @!piva {\n" +
            "            .<caret>\n" +
            "        }\n" +
            "    }\n" +
            "}",
            ".brewery", ".ibu");
    }

    public void testTypedArrayForLoopTopicAccessor() {
        doTestContainsAll(
            "class Pivo {\n" +
            "    has Str $.brewery;\n" +
            "    has Int $.ibu;\n" +
            "}\n" +
            "class Store {\n" +
            "    has Pivo @.piva;\n" +
            "}\n" +
            "for Store.new.piva {\n" +
            "    .<caret>\n" +
            "}",
            ".brewery", ".ibu");
    }

    public void testTypedArrayForLoopParameterVariable() {
        doTestContainsAll(
            "class Pivo {\n" +
            "    has Str $.brewery;\n" +
            "    has Int $.ibu;\n" +
            "}\n" +
            "my Pivo @piva;\n" +
            "for @piva -> $pivo {\n" +
            "    $pivo.<caret>\n" +
            "}",
            ".brewery", ".ibu");
    }

    public void testTypedArrayForLoopParameterParameter() {
        doTestContainsAll(
            "class Pivo {\n" +
            "    has Str $.brewery;\n" +
            "    has Int $.ibu;\n" +
            "}\n" +
            "sub param(Pivo @piva) {\n" +
            "    for @piva -> $pivo {\n" +
            "        $pivo.<caret>\n" +
            "    }\n" +
            "}",
            ".brewery", ".ibu");
    }

    public void testTypedArrayForLoopParameterAttribute() {
        doTestContainsAll(
            "class Pivo {\n" +
            "    has Str $.brewery;\n" +
            "    has Int $.ibu;\n" +
            "}\n" +
            "class Store {\n" +
            "    has Pivo @.piva;\n" +
            "    method m() {\n" +
            "        for @!piva -> $pivo {\n" +
            "            $pivo.<caret>\n" +
            "        }\n" +
            "    }\n" +
            "}",
            ".brewery", ".ibu");
    }

    public void testTypedArrayForLoopParameterAccessor() {
        doTestContainsAll(
            "class Pivo {\n" +
            "    has Str $.brewery;\n" +
            "    has Int $.ibu;\n" +
            "}\n" +
            "class Store {\n" +
            "    has Pivo @.piva;\n" +
            "}\n" +
            "for Store.new.piva -> $pivo {\n" +
            "    $pivo.<caret>\n" +
            "}",
            ".brewery", ".ibu");
    }

    public void testUntypedHashIterationGivePairs() {
        doTestContainsAll(
            "my %h;\n" +
            "for %h {\n" +
            "    .<caret>\n" +
            "}",
            ".key", ".value");
    }

    public void testResolutionDoesNotDependOnTypeBeingInScope() {
        doTestContainsAll(
            "class Owner {\n" +
            "    my class Inner {\n" +
            "        has $.some-attr;\n" +
            "    }\n" +
            "    method m(--> Inner) {\n" +
            "        Inner.new\n" +
            "    }\n" +
            "}\n" +
            "Owner.m.<caret>",
            ".some-attr");
    }
}