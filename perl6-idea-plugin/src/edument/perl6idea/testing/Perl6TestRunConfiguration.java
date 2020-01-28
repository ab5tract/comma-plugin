package edument.perl6idea.testing;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import edument.perl6idea.run.Perl6DebuggableConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

abstract public class Perl6TestRunConfiguration extends RunConfigurationBase implements Perl6DebuggableConfiguration {
    private static final String PARALELLISM_DEGREE = "PARALELLISM_DEGREE";
    private Integer parallelismDegree;
    private static final String ENVS = "ENVS";
    private Map<String, String> myEnvs = new HashMap<>();
    private static final String PASS_PARENT_ENV = "PASS_PARENT_ENV";
    private boolean passParentEnvs;

    Perl6TestRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new Perl6TestSettingsEditor();
    }

    @Override
    public int getDebugPort() {
        return 9999;
    }

    @Override
    public void setDebugPort(int debugPort) {
    }

    @Override
    public boolean isStartSuspended() {
        return false;
    }

    @Override
    public void setStartSuspended(boolean startSuspended) {
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        Element degree = element.getChild(PARALELLISM_DEGREE);
        parallelismDegree = degree == null ? 1 : Integer.valueOf(degree.getText());
        Element envs = element.getChild(ENVS);
        if (envs != null) {
            for (Element envVar : envs.getChildren()) {
                myEnvs.put(envVar.getName(), envVar.getValue());
            }
        }
        Element isPassParentEnv = element.getChild(PASS_PARENT_ENV);
        passParentEnvs = isPassParentEnv == null ? true : Boolean.valueOf(isPassParentEnv.getText());
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        element.addContent(new Element(PARALELLISM_DEGREE).setText(String.valueOf(parallelismDegree)));
        Element envs = new Element(ENVS);
        for (Map.Entry<String, String> envVar : myEnvs.entrySet()) {
            Element e = new Element(envVar.getKey());
            e.setText(envVar.getValue());
            envs.addContent(e);
        }
        element.addContent(envs);
        element.addContent(new Element(PASS_PARENT_ENV).setText(String.valueOf(passParentEnvs)));
    }

    public Integer getParallelismDegree() {
        if (parallelismDegree == null) return 1;
        return parallelismDegree;
    }
    public void setParallelismDegree(int parallelismDegree) {
        this.parallelismDegree = parallelismDegree;
    }

    protected Map<String, String> getEnvs() {
        return myEnvs;
    }

    protected void setEnvs(Map<String, String> envs) {
        myEnvs = envs;
    }

    protected boolean isPassParentEnvs() {
        return passParentEnvs;
    }

    protected void setPassParentEnvs(boolean passParentEnvs) {
        this.passParentEnvs = passParentEnvs;
    }
}