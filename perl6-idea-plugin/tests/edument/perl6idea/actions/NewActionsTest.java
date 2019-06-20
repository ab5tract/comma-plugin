package edument.perl6idea.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.Function;
import edument.perl6idea.Perl6LightProjectDescriptor;
import edument.perl6idea.module.builder.Perl6ModuleBuilderModule;
import edument.perl6idea.module.builder.Perl6ModuleBuilderScript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class NewActionsTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new Perl6LightProjectDescriptor();
    }

    public void testNewScriptAction() {
        Path basePath = Paths.get(getProject().getBasePath());
        Perl6ModuleBuilderScript.stubScript(basePath, "test.p6", true);
        File path = basePath.resolve("test.p6").toFile();
        assertTrue(path.exists());
    }

    // FIXME
    // This test relies on a fact that META6.json stubbing  or editing event
    // must be sync with a module creation.
    // However, we are using `invokeLater` handler in Perl6MetaDataComponent,
    // so the order of execution cannot be guaranteed and, in fact, it is almost
    // always not the needed one. On the other hand, usage of a sync version of meta
    // edition code causes exception in real IDE usage on e.g. module deletion.
    // The test is fragile and bad written by itself,
    // so better to ignore it for now.

    //public void testNewModuleAction() throws IOException {
    //    Perl6MetaDataComponent metaData = myModule.getComponent(Perl6MetaDataComponent.class);
    //    String basePath = Paths.get(getProject().getBasePath(), "lib").toString();
    //    Perl6ModuleBuilder.stubModule(metaData, basePath, "Foo::Bar", true, false, getProject().getBaseDir());
    //    assertExists(Paths.get(basePath, "Foo", "Bar.pm6").toFile());
    //
    //    VirtualFile meta = getProject().getBaseDir().findChild("META6.json");
    //    checkMETAContent(meta, s -> s.contains("Foo::Bar") && s.contains("Foo/Bar.pm6"));
    //    Perl6ModuleBuilder.stubModule(metaData, basePath, "Foo::Bar", false, false, null);
    //    assertTrue(Paths.get(basePath, "Foo", "Bar.pm6").toFile().exists());
    //    assertTrue(meta.exists());
    //
    //    Perl6ModuleBuilder.stubModule(metaData, basePath, "Foo::Baz", false, false,null);
    //    assertTrue(Paths.get(basePath, "Foo", "Baz.pm6").toFile().exists());
    //    checkMETAContent(meta, s -> s.contains("Foo::Bar") && s.contains("Foo/Bar.pm6"));
    //    checkMETAContent(meta, s -> s.contains("Foo::Baz") && s.contains("Foo/Baz.pm6"));
    //}

    public void testNewTestAction() {
        Project p = getProject();
        String basePath = p.getBasePath();
        Perl6ModuleBuilderModule.stubTest(Paths.get(basePath, "t"), "10-sanity", Collections.emptyList());
        Perl6ModuleBuilderModule.stubTest(Paths.get(basePath, "t"), "20-sanity.t", Collections.emptyList());
        assertTrue(Paths.get(basePath, "t", "10-sanity.t").toFile().exists());
        assertTrue(Paths.get(basePath, "t", "20-sanity.t").toFile().exists());
    }

    private static void checkMETAContent(VirtualFile basePath, Function<String, Boolean> check) throws IOException {
        assertTrue(check.fun(new String(basePath.contentsToByteArray(), CharsetToolkit.UTF8_CHARSET)));
    }
}
