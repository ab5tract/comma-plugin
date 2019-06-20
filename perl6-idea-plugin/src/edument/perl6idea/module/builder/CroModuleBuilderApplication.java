package edument.perl6idea.module.builder;

import com.intellij.ide.util.projectWizard.ModuleNameLocationSettings;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.module.Perl6ModuleWizardStep;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class CroModuleBuilderApplication implements Perl6ModuleBuilderGeneric {
    private String myModuleName;
    private boolean myWebsocketSupport;
    private boolean myTemplatingSUpport;

    @Override
    public void setupRootModelOfPath(@NotNull ModifiableRootModel model, Path path) {
        Perl6MetaDataComponent metaData = model.getModule().getComponent(Perl6MetaDataComponent.class);
        VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());

        Path directoryName = path.getFileName();
        if (Objects.equals(directoryName.toString(), "lib")) {
            stubRoutes();
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

    private void stubRoutes() {
        // TODO
    }

    private void stubCroDockerfile(Path sourcePath) {
        Path dockerFilePath = sourcePath.resolve("Dockerfile");
        InputStream dockerFileTemplateStream = getClass().getClassLoader().getResourceAsStream("templates/CroDockerfile");
        BufferedReader reader = new BufferedReader(new InputStreamReader(dockerFileTemplateStream, StandardCharsets.UTF_8));
        List<String> lines = new ArrayList<>();

        try {
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
            dockerFileTemplateStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Perl6Utils.writeCodeToPath(dockerFilePath, lines);
    }

    private void stubCroServiceFile(Path sourcePath, String moduleName) {
        String HOST_VARIABLE = convertToEnvName(moduleName) + "_HOST";
        String PORT_VARIABLE = convertToEnvName(moduleName) + "_PORT";
        Path croServiceFilePath = sourcePath.resolve( "service.p6");
        InputStream serviceTemplateStream = getClass().getClassLoader().getResourceAsStream("templates/service.p6.template");
        BufferedReader reader = new BufferedReader(new InputStreamReader(serviceTemplateStream, StandardCharsets.UTF_8));
        List<String> lines = new ArrayList<>();

        try {
            while (reader.ready()) {
                String line = reader.readLine();
                line = line.replaceAll("\\$\\$HOST_VARIABLE\\$\\$", HOST_VARIABLE);
                line = line.replaceAll("\\$\\$PORT_VARIABLE\\$\\$", PORT_VARIABLE);
                lines.add(line);
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Perl6Utils.writeCodeToPath(croServiceFilePath, lines);
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
