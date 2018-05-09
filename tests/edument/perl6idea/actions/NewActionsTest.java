package edument.perl6idea.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import edument.perl6idea.module.Perl6ModuleBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static java.io.File.separator;

public class NewActionsTest extends LightPlatformCodeInsightFixtureTestCase {
    public void testNewScriptAction() {
        Perl6ModuleBuilder.stubScript(getProject().getBasePath(), "test.p6");
        File path = Paths.get(getProject().getBasePath(), "test.p6").toFile();
        assertTrue(path.exists());
    }

    public void testNewModuleAction() {
        Project p = getProject();
        String basePath = p.getBasePath();
        Perl6ModuleBuilder.stubModule(p, basePath, "Foo::Bar", null, true);
        assertExists(Paths.get(basePath, "Foo", "Bar.pm6").toFile());
        Perl6ModuleBuilder.stubModule(p, Paths.get(basePath, "lib").toString(), "Foo::Bar", null, true);
        assertExists(Paths.get(basePath, "lib", "Foo", "Bar.pm6").toFile());
        assertExists(Paths.get(basePath, "META6.json").toFile());
        checkMETA(basePath,"Foo::Bar", "Foo" + separator + "Bar.pm6");
        Perl6ModuleBuilder.stubModule(p, Paths.get(basePath, "lib").toString(), "Foo::Baz", null, false);
        assertExists(Paths.get(basePath, "lib", "Foo", "Baz.pm6").toFile());
        checkMETA(basePath,"Foo::Bar", "Foo" + separator + "Bar.pm6");
        checkMETA(basePath, "Foo::Baz", "Foo" + separator + "Baz.pm6");
    }

    public void testNewTestAction() {
        Project p = getProject();
        String basePath = p.getBasePath();
        Perl6ModuleBuilder.stubTest(basePath + separator + "t", "10-sanity", Collections.emptyList());
        Perl6ModuleBuilder.stubTest(basePath + separator + "t", "20-sanity.t", Collections.emptyList());
        assertExists(Paths.get(basePath, "t", "10-sanity.t").toFile());
        assertExists(Paths.get(basePath, "t", "20-sanity.t").toFile());
    }

    private static void checkMETA(String basePath, String name, String path) {
        try {
            String metaInfo =
              new String(Files.readAllBytes(Paths.get(basePath, "META6.json")), CharsetToolkit.UTF8_CHARSET);
            assertTrue(metaInfo.contains(name));
            assertTrue(metaInfo.contains(path));
        } catch (IOException e) {
            assertTrue(false);
        }
    }
}
