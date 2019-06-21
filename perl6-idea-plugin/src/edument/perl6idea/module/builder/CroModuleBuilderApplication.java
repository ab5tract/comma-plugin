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
    private static final String CRO_RESOURCE_PREFIX = "templates/cro/";
    private static final Logger LOG = Logger.getInstance(CroModuleBuilderApplication.class);
    private String myModuleName;
    private boolean myWebsocketSupport;
    private boolean myTemplatingSUpport;

    @Override
    public void setupRootModelOfPath(@NotNull ModifiableRootModel model, Path path) {
        Path directoryName = path.getFileName();
        CroAppTemplateConfig conf = new CroAppTemplateConfig(myModuleName, myWebsocketSupport, myTemplatingSUpport);
        if (Objects.equals(directoryName.toString(), "lib")) {
            Perl6MetaDataComponent metaData = model.getModule().getComponent(Perl6MetaDataComponent.class);
            stubRoutes(metaData, path, conf);
        } else if (Objects.equals(directoryName.toString(), "t")) {
            Perl6ModuleBuilderModule.stubTest(path, "00-sanity.t", Collections.singletonList(myModuleName));
        } else {
            stubCroDockerfile(path, conf);
            stubCroServiceFile(path, conf);
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

    private static void stubRoutes(Perl6MetaDataComponent metaData, Path path, CroAppTemplateConfig conf) {
        VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());
        String templateContent = String.join("\n", Perl6Utils.getResourceAsLines(CRO_RESOURCE_PREFIX + "Routes.pm6"));
        String importLine = "";
        String routeLine = "";
        if (conf.websocketSupport) {
            String wsContent = String.join("\n", Perl6Utils.getResourceAsLines(CRO_RESOURCE_PREFIX + "ws.parts"));
            importLine = "\n" + wsContent.split("--")[0];
            routeLine = "\n" + wsContent.split("--")[1];
        }
        templateContent = templateContent
            .replace("$$WS_IMPORT$$", importLine)
            .replace("$$WS_ROUTE$$", routeLine);
        try {
            metaData.createStubMetaFile(conf.moduleName, sourceRoot.getParent(), false);
            VirtualFile routesFile = sourceRoot.findOrCreateChildData(CroModuleBuilderApplication.class, "Routes.pm6");
            routesFile.setBinaryContent(
                String.join("\n", templateContent).getBytes(StandardCharsets.UTF_8)
            );
        }
        catch (IOException|NullPointerException e) {
            LOG.error(e);
        }
    }

    private static void stubCroDockerfile(Path sourcePath, CroAppTemplateConfig conf) {
        Path dockerFilePath = sourcePath.resolve("Dockerfile");
        String dockerfilePath = CRO_RESOURCE_PREFIX + "Dockerfile";
        List<String> templateContent = getAndUnstubResource(dockerfilePath, conf);
        Perl6Utils.writeCodeToPath(dockerFilePath, templateContent);
    }

    private static void stubCroServiceFile(Path sourcePath, CroAppTemplateConfig conf) {
        Path croServiceFilePath = sourcePath.resolve( "service.p6");
        List<String> templateContent = getAndUnstubResource(CRO_RESOURCE_PREFIX + "service.p6", conf);
        Perl6Utils.writeCodeToPath(croServiceFilePath, templateContent);
    }

    @NotNull
    private static List<String> getAndUnstubResource(String resourcePath, CroAppTemplateConfig conf) {
        final String HOST_VARIABLE = convertToEnvName(conf.moduleName) + "_HOST";
        final String PORT_VARIABLE = convertToEnvName(conf.moduleName) + "_PORT";
        final String DOCKER_IMAGE = conf.websocketSupport ? "cro-http-websocket:0.8.0" : "cro-http:0.8.0";
        List<String> templateContent = Perl6Utils.getResourceAsLines(resourcePath);
        for (int i = 0; i < templateContent.size(); i++) {
            templateContent.set(i, templateContent.get(i)
                .replaceAll("\\$\\$HOST_VARIABLE\\$\\$", HOST_VARIABLE)
                .replaceAll("\\$\\$PORT_VARIABLE\\$\\$", PORT_VARIABLE)
                .replaceAll("\\$\\$DOCKER_IMAGE\\$\\$", DOCKER_IMAGE));
        }
        return templateContent;
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

    public class CroAppTemplateConfig {
        public String moduleName;
        public boolean websocketSupport;
        public boolean templatingSupport;

        public CroAppTemplateConfig(String moduleName, boolean websocketSupport, boolean templatingSupport) {
            this.moduleName = moduleName;
            this.websocketSupport = websocketSupport;
            this.templatingSupport = templatingSupport;
        }
    }
}
