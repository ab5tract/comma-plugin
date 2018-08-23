package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodCompletionTest extends LightCodeInsightFixtureTestCase {
    private Sdk testSdk;

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
    protected String getTestDataPath() {
        return "testData/completion";
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    @Override
    protected void tearDown() throws Exception {
        SdkConfigurationUtil.removeSdk(testSdk);
        super.tearDown();
    }

    public void testMethodOnSelfCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { method a{}; method b{ self.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".a", ".b")));
    }

    public void testMethodOnSelfFromRoleCompletion() {
        myFixture
            .configureByText(Perl6ScriptFileType.INSTANCE, "role Foo { method a {} }; class Bar does Foo { method b{ self.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".a", ".b")));
    }

    public void testMethodOnSelfFromParent() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { method a {} }; class Bar is Foo { method b{ self.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".a", ".b")));
    }

    public void testMethodOnSelfFromOuterParent() {
        myFixture.configureByFiles("IdeaFoo/Bar7.pm6", "IdeaFoo/Baz.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.contains(".visible"));
    }

    public void testMethodOnSelfFromParentsRole() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Role { method role {} }; class Foo does Role { method a {} }; class Bar is Foo { method b{ self.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".a", ".b", ".role")));
    }

    public void testMethodOnSelfFromAnyInheritance() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { method foo{ self.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".sink", ".minpairs")));
    }


    public void testMethodOnTypeNameOuterFileCompletion() {
        myFixture.configureByFiles("IdeaFoo/Bar4.pm6", "IdeaFoo/Baz.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.contains(".visible"));
    }

    public void testMethodOnLongTypeNameOuterFileCompletion() {
        myFixture.configureByFiles("IdeaFoo/Bar5.pm6", "IdeaFoo/Baz.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.contains(".visible"));
    }

    public void testMethodOnTypeNameCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { method a{}; method b{ Foo.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".a", ".b")));
    }

    public void testMethodOnTypeNameFromRoleCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "role Foo { method a {} }; class Bar does Foo { method b{ Bar.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".a", ".b")));
    }

    public void testMethodOnTypeNameFromParent() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { method a {} }; class Bar is Foo { method b{ Bar.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".a", ".b")));
    }

    public void testMethodOnTypeNameFromParentsRole() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Role { method role {} }; class Foo does Role { method a {} }; class Bar is Foo { method b{ Bar.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".a", ".b", ".role")));
    }

    public void testMethodOnTypeNameFromAnyInheritance() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { method foo { Foo.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".sink", ".minpairs")));
    }

    public void testMethodOnTypeFromCORE() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "Int.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.contains(".Range"));
    }

    public void testMethodOnTypeFromModule() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use NativeCall; Pointer.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.contains(".of"));
    }

    public void testPrivateMethodCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { method !a{}; method !b{ self!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList("!a", "!b")));
    }

    public void testPrivateMethodFromRoleCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Bar { method !bar {}; }; class Foo does Bar { method !a{ self!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList("!a", "!bar")));
    }

    public void testPrivateMethodFromOuterRoleCompletion() {
        myFixture.configureByFiles("IdeaFoo/Bar6.pm6", "IdeaFoo/Baz.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.contains("!private"));
    }

    public void testPrivateMethodFromNestedRoleCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Baz { method !baz {} }; role Bar does Baz { method !bar {} }; class Foo does Bar { method !a { self!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList("!a", "!bar")));
    }

    public void testPrivateMethodFromExternalRoleCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "use NativeCall; role Foo does NativeCall::Native { method bar { self!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Collections.singletonList("!setup")));
    }

    public void testCorrectImportGathering() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Foo { { use NativeCall; }; class Bar does NativeCall::Native { method !b {}; method !a { self!<caret> } } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        // We don't get methods from NativeCall in another block, so `!setup` is not available
        assertTrue(!methods.contains("!setup"));
    }

    public void testUnknownTypeHasAnyMuMethods() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "UnknownTypeName.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".note", ".reduce", ".return-rw")));
    }

    public void testSubmethodCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { submethod subm {}; method foo { self.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".foo", ".subm")));
    }

    public void testSubmethodFromParent() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Base { submethod subm {} }; class Foo is Base { method foo { self.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(!methods.containsAll(Collections.singletonList(".subm")));
    }

    public void testSubmethodFromRole() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Base { submethod subm {} }; class Foo does Base { method foo { self.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Collections.singletonList(".subm")));
    }

    public void testSubmethodCalledFromOutside() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { submethod foo {}; method bar {} }; Foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Arrays.asList(".foo", ".foo")));
    }

    public void testNarrowingOfPrivateMethods() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { method !bar {}; method !foo { self!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.stream().allMatch(p -> p.startsWith("!")));
    }

    public void testAccessors1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { has $!foo; method !a { self!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNull(methods);
    }

    public void testAccessors2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { has $.foo; method a { self.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".foo"));
    }

    public void testAccessors3() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo { has $.foo; method !a { self!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList("!a", "!foo")));
    }

    public void testMethodsFromParametrizedRole() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "use NativeCall; role Foo does NativeCall::Native[Foo, lib-path] { method bar { self!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertTrue(methods.containsAll(Collections.singletonList("!setup")));
    }

    public void testPrivateGettersInChildClasses() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Bar { has $.bar }; class Foo is Bar { method a { $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNull(methods);
    }

    public void testPublicGettersInChildClasses() {
        myFixture
            .configureByText(Perl6ScriptFileType.INSTANCE, "class Bar { has $.bar }; class Foo is Bar { method a { self.ba<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".bar"));
    }

    public void testParentPrivateMethodIsPrivate() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Bar { method !private {}; }; class Foo is Bar { method a { self!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertEquals(0, methods.size());
    }

    public void testParentPrivateAttributeIsPrivate() {
        myFixture
            .configureByText(Perl6ScriptFileType.INSTANCE, "class Bar { has $!private; }; class Foo is Bar { method a { $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNull(methods);
    }

    public void testCOREClassMethodCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class C is IO::Socket::Async { method a { self.<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".listen"));
    }

    // Literal cases test
    public void testIntCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "1.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".acosh", ".abs")));
    }

    public void testRatCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "1.15.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".denominator", ".polymod")));
    }

    public void testNumCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "1e5.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".is-prime", ".abs")));
    }

    public void testStrCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "'Foo'.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".match", ".samespace")));
    }

    public void testComplexCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "1.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".acosh", ".abs")));
    }

    public void testArrayCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "[1, 2, 3].<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".reification-target", ".prepend")));
    }

    public void testArrayEnclosedCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "([1, 2, 3]).<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".reification-target", ".prepend")));
    }

    public void testCaptureCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "\\(1).<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".from-args", ".elems")));
    }

    public void testColonPairCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, ":foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".freeze", ".antipair")));
    }

    public void testGatherCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "gather {}.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".iterator", ".lazy")));
    }

    public void testStartCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "start {}.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".kept", ".broken")));
    }

    public void testSupplyCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "supply {}.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".live", ".on-demand")));
    }

    public void testVersionCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "(v1).<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".parts", ".plus")));
    }

    public void testSpecialVariable1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "$/.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".made", ".prematch")));
    }

    public void testSpecialVariable2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "$3.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".made", ".prematch")));
    }

    public void testSpecialVariable3() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "$!.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".resume", ".backtrace")));
    }

    public void testSpecialVariable4() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "$<foo>.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".made", ".prematch")));
    }

    public void testTypedVar() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my Exception $foo; $foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".resume", ".backtrace")));
    }

    public void testTypedVarWithSmiley() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my Exception:D $foo; $foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".resume", ".backtrace")));
    }

    public void testTypedParameter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo(Exception $foo) { $foo.<caret> }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".resume", ".backtrace")));
    }

    public void testTypedParameterWithSmiley() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo(Exception:D $foo) { $foo.<caret> }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".resume", ".backtrace")));
    }

    public void testTypedParameterWithCoercion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo(Exception(Str) $foo) { $foo.<caret> }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Arrays.asList(".resume", ".backtrace")));
    }

    public void testTypedArrayVar() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my @foo; @foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Collections.singletonList(".reification-target")));
    }

    public void testTypedListParameter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo(@foo) { @foo.<caret> }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".reification-target"));
    }

    public void testTypedHashVar() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my %foo; %foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".dynamic"));
        assertFalse(methods.contains(".reification-target"));
    }

    public void testTypedMapParameter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo(%foo) { %foo.<caret> }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".IterationBuffer"));
        assertFalse(methods.contains(".reification-target"));
    }

    public void testNewMethodOnTypeObjectCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "Int.new.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.containsAll(Collections.singletonList(".acos")));
    }

    public void testAssignmentInference() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = Int.new; $foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".acos"));
    }

    public void testAssignmentInferenceWithArguments() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = Int.new('Non-existent argument'); $foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".acos"));
    }

    public void testLocalClassCase() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Cow { method moo {} }; my Cow $c .= new; $c.<caret>;");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".moo"));
    }

    public void testBuiltInRegexCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "grammar A { rule a { <<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains("alpha"));
    }

    public void testOwnRegexCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "grammar A { rule rule-a { <<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains("rule-a"));
    }

    public void testInheritedRegexCompletion() {
        myFixture
            .configureByText(Perl6ScriptFileType.INSTANCE, "grammar B { regex regex-a {''} }; grammar A  is B{ rule rule-a { <<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains("regex-a"));
    }

    public void testGeneralizedMethodInferenceOnKeyword() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = start {}; $foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".keep"));
    }

    public void testGeneralizedMethodInferenceOnLiteral() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo = 50; $foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".acos"));
    }

    public void testInferenceOnMultiPartTypeName() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo::Bar { method foo-bar {} }; Foo::Bar.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".foo-bar"));
    }

    public void testInferenceOnMultiPartTypedVar() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo::Bar { method foo-bar {} }; my Foo::Bar $foo = Foo::Bar.new; $foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".foo-bar"));
    }

    public void testAnnotatedTypeOverridesAssigned() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my Int $foo = Str.new; $foo.<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertTrue(methods.contains(".acos"));
    }

    public void testRolesCompositionIsFlattened1() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role A { has $!foo = 5 }; role B does A {}; class C does B { method a { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.contains("$!foo"));
    }

    public void testRolesCompositionIsFlattened2() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role A { has $!foo = 5 }; role B does A { method a { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        assertNull(myFixture.getLookupElementStrings());
    }
}