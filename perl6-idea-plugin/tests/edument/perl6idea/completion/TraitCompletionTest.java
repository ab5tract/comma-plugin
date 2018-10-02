package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.sdk.Perl6SdkType;

import java.util.Collections;
import java.util.List;

public class TraitCompletionTest extends LightCodeInsightFixtureTestCase {
    private Sdk testSdk;

    @Override
    protected String getTestDataPath() {
        return "testData/codeInsight/localVariables";
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

    public void testCompletionForRoutineParameter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo($a is <caret>) {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Collections.singletonList("rw")));
        assertEquals(5, vars.size());
    }

    public void testCompletionForRoutine() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo is e<caret> {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Collections.singletonList("export")));
        assertEquals(6, vars.size());
    }

    public void testCompletionForMultipleTraits() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "sub foo is rw is e<caret> {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Collections.singletonList("export")));
        assertEquals(6, vars.size());
    }

    public void testCompletionForPackage() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "class Foo is I<caret> {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Collections.singletonList("Int")));
    }

    public void testExportTraitAbsenceForMyScopedVariables() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my $foo is exp<caret> {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertFalse(vars.contains("export"));
    }

    public void testExportTraitPresenceForOurScopedVariables() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "our $foo is exp<caret> {}");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNull(vars);
    }
}
