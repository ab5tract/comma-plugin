package edument.rakuidea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import edument.rakuidea.psi.RakuFile;
import edument.rakuidea.sdk.RakuSdkType;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;

@Ignore
public class CommaFixtureTestCase extends BasePlatformTestCase {
    protected Sdk testSdk;

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new RakuLightProjectDescriptor();
    }

    protected void ensureModuleIsLoaded(String moduleName) throws InterruptedException {
        ensureModuleIsLoaded(moduleName, "use");
    }

    protected void ensureModuleIsLoaded(String moduleName, String invocation) throws InterruptedException {
        RakuSdkType sdkType = (RakuSdkType)testSdk.getSdkType();
        RakuFile file = sdkType.getPsiFileForModule(getProject(), moduleName, invocation + " " + moduleName);
        while (file.getName().equals("DUMMY")) {
            Thread.sleep(100);
            file = sdkType.getPsiFileForModule(getProject(), moduleName, invocation + " " + moduleName);
        }
    }

    private void ensureSetting() throws InterruptedException {
        RakuSdkType sdkType = (RakuSdkType)testSdk.getSdkType();
        RakuFile file = sdkType.getCoreSettingFile(getProject());
        while (file.getName().equals("DUMMY")) {
            Thread.sleep(100);
            file = sdkType.getCoreSettingFile(getProject());
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ApplicationManager.getApplication().runWriteAction(() -> {
            String homePath = RakuSdkType.getInstance().suggestHomePath();
            assertNotNull("Found a perl6 in path to use in tests", homePath);
            testSdk = SdkConfigurationUtil.createAndAddSDK(homePath, RakuSdkType.getInstance());
            ProjectRootManager.getInstance(myFixture.getModule().getProject()).setProjectSdk(testSdk);
        });
        ensureSetting();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            RakuSdkType sdkType = (RakuSdkType)testSdk.getSdkType();
            sdkType.invalidateFileCaches(getProject());
            SdkConfigurationUtil.removeSdk(testSdk);
        } catch (Exception ignored) {
        }
        finally {
            super.tearDown();
        }
    }
}
