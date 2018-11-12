package edument.perl6idea.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.ex.temp.TempFileSystem;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.module.Perl6MetaDataComponent;
import edument.perl6idea.module.Perl6ModuleBuilder;
import org.jetbrains.annotations.SystemIndependent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class NewActionsTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    public void testNewScriptAction() {
        @SystemIndependent String basePath = getProject().getBasePath();
        Perl6ModuleBuilder.stubScript(basePath, "test.p6");
        File path = Paths.get(basePath, "test.p6").toFile();
        assertTrue(path.exists());
    }

    public void testNewModuleAction() throws IOException {
        Perl6MetaDataComponent metaData = myModule.getComponent(Perl6MetaDataComponent.class);
        String basePath = Paths.get(getProject().getBasePath(), "lib").toString();
        VirtualFile libVirtualFile = VirtualFileManager.getInstance().findFileByUrl("temp:///lib");
        Perl6ModuleBuilder.stubModule(metaData, basePath, "Foo::Bar", true, false, libVirtualFile);
        assertExists(Paths.get(basePath, "Foo", "Bar.pm6").toFile());

        VirtualFile meta = libVirtualFile.getParent().findChild("META6.json");
        checkMETA(meta,"Foo::Bar", "Foo/Bar.pm6");
        Perl6ModuleBuilder.stubModule(metaData, Paths.get(basePath, "lib").toString(), "Foo::Bar", false, false, null);
        assertExists(Paths.get(basePath, "lib", "Foo", "Bar.pm6").toFile());
        assertTrue(meta.exists());

        Perl6ModuleBuilder.stubModule(metaData, Paths.get(basePath, "lib").toString(), "Foo::Baz", false, false,null);
        assertExists(Paths.get(basePath, "lib", "Foo", "Baz.pm6").toFile());
        checkMETA(meta,"Foo::Bar", "Foo/Bar.pm6");
        checkMETA(meta, "Foo::Baz", "Foo/Baz.pm6");
    }

    public void testNewTestAction() {
        Project p = getProject();
        String basePath = p.getBasePath();
        Perl6ModuleBuilder.stubTest(basePath + "/t", "10-sanity", Collections.emptyList());
        Perl6ModuleBuilder.stubTest(basePath + "/t", "20-sanity.t", Collections.emptyList());
        assertExists(Paths.get(basePath, "t", "10-sanity.t").toFile());
        assertExists(Paths.get(basePath, "t", "20-sanity.t").toFile());
    }

    private static void checkMETA(VirtualFile basePath, String name, String path) {
        try {
            String metaContent = new String(basePath.contentsToByteArray(), CharsetToolkit.UTF8_CHARSET);
            assertTrue(metaContent.contains(name));
            assertTrue(metaContent.contains(path));
        } catch (IOException e) {
            assertTrue(false);
        }
    }
}
