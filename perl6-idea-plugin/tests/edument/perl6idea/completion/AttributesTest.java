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
import java.util.List;

public class AttributesTest extends LightCodeInsightFixtureTestCase {
    private Sdk testSdk;

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    @Override
    protected String getTestDataPath() {
        return "testData/completion";
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

    public void testOwnAttributes() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class C { has $!abc; method a() { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!", "$!abc")));
        assertEquals(2, vars.size());
    }

    public void testRoleAttributes() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Foo { has $!foo; has $.bar; }; class A does Foo { has $!a; method a() { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!", "$!a", "$!foo", "$!bar")));
        assertEquals(4, vars.size());
    }

    public void testNestedRoleAttributes() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "role Nested { has $!nested; }; role Foo does Nested { has $!foo; has $.bar; }; class A does Foo { has $!a; method a() { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!", "$!a", "$!foo", "$!bar", "$!nested")));
        assertEquals(5, vars.size());
    }

    public void testExternalAttributes() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "use NativeCall; class A does NativeCall::Native { has $!a; method a() { say $!<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!", "$!a", "$!setup", "$!is-clone")));
    }

    public void testOuterFileAttributes() {
        myFixture.configureByFiles("IdeaFoo/Bar1.pm6", "IdeaFoo/Baz.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!private", "$!visible", "$.visible")));
    }

    public void testOuterFileLongFormAttributes() {
        myFixture.configureByFiles("IdeaFoo/Bar2.pm6", "IdeaFoo/Baz.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!private", "$!visible", "$.visible")));
    }

    public void testOuterFileNestedAttributes() {
        myFixture.configureByFiles("IdeaFoo/Bar3.pm6", "IdeaFoo/Baz.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!private", "$!visible", "$.visible")));
    }

    public void testAttributeCompletionWithInnerClasses() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class C { has $!abc; class Inner { has $!xyz;  method m() { say $!<caret> } } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("$!", "$!xyz")));
        assertEquals(2, vars.size());
    }

    public void testDotAttribute() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Foo { has $.foo; has $.bar; method test { $<caret> } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertTrue(vars.containsAll(Arrays.asList("$.foo", "$.bar", "$!foo", "$!bar")));
    }

    public void testAfterDotCompletion() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE,
                                  "class Foo { has $.foo1; has $.foo2; method test { $.fo<caret>; } }");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertTrue(vars.containsAll(Arrays.asList(".foo1", ".foo2")));
    }
}
