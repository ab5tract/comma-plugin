package edument.perl6idea.module.builder;

import com.intellij.ide.util.projectWizard.ModuleNameLocationSettings;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.module.Perl6ModuleWizardStep;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class CroModuleBuilderApplication implements Perl6ModuleBuilderGeneric {
    private static final Logger LOG = Logger.getInstance(CroModuleBuilderApplication.class);
    private String myModuleName;
    private boolean myWebsocketSupport;
    private boolean myTemplatingSUpport;

    @Override
    public void setupRootModelOfPath(@NotNull ModifiableRootModel model, Path path) {
        Perl6MetaDataComponent metaData = model.getModule().getComponent(Perl6MetaDataComponent.class);
        Path directoryName = path.getFileName();
        if (Objects.equals(directoryName.toString(), "lib")) {
            stubRoutes(metaData, path, myWebsocketSupport, myTemplatingSUpport);
        } else if (Objects.equals(directoryName.toString(), "t")) {
            Perl6ModuleBuilderModule.stubTest(path, "00-sanity.t", Collections.singletonList(myModuleName));
        } else {
            stubCroDockerfile(path);
            stubCroServiceFile(path, myModuleName);
        }
    }

    @Override
    public void loadFromDialogData(Map<String, String> data) {
        myModuleName = data.get(Perl6ModuleWizardStep.MODULE_NAME);
        myWebsocketSupport = Boolean.parseBoolean(data.get(Perl6ModuleWizardStep.WEBSOCKET_SUPPORT));
        myTemplatingSUpport = Boolean.parseBoolean(data.get(Perl6ModuleWizardStep.TEMPLATE_SUPPORT));
    }

    @Override
    public String[] getSourceDirectories() {
        return new String[]{"lib", "t", ""};
    }

    private static void stubRoutes(Perl6MetaDataComponent metaData, Path path, boolean websocketSupport, boolean templatingSupport) {
        VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());
        String routesModulePath = Perl6ModuleBuilderModule.stubModule(metaData, path, "Routes", true, false,
                                            sourceRoot == null ? null : sourceRoot.getParent(), "Empty", false);
        VirtualFile routesFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(routesModulePath);

        List<String> routesTemplateLines = Perl6Utils.getResourceAsLines(
            websocketSupport ? "templates/WebsocketRoutes.pm6.template" : "templates/Routes.pm6.template");
        try {
            routesFile.setBinaryContent(
                String.join("\n", routesTemplateLines).getBytes(StandardCharsets.UTF_8)
            );
        }
        catch (IOException|NullPointerException e) {
            LOG.error(e);
        }
    }

    private static void stubCroDockerfile(Path sourcePath, boolean websocketSupport) {
        Path dockerFilePath = sourcePath.resolve("Dockerfile");
        String dockerfilePath = String.format("templates/%s", websocketSupport ? "WebsocketCroDockerfile" : "CroDockerfile");
        Perl6Utils.writeCodeToPath(dockerFilePath, Perl6Utils.getResourceAsLines(dockerfilePath));
    }

    private static void stubCroServiceFile(Path sourcePath, String moduleName) {
        String HOST_VARIABLE = convertToEnvName(moduleName) + "_HOST";
        String PORT_VARIABLE = convertToEnvName(moduleName) + "_PORT";
        Path croServiceFilePath = sourcePath.resolve( "service.p6");

        List<String> templateContent = Perl6Utils.getResourceAsLines("templates/service.p6.template");
        // Iterate over template lines, replacing stubs with actual data
        for (int i = 0; i < templateContent.size(); i++) {
            templateContent.set(i, templateContent.get(i)
                .replaceAll("\\$\\$HOST_VARIABLE\\$\\$", HOST_VARIABLE)
                .replaceAll("\\$\\$PORT_VARIABLE\\$\\$", PORT_VARIABLE));
        }
        Perl6Utils.writeCodeToPath(croServiceFilePath, templateContent);
    }

    private static String convertToEnvName(String name) {
        return name.replaceAll("[^\\w_]", "_").toUpperCase(Locale.ENGLISH);
    }

    @Override
    public void modifySettingsStep(SettingsStep step) {
        final ModuleNameLocationSettings nameField = step.getModuleNameLocationSettings();
        if (myModuleName != null && nameField != null)
            nameField.setModuleName(StringUtil.sanitizeJavaIdentifier(myModuleName));
    }

    @Override
    public boolean shouldBeMarkedAsRoot(String directoryName) {
        return !directoryName.isEmpty();
    }
}
