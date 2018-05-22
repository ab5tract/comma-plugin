package edument.perl6idea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.sdk.Perl6SdkType;

import java.util.Arrays;
import java.util.List;

public class TypeCompletionTest extends LightCodeInsightFixtureTestCase {
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
    protected void tearDown() throws Exception {
        SdkConfigurationUtil.removeSdk(testSdk);
        super.tearDown();
    }

    public void testTypesFromSetting() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my In<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("Instant", "Int")));
    }

    public void testMultipartTypesFromSetting() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "my IO::<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("IO::Path", "IO::Handle")));
    }

    public void testSanityNoNativeCallWithoutImport() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "say NativeCal<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertEmpty(vars);
    }

    public void testUseGlobalSymbol() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "use NativeCall; say NativeCal<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("NativeCall::CStr", "NativeCall::Compiler::GNU")));
    }

    public void testNeedGlobalSymbol() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "need NativeCall; say NativeCal<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("NativeCall::CStr", "NativeCall::Compiler::GNU")));
    }
}
