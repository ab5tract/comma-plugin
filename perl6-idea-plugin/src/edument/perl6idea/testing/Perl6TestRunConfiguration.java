package edument.perl6idea.testing;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import edument.perl6idea.run.Perl6DebuggableConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

abstract public class Perl6TestRunConfiguration extends RunConfigurationBase<RunProfileState> implements Perl6DebuggableConfiguration {
    // Kind and kind-specific fields
    private static final String TEST_KIND = "TEST_KIND";
    private RakUTestKind testKind;

    private static final String MODULE = "MODULE";
    private String moduleName;
    private static final String DIRECTORY_PATH = "DIRECTORY_PATH";
    private String directoryPath;
    private static final String PATTERN = "PATTERN";
    private String filePattern;
    private static final String FILE = "FILE";
    private String filePath;

    // Generic fields
    private static final String PARALELLISM_DEGREE = "PARALELLISM_DEGREE";
    private Integer parallelismDegree;
    private static final String ENVS = "ENVS";
    private Map<String, String> myEnvs = new HashMap<>();
    private static final String PASS_PARENT_ENV = "PASS_PARENT_ENV";
    private boolean passParentEnvs;

    public Perl6TestRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory) {
        super(project, factory, "Raku test");
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new Perl6TestSettingsEditor(getProject());
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

        // Read kind
        Element kind = element.getChild(TEST_KIND);
        testKind = kind == null ? RakUTestKind.ALL : (RakUTestKind.valueOf(kind.getText()));

        // Read specific options
        switch (testKind) {
            case ALL:
                break;
            case MODULE: {
                Element module = element.getChild(MODULE);
                moduleName = module == null ? "" : module.getText();
                break;
            }
            case DIRECTORY: {
                Element directory = element.getChild(DIRECTORY_PATH);
                directoryPath = directory == null ? "" : directory.getText();
                break;
            }
            case PATTERN: {
                Element pattern = element.getChild(PATTERN);
                filePattern = pattern == null ? "*" : pattern.getText();
                break;
            }
            case FILE: {
                Element file = element.getChild(FILE);
                filePath = file == null ? "" : file.getText();
                break;
            }
        }

        // Read generic options
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
        // Write kind
        element.addContent(new Element(TEST_KIND).setText(String.valueOf(testKind)));
        // Write kind specific options
        if (testKind == null) {
            testKind = RakUTestKind.ALL;
        }
        switch (testKind) {
            case ALL:
                break;
            case MODULE: {
                element.addContent(new Element(MODULE).setText(moduleName));
                break;
            }
            case DIRECTORY: {
                element.addContent(new Element(DIRECTORY_PATH).setText(directoryPath.toString()));
                break;
            }
            case PATTERN: {
                element.addContent(new Element(PATTERN).setText(filePattern));
                break;
            }
            case FILE: {
                element.addContent(new Element(FILE).setText(filePath.toString()));
                break;
            }
        }

        // Write generic options
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
        if (parallelismDegree == null) {
            return 1;
        }
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

    protected RakUTestKind getTestKind() {
        return testKind;
    }

    protected void setTestKind(RakUTestKind testKind) {
        this.testKind = testKind;
    }

    protected String getModuleName() {
        return moduleName;
    }

    protected void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    protected String getDirectoryPath() {
        return directoryPath;
    }

    protected void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    protected String getFilePattern() {
        return filePattern;
    }

    protected void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    protected String getFilePath() {
        return filePath;
    }

    protected void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}