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

public class SubCompletionTest extends LightCodeInsightFixtureTestCase {
    private Sdk testSdk;

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

    public void testCompletionFromLocal() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo() {}\nfo<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Collections.singletonList("foo")));
        assertEquals(2, vars.size());
    }

    public void testCompletionFromOuter() {
        myFixture.configureByFiles("IdeaFoo/Bar8.pm6", "IdeaFoo/Baz.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNull(vars);
    }

    public void testComplectionFromOurLocal() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our sub foo() {}\nfo<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Collections.singletonList("foo")));
        assertEquals(2, vars.size());
    }

    public void testCompletionFromCORE() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "se<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("sec", "sech", "set")));
        assertEquals(17, vars.size());
    }

    public void testCompletionFromImport() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use Test;\nis-<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("is-approx", "is-deeply", "isa-ok")));
        assertEquals(4, vars.size());
    }
}