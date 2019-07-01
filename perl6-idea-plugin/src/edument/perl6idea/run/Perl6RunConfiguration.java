package edument.perl6idea.run;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.configurations.*;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

abstract public class Perl6RunConfiguration extends LocatableConfigurationBase implements CommonProgramRunConfigurationParameters,
                                                                                 Perl6DebuggableConfiguration {
    private static final String SCRIPT_PATH = "SCRIPT_PATH";
    private static final String SCRIPT_ARGS = "SCRIPT_ARGS";
    private static final String WORKING_DIRECTORY = "WORKING_DIRECTORY";
    private static final String ENV_VARS = "ENV_VARS";
    private static final String PASS_ENVIRONMENT = "PASS_ENVIRONMENT";
    private static final String PERL6_PARAMS = "PERL6_PARAMS";
    private static final String DEBUG_PORT = "DEBUG_PORT";
    private static final String START_SUSPENDED = "START_SUSPENDED";

    private String scriptPath;
    private String scriptArgs;
    private String workingDirectory;
    private Map<String, String> envVars = new HashMap<>();
    private boolean passParentEnvs;
    private String interpreterParameters;
    private boolean myStartSuspended;
    private int myDebugPort;

    public Perl6RunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new Perl6RunSettingsEditor(getProject());
    }

    @Override
    public String suggestedName() {
        if (getScriptPath() == null)
            return null;
        VirtualFile script = LocalFileSystem.getInstance().findFileByPath(getScriptPath());
        return script == null ? null : script.getName();
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String myScriptPath) {
        this.scriptPath = myScriptPath;
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        Element scriptPathElem = element.getChild(SCRIPT_PATH);
        Element scriptArgsElem = element.getChild(SCRIPT_ARGS);
        Element workDirectoryElem = element.getChild(WORKING_DIRECTORY);
        Element envVarsElem = element.getChild(ENV_VARS);
        Element passEnvElem = element.getChild(PASS_ENVIRONMENT);
        Element perl6ParamsElem = element.getChild(PERL6_PARAMS);
        Element debugPortElem = element.getChild(DEBUG_PORT);
        Element startSuspendedElem = element.getChild(START_SUSPENDED);
        if (scriptPathElem == null    || scriptArgsElem == null ||
            workDirectoryElem == null || envVarsElem == null ||
            passEnvElem == null       || perl6ParamsElem == null ||
            debugPortElem == null     || startSuspendedElem == null) {
            throw new InvalidDataException();
        } else {
            scriptPath = scriptPathElem.getText();
            scriptArgs = scriptArgsElem.getText();
            workingDirectory = workDirectoryElem.getText();
            Map<String, String> env = new HashMap<>();
            envVarsElem.getChildren().forEach(c -> env.put(c.getName(), c.getValue()));
            envVars = env;
            passParentEnvs = Boolean.valueOf(passEnvElem.getText());
            interpreterParameters = perl6ParamsElem.getText();
            myDebugPort = Integer.valueOf(debugPortElem.getText());
            myStartSuspended = Boolean.valueOf(startSuspendedElem.getText());
        }
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        element.addContent(new Element(SCRIPT_PATH).setText(scriptPath));
        element.addContent(new Element(SCRIPT_ARGS).setText(scriptArgs));
        element.addContent(new Element(WORKING_DIRECTORY).setText(workingDirectory));
        Element envVarsElement = new Element(ENV_VARS);
        envVars.keySet().forEach(key -> envVarsElement.addContent(new Element(key).setText(envVars.get(key))));
        element.addContent(envVarsElement);
        element.addContent(new Element(PASS_ENVIRONMENT).setText(String.valueOf(passParentEnvs)));
        element.addContent(new Element(PERL6_PARAMS).setText(interpreterParameters));
        element.addContent(new Element(DEBUG_PORT).setText(String.valueOf(myDebugPort)));
        element.addContent(new Element(START_SUSPENDED).setText(String.valueOf(myStartSuspended)));
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        Path path = Paths.get(scriptPath);
        if (!path.toFile().exists()) {
            throw new RuntimeConfigurationError("Path to main script must be specified");
        }
    }

    @Override
    public void setProgramParameters(@Nullable String value) {
        scriptArgs = value;
    }

    @Nullable
    @Override
    public String getProgramParameters() {
        return scriptArgs;
    }

    @Override
    public void setWorkingDirectory(@Nullable String value) {
        workingDirectory = value;
    }

    @Nullable
    @Override
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public void setEnvs(@NotNull Map<String, String> envs) {
        envVars = envs;
    }

    @NotNull
    @Override
    public Map<String, String> getEnvs() {
        return envVars;
    }

    @Override
    public void setPassParentEnvs(boolean passParentEnvs) {
        this.passParentEnvs = passParentEnvs;
    }

    @Override
    public boolean isPassParentEnvs() {
        return passParentEnvs;
    }

    public String getInterpreterParameters() {
        return interpreterParameters;
    }

    public void setInterpreterParameters(String interpreterParameters) {
        this.interpreterParameters = interpreterParameters;
    }

    @Override
    public int getDebugPort() {
        return myDebugPort;
    }

    @Override
    public void setDebugPort(int debugPort) {
        myDebugPort = debugPort;
    }

    @Override
    public boolean isStartSuspended() {
        return myStartSuspended;
    }

    @Override
    public void setStartSuspended(boolean startSuspended) {
        myStartSuspended = startSuspended;
    }
}
