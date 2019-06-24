package edument.perl6idea.cro.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.project.Project;
import edument.perl6idea.run.Perl6RunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Perl6CroRunConfigurationBase extends Perl6RunConfiguration {
    public Perl6CroRunConfigurationBase(@NotNull Project project,
                                        @NotNull ConfigurationFactory factory,
                                        String name) {
        super(project, factory, name);
    }

    protected void configureCro(Project project) {
            Yaml yaml = new Yaml();
            String path = project.getBasePath();
            if (path == null)
                return;
            String croConfPath = Paths.get(path, ".cro.yml").toString();
            try (
                BufferedInputStream yamlConfStream = new BufferedInputStream(new FileInputStream(croConfPath))
            ) {
                Map<String, Object> croConf = yaml.load(yamlConfStream);
                Object scriptPath = croConf.get("entrypoint");
                if (scriptPath instanceof String)
                    setScriptPath(Paths.get(path, (String)scriptPath).toString());

                Object endpoints = croConf.get("endpoints");
                Map<String, String> environment = new HashMap<>();
                int port = 20000;
                if (endpoints instanceof List) {
                    for (Object entrypoint : (List)endpoints) {
                        if (!(entrypoint instanceof Map))
                            continue;
                        Map<String, Object> ep = (Map<String, Object>)entrypoint;
                        Object hostEnvVar = ep.get("host-env");
                        if (hostEnvVar instanceof String)
                            environment.put((String)hostEnvVar, "localhost");
                        Object portEnvVar = ep.get("port-env");
                        if (portEnvVar instanceof String)
                            environment.put((String)portEnvVar, String.valueOf(port++));
                    }
                }
                setEnvs(environment);
                setInterpreterParameters("-Ilib");
            }
            catch (Exception ignore) {
                // Was not able to load a configuration from .cro.yml,
                // do nothing
            }
        }

}
