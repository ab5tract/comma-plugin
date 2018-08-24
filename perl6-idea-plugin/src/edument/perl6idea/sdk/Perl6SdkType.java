package edument.perl6idea.sdk;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.psi.PsiElement;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.utils.Perl6CommandLine;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Perl6SdkType extends SdkType {
    private static final String NAME = "Perl 6 SDK";
    private static Logger LOG = Logger.getInstance(Perl6SdkType.class);
    private List<Perl6Symbol> setting;
    private Map<String, Perl6ExternalPackage> settingClasses;
    private Map<String, String> moarBuildConfig;
    private Map<String, List<Perl6Symbol>> useNameCache = new ConcurrentHashMap<>();
    private Map<String, List<Perl6Symbol>> needNameCache = new ConcurrentHashMap<>();

    private Perl6SdkType() {
        super(NAME);
    }

    public static Perl6SdkType getInstance() {
        return SdkType.findInstance(Perl6SdkType.class);
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Perl6Icons.CAMELIA;
    }

    @NotNull
    @Override
    public Icon getIconForAddAction() {
        return getIcon();
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        return findPerl6InPath();
    }

    @Nullable
    private static String findPerl6InPath() {
        final String command = perl6Command();
        final String path = System.getenv("PATH");
        for (String root : path.split(File.pathSeparator)) {
            final File file = new File(root, command);
            if (file.exists()) {
                return file.getParentFile().getAbsolutePath();
            }
        }
        return null;
    }

    public static String perl6Command() {
        return SystemInfo.isWindows ? "perl6.bat"  : "perl6";
    }

    @Nullable
    public static String getSdkHomeByElement(PsiElement element) {
        return getSdkHomeByModule(ModuleUtilCore.findModuleForPsiElement(element));
    }

    @Nullable
    public static String getSdkHomeByModule(Module module) {
        if (module == null)
            return null;
        Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
        return sdk != null && sdk.getSdkType() instanceof Perl6SdkType
               ? sdk.getHomePath()
               : getSdkHomeByProject(module.getProject());
    }

    @Nullable
    public static String getSdkHomeByProject(Project project) {
        if (project == null)
            return null;
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        return sdk != null && sdk.getSdkType() instanceof Perl6SdkType
               ? sdk.getHomePath()
               : null;
    }

    @Override
    public boolean isValidSdkHome(@NotNull String path) {
        return Paths.get(path, perl6Command()) != null;
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel model, @NotNull SdkModificator modificator) {
        return null;
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData data, @NotNull Element additional) {
    }

    @Nullable
    @Override
    public String getVersionString(@NotNull String path) {
        Path binPath = Paths.get(path, perl6Command());
        String[] command = {binPath.normalize().toString(), "-e", "say $*PERL.compiler.version"};
        BufferedReader std = null;
        try {
            if (!Files.isDirectory(binPath) && Files.isExecutable(binPath)) {
                Process p = Runtime.getRuntime().exec(command);
                std = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String firstLine = std.readLine();
                if (firstLine != null) {
                    std.close();
                    return firstLine;
                }
            }
        } catch (IOException e) {
            LOG.error(e);
            return null;
        } finally {
            if (std != null) {
                try {
                    std.close();
                }
                catch (IOException e) {
                    LOG.warn(e);
                }
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String suggestSdkName(@Nullable String currentSdkName, @NotNull String sdkHome) {
        String version = getVersionString(sdkHome);
        if (version == null) {
            return "Unknown at " + sdkHome;
        }
        return "Perl 6 " + version;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Perl 6 SDK";
    }

    @Nullable
    public Map<String, String> getMoarBuildConfiguration(Project project) {
        if (moarBuildConfig != null) return moarBuildConfig;
        String perl6path = getSdkHomeByProject(project);
        if (perl6path == null) {
            LOG.error("getMoarBuildConfiguration is called without Perl 6 SDK set, cannot use debug features");
            return null;
        }

        Map<String, String> buildConfig = new TreeMap<>();

        GeneralCommandLine cmd = Perl6CommandLine.getPerl6CommandLine(
                System.getProperty("java.io.tmpdir"),
                perl6path);
        cmd.addParameter("--show-config");
        List<String> subs = Perl6CommandLine.execute(cmd);
        if (subs == null) return null;
        for (String line : subs) {
            int equalsPosition = line.indexOf('=');
            String key = line.substring(0, equalsPosition);
            String value = line.substring(equalsPosition + 1);
            buildConfig.put(key, value);
        }
        moarBuildConfig = buildConfig;
        return moarBuildConfig;
    }

    public Perl6ExternalPackage getCoreSettingSymbol(String name, Perl6PsiElement element) {
        if (settingClasses == null)
            getCoreSettingSymbols(element);
        Perl6ExternalPackage externalPackage = settingClasses.get(name);
        // If no such core symbol, return empty class
        return externalPackage != null ? externalPackage : new Perl6ExternalPackage("", Perl6PackageKind.CLASS);
    }

    public List<Perl6Symbol> getCoreSettingSymbols(Perl6PsiElement element) {
        if (setting != null)
            return setting;
        File coreSymbols = Perl6CommandLine.getResourceAsFile(this,"symbols/perl6-core-symbols.p6");
        String perl6path = getSdkHomeByElement(element);
        if (perl6path == null) {
            LOG.warn("getCoreSettingSymbols is called without Perl 6 SDK set, using fallback");
            return getFallback();
        }
        if (coreSymbols == null) {
            LOG.warn("getCoreSettingSymbols is called with corrupted resources bundle, using fallback");
            return getFallback();
        }

        try {
            GeneralCommandLine cmd = Perl6CommandLine.pushFile(
                    Perl6CommandLine.getPerl6CommandLine(
                            System.getProperty("java.io.tmpdir"),
                            perl6path),
                    coreSymbols);
            List<String> subs = Perl6CommandLine.execute(cmd);
            if (subs == null) {
                LOG.warn("getCoreSettingSymbols got no symbols from Perl 6, using fallback");
                return getFallback();
            }
            setting = makeSettingSymbols(subs);
            return setting;
        } catch (ExecutionException e) {
            LOG.error(e);
            return getFallback();
        }
    }

    private List<Perl6Symbol> getFallback() {
        File fallback = Perl6CommandLine.getResourceAsFile(this,"symbols/CORE.fallback");
        if (fallback == null) LOG.error("getCoreSettingSymbols is called with corrupted resources bundle");

        try {
            if (fallback != null) {
                return setting = makeSettingSymbols(Files.readAllLines(fallback.toPath(), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            LOG.error(e);
        }
        return new ArrayList<>();
    }

    private List<Perl6Symbol> makeSettingSymbols(List<String> names) {
        Perl6ExternalNamesParser parser = new Perl6ExternalNamesParser(names);
        settingClasses = parser.getExternal();
        return parser.result();
    }

    public List<Perl6Symbol> getNamesForUse(Project project, String name) {
        List<Perl6Symbol> cached = useNameCache.get(name);
        if (cached == null) {
            cached = loadModuleSymbols(project, "use", name);
            useNameCache.put(name, cached);
        }
        return cached;
    }

    public List<Perl6Symbol> getNamesForNeed(Project project, String name) {
        List<Perl6Symbol> cached = needNameCache.get(name);
        if (cached == null) {
            cached = loadModuleSymbols(project, "need", name);
            needNameCache.put(name, cached);
        }
        return cached;
    }

    private List<Perl6Symbol> loadModuleSymbols(Project project, String directive, String name) {
        String homePath = getSdkHomeByProject(project);
        if (homePath == null) {
            LOG.warn(new ExecutionException("SDK path is not set"));
            return new ArrayList<>();
        }
        File moduleSymbols = Perl6CommandLine.getResourceAsFile(this,"symbols/perl6-module-symbols.p6");
        GeneralCommandLine cmd = Perl6CommandLine.getPerl6CommandLine(
            System.getProperty("java.io.tmpdir"),
            homePath);
        cmd.addParameter(moduleSymbols.getPath());
        cmd.addParameter(directive);
        cmd.addParameter(name);

        List<String> symbols = Perl6CommandLine.execute(cmd);
        return symbols == null ? new ArrayList<>() : new Perl6ExternalNamesParser(symbols).result();
    }
}
