package edument.perl6idea.module.builder;

import com.intellij.ide.util.projectWizard.ModuleNameLocationSettings;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.module.Perl6ModuleWizardStep;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CroModuleBuilderApplication implements Perl6ModuleBuilderGeneric {
    private static final String CRO_RESOURCE_PREFIX = "templates/cro/";
    private static final Logger LOG = Logger.getInstance(CroModuleBuilderApplication.class);
    private String myModuleName;
    private boolean myWebsocketSupport;
    private boolean myTemplatingSupport;

    @Override
    public void setupRootModelOfPath(@NotNull ModifiableRootModel model, Path path) {
        Path directoryName = path.getFileName();
        CroAppTemplateConfig conf = new CroAppTemplateConfig(myModuleName, myWebsocketSupport, myTemplatingSupport);
        if (Objects.equals(directoryName.toString(), "lib")) {
            Perl6MetaDataComponent metaData = model.getModule().getService(Perl6MetaDataComponent.class);
            stubRoutes(metaData, path, conf);
        } else if (Objects.equals(directoryName.toString(), "t")) {
            stubCroTest(path, conf);
        } else {
            stubCroDockerfile(path, conf);
            stubCroServiceFile(path, conf);
            stubCroYAML(path, conf);
            if (myTemplatingSupport)
                stubTemplatesDirectory(path, conf);
        }
    }

    @Override
    public void loadFromDialogData(Map<String, String> data) {
        myModuleName = data.get(Perl6ModuleWizardStep.MODULE_NAME);
        myWebsocketSupport = Boolean.parseBoolean(data.get(Perl6ModuleWizardStep.WEBSOCKET_SUPPORT));
        myTemplatingSupport = Boolean.parseBoolean(data.get(Perl6ModuleWizardStep.TEMPLATE_SUPPORT));
    }

    @Override
    public String[] getSourceDirectories() {
        return new String[]{"lib", "t", ""};
    }

    private static void stubRoutes(Perl6MetaDataComponent metaData, Path path, CroAppTemplateConfig conf) {
        VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());
        String templateContent = String.join("\n", Perl6Utils.getResourceAsLines(CRO_RESOURCE_PREFIX + "Routes.pm6"));
        templateContent = populateRouteVariables(templateContent, conf.websocketSupport, "ws.parts", "WS", conf);
        templateContent = populateRouteVariables(templateContent, conf.templatingSupport, "crotmp.parts", "CROTMP", conf);
        try {
            metaData.createStubMetaFile(conf.moduleName, sourceRoot.getParent(), false);

            String modulePath = Perl6ModuleBuilderModule.stubModule(metaData, path,
                                                conf.moduleName + "::Routes",
                                                true, false, sourceRoot.getParent(),
                                                "Empty", false);

            VirtualFile routesFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(modulePath));
            if (routesFile == null)
                return;
            routesFile.setBinaryContent(
                String.join("\n", templateContent).getBytes(StandardCharsets.UTF_8)
            );
            ApplicationManager.getApplication().invokeLater(() -> WriteAction.run(
                () -> {
                    if (metaData.isMetaDataExist()) {
                        metaData.setName(conf.moduleName);
                        addCroDependencies(metaData, conf);
                    }
                }));
        }
        catch (IOException|NullPointerException e) {
            LOG.error(e);
        }
    }

    @NotNull
    private static String populateRouteVariables(String templateContent,
                                                 boolean featureSupport,
                                                 String partsFileName,
                                                 String featurePrefix,
                                                 CroAppTemplateConfig conf) {
        String importLine = "";
        String routeLine = "";
        if (featureSupport) {
            String wsContent = String.join("\n", Perl6Utils.getResourceAsLines(CRO_RESOURCE_PREFIX + partsFileName));
            importLine = "\n" + wsContent.split("--")[0];
            routeLine = "\n" + wsContent.split("--")[1];
        }
        templateContent = templateContent
            .replace(String.format("$$%s_IMPORT$$", featurePrefix), StringUtil.trimEnd(importLine, "\n"))
            .replace(String.format("$$%s_ROUTE$$", featurePrefix), routeLine)
            .replaceAll("\\$\\$MODULE_NAME\\$\\$", conf.moduleName);
        return templateContent;
    }

    private static void addCroDependencies(Perl6MetaDataComponent metaData, CroAppTemplateConfig conf) {
        metaData.addDepends("Cro::HTTP");
        metaData.addTestDepends("Cro::HTTP::Test");
        if (conf.websocketSupport)
            metaData.addDepends("Cro::WebSocket");
        if (conf.templatingSupport)
            metaData.addDepends("Cro::WebApp");
    }

    private static void stubCroTest(Path sourcePath, CroAppTemplateConfig conf) {
        stubFileByResource(sourcePath, conf, "routes.t", null);
    }

    private static void stubCroDockerfile(Path sourcePath, CroAppTemplateConfig conf) {
        stubFileByResource(sourcePath, conf, "Dockerfile", null);
    }

    private static void stubCroServiceFile(Path sourcePath, CroAppTemplateConfig conf) {
        stubFileByResource(sourcePath, conf, "service.p6", null);
    }

    private static void stubCroYAML(Path sourcePath, CroAppTemplateConfig conf) {
        stubFileByResource(sourcePath, conf, ".cro.yml", "cro.yml");
    }

    private void stubTemplatesDirectory(Path sourcePath, CroAppTemplateConfig conf) {
        Path directoryPath = sourcePath.resolve("templates");
        try {
            Path templatesDirectoryPath = Files.createDirectory(directoryPath);
            File greetFile = Perl6Utils.getResourceAsFile(CRO_RESOURCE_PREFIX + "greet.crotmp");
            if (greetFile != null)
                Files.move(Paths.get(greetFile.getPath()), templatesDirectoryPath.resolve("greet.crotmp"));
        }
        catch (IOException e) {
            Logger.getInstance(CroModuleBuilderApplication.class).warn(e);
        }
    }

    private static void stubFileByResource(Path sourcePath, CroAppTemplateConfig conf, String targetFileName, String resourceFileName) {
        Path targetPath = sourcePath.resolve(targetFileName);
        String resourceName = resourceFileName == null ? targetFileName : resourceFileName;
        List<String> templateContent = getAndUnstubResource(CRO_RESOURCE_PREFIX + resourceName, conf);
        Perl6Utils.writeCodeToPath(targetPath, templateContent);
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
                .replaceAll("\\$\\$DOCKER_IMAGE\\$\\$", DOCKER_IMAGE)
                .replaceAll("\\$\\$MODULE_NAME\\$\\$", conf.moduleName));
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

    public static class CroAppTemplateConfig {
        public final String moduleName;
        public final boolean websocketSupport;
        public final boolean templatingSupport;

        public CroAppTemplateConfig(String moduleName, boolean websocketSupport, boolean templatingSupport) {
            this.moduleName = moduleName;
            this.websocketSupport = websocketSupport;
            this.templatingSupport = templatingSupport;
        }
    }
}
