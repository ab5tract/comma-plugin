package edument.rakuidea.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Function;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.language.RakuLanguageVersion;
import edument.rakuidea.module.builder.RakuModuleBuilderModule;
import edument.rakuidea.module.builder.RakuModuleBuilderScript;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class NewActionsTest extends CommaFixtureTestCase {
    public void testNewScriptAction() {
        Path basePath = Paths.get(getProject().getBasePath());
        RakuModuleBuilderScript.stubScript(basePath, "test.rakuidea", true, RakuLanguageVersion.D);
        File path = basePath.resolve("test.rakuidea").toFile();
        assertTrue(path.exists());
    }

    // FIXME
    // This test relies on a fact that META6.json stubbing  or editing event
    // must be sync with a module creation.
    // However, we are using `invokeLater` handler in RakuMetaDataComponent,
    // so the order of execution cannot be guaranteed and, in fact, it is almost
    // always not the needed one. On the other hand, usage of a sync version of meta
    // edition code causes exception in real IDE usage on e.g. module deletion.
    // The test is fragile and bad written by itself,
    // so better to ignore it for now.

    //public void testNewModuleAction() throws IOException {
    //    RakuMetaDataComponent metaData = myModule.getComponent(RakuMetaDataComponent.class);
    //    String basePath = Paths.get(getProject().getBasePath(), "lib").toString();
    //    RakuModuleBuilder.stubModule(metaData, basePath, "Foo::Bar", true, false, getProject().getBaseDir());
    //    assertExists(Paths.get(basePath, "Foo", "Bar.pm6").toFile());
    //
    //    VirtualFile meta = getProject().getBaseDir().findChild("META6.json");
    //    checkMETAContent(meta, s -> s.contains("Foo::Bar") && s.contains("Foo/Bar.pm6"));
    //    RakuModuleBuilder.stubModule(metaData, basePath, "Foo::Bar", false, false, null);
    //    assertTrue(Paths.get(basePath, "Foo", "Bar.pm6").toFile().exists());
    //    assertTrue(meta.exists());
    //
    //    RakuModuleBuilder.stubModule(metaData, basePath, "Foo::Baz", false, false,null);
    //    assertTrue(Paths.get(basePath, "Foo", "Baz.pm6").toFile().exists());
    //    checkMETAContent(meta, s -> s.contains("Foo::Bar") && s.contains("Foo/Bar.pm6"));
    //    checkMETAContent(meta, s -> s.contains("Foo::Baz") && s.contains("Foo/Baz.pm6"));
    //}

    public void testNewTestAction() {
        Project p = getProject();
        String basePath = p.getBasePath();
        RakuModuleBuilderModule.stubTest(Paths.get(basePath, "t"), "10-sanity", Collections.emptyList(), RakuLanguageVersion.D);
        RakuModuleBuilderModule.stubTest(Paths.get(basePath, "t"), "20-sanity.rakutest", Collections.emptyList(), RakuLanguageVersion.D);
        assertTrue(Paths.get(basePath, "t", "10-sanity.rakutest").toFile().exists());
        assertTrue(Paths.get(basePath, "t", "20-sanity.rakutest").toFile().exists());
    }

    private static void checkMETAContent(VirtualFile basePath, Function<String, Boolean> check) throws IOException {
        assertTrue(check.fun(new String(basePath.contentsToByteArray(), StandardCharsets.UTF_8)));
    }
}
