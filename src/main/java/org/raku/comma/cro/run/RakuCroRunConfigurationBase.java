package org.raku.comma.cro.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.raku.comma.run.CroRunSettingsEditor;
import org.raku.comma.run.RakuRunConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RakuCroRunConfigurationBase extends RakuRunConfiguration {
    private static final String CRO_DEV_MODE = "CRO_DEV_MODE";

    private boolean croDevMode;

    public RakuCroRunConfigurationBase(@NotNull Project project,
                                       @NotNull ConfigurationFactory factory,
                                       String name)
    {
        super(project, factory, name);
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new CroRunSettingsEditor(getProject());
    }

    public boolean getCroDevMode() {
        return croDevMode;
    }

    public void setCroDevMode(boolean croDevMode) {
        this.croDevMode = croDevMode;
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        Element croDevModeNode = element.getChild(CRO_DEV_MODE);
        if (croDevModeNode == null) {
            croDevMode = true;
        } else {
            croDevMode = Boolean.valueOf(croDevModeNode.getText());
        }
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);
        element.addContent(new Element(CRO_DEV_MODE).setText(String.valueOf(croDevMode)));
    }

    protected void configureCro(Project project) {
        // always set to true when created
        croDevMode = true;
        // initialize most of the variables from .cro.yml if present
        Yaml yaml = new Yaml();
        String path = project.getBasePath();
        if (path == null) {
            return;
        }
        String croConfPath = Paths.get(path, ".cro.yml").toString();
        try (
            BufferedInputStream yamlConfStream = new BufferedInputStream(new FileInputStream(croConfPath))
        ) {
            Map<String, Object> croConf = yaml.load(yamlConfStream);
            Object scriptPath = croConf.get("entrypoint");
            if (scriptPath instanceof String) {
                setScriptPath(Paths.get(path, (String)scriptPath).toString());
            }

            Object endpoints = croConf.get("endpoints");
            Map<String, String> environment = new HashMap<>();
            int port = 20000;
            if (endpoints instanceof List) {
                for (Object entrypoint : (List<?>)endpoints) {
                    if (!(entrypoint instanceof Map)) {
                        continue;
                    }
                    Map<String, Object> ep = (Map<String, Object>)entrypoint;
                    Object hostEnvVar = ep.get("host-env");
                    if (hostEnvVar instanceof String) {
                        environment.put((String)hostEnvVar, "localhost");
                    }
                    Object portEnvVar = ep.get("port-env");
                    if (portEnvVar instanceof String) {
                        environment.put((String)portEnvVar, String.valueOf(port++));
                    }
                }
            }
            setEnvs(environment);
            setInterpreterParameters("-I.");
        }
        catch (Exception ignore) {
            // Was not able to load a configuration from .cro.yml,
            // do nothing
        }
    }
}
