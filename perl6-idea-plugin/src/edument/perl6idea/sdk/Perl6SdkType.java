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
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.external.ExternalPerl6File;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.utils.Perl6CommandLine;
import edument.perl6idea.utils.Perl6Utils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

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
    public static final String SETTING_FILE_NAME = "SETTINGS.pm6";
    private static Logger LOG = Logger.getInstance(Perl6SdkType.class);
    private Map<String, String> moarBuildConfig;

    // Symbol caches
    private Map<String, JSONArray> useNameSymbolCache = new ConcurrentHashMap<>();
    private Map<String, Perl6File> useNameFileCache = new ConcurrentHashMap<>();

    private Map<String, JSONArray> needNameSymbolCache = new ConcurrentHashMap<>();
    private Map<String, Perl6File> needNameFileCache = new ConcurrentHashMap<>();

    private JSONArray settingJson;
    private Perl6File setting;
    private boolean mySettingsStarted = false;
    private Set<String> myPackageStarted = ContainerUtil.newConcurrentSet();

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
            if (!binPath.toFile().isDirectory() && Files.isExecutable(binPath)) {
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
            if (equalsPosition > 0) {
                String key = line.substring(0, equalsPosition);
                String value = line.substring(equalsPosition + 1);
                buildConfig.put(key, value);
            }
        }
        moarBuildConfig = buildConfig;
        return moarBuildConfig;
    }

    public Perl6File getCoreSettingFile(Perl6PsiElement element) {
        if (setting != null)
            return setting;
        if (settingJson != null)
            return setting = makeSettingSymbols(element.getProject(), settingJson);

        Project project = element.getProject();
        File coreSymbols = Perl6Utils.getResourceAsFile("symbols/perl6-core-symbols.p6");
        String perl6path = getSdkHomeByElement(element);

        if (perl6path == null || coreSymbols == null) {
            String errorMessage = perl6path == null
                                  ? "getCoreSettingFile is called without Perl 6 SDK set, using fallback"
                                  : "getCoreSettingFile is called with corrupted resources bundle, using fallback";
            LOG.warn(errorMessage);
            return getFallback(project);
        }

        try {
            if (!mySettingsStarted) {
                GeneralCommandLine cmd = Perl6CommandLine.pushFile(
                    Perl6CommandLine.getPerl6CommandLine(
                        System.getProperty("java.io.tmpdir"),
                        perl6path),
                    coreSymbols);
                Thread thread = new Thread(() -> {
                    mySettingsStarted = true;
                    List<String> settingLines = Perl6CommandLine.execute(cmd);
                    if (settingLines == null) {
                        LOG.warn("getCoreSettingFile got no symbols from Perl 6, using fallback");
                        getFallback(project);
                    }
                    setting = makeSettingSymbols(project, String.join("\n", settingLines));
                });
                thread.start();
            }
            return new ExternalPerl6File(project, new LightVirtualFile("DUMMY"));
        } catch (ExecutionException e) {
            LOG.error(e);
            return getFallback(project);
        }
    }

    private Perl6File getFallback(Project project) {
        File fallback = Perl6Utils.getResourceAsFile("symbols/CORE.fallback");
        if (fallback == null) {
            LOG.error("getCoreSettingFile is called with corrupted resources bundle");
            return new ExternalPerl6File(project, new LightVirtualFile(SETTING_FILE_NAME));
        }

        try {
            return setting = makeSettingSymbols(project, new String(Files.readAllBytes(fallback.toPath()), StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.error(e);
            return new ExternalPerl6File(project, new LightVirtualFile(SETTING_FILE_NAME));
        }
    }

    private Perl6File makeSettingSymbols(Project project, String json) {
        try {
            settingJson = new JSONArray(json);
            return makeSettingSymbols(project, settingJson);
        } catch (JSONException e) {
            Logger.getInstance(Perl6SdkType.class).warn(e);
        }
        return new ExternalPerl6File(project, new LightVirtualFile(SETTING_FILE_NAME));
    }

    private static Perl6File makeSettingSymbols(Project project, JSONArray settingJson) {
        if (project.isDisposed())
            return null;
        ExternalPerl6File perl6File = new ExternalPerl6File(project, new LightVirtualFile(SETTING_FILE_NAME));
        Perl6ExternalNamesParser parser = new Perl6ExternalNamesParser(project, perl6File, settingJson).parse();
        perl6File.setSymbols(parser.result());
        return perl6File;
    }

    public Perl6File getPsiFileForModule(Project project, String name, String invocation) {
        Map<String, Perl6File> cache = invocation.startsWith("use") ? useNameFileCache : needNameFileCache;
        Map<String, JSONArray> symbolCache = invocation.startsWith("use") ? useNameSymbolCache : needNameSymbolCache;
        // If we have anything in file cache, return it
        if (cache.containsKey(name))
            return cache.get(name);

        if (!myPackageStarted.contains(name)) {
            myPackageStarted.add(name);
            Thread thread = new Thread(() -> {
                // if not, check if we have symbol cache, if yes, parse, save and return it
                if (symbolCache.containsKey(name))
                    cache.compute(name, (n, v) -> constructExternalPsiFile(project, n, symbolCache.get(n)));
                // if no symbol cache, compute as usual
                cache.compute(name, (n, v) -> constructExternalPsiFile(project, n, invocation));
            });
            thread.start();
        }
        return new ExternalPerl6File(project, new LightVirtualFile("DUMMY"));
    }

    private static Perl6File constructExternalPsiFile(Project project, String name, JSONArray externalsJSON) {
        LightVirtualFile dummy = new LightVirtualFile(name + ".pm6");
        ExternalPerl6File perl6File = new ExternalPerl6File(project, dummy);
        Perl6ExternalNamesParser parser = new Perl6ExternalNamesParser(project, perl6File, externalsJSON);
        perl6File.setSymbols(parser.result());
        return perl6File;
    }

    private static Perl6File constructExternalPsiFile(Project project, String name, String invocation) {
        LightVirtualFile dummy = new LightVirtualFile(name + ".pm6");
        ExternalPerl6File perl6File = new ExternalPerl6File(project, dummy);
        List<Perl6Symbol> symbols = loadModuleSymbols(project, perl6File, invocation);
        perl6File.setSymbols(symbols);
        return perl6File;
    }

    public void invalidateCaches() {
        moarBuildConfig = null;
        invalidateFileCache();
        invalidateSymbolCache();
    }

    public void invalidateFileCache() {
        myPackageStarted = ContainerUtil.newConcurrentSet();
        mySettingsStarted = false;
        useNameFileCache = new ConcurrentHashMap<>();
        needNameFileCache = new ConcurrentHashMap<>();
        setting = null;
    }

    public void invalidateSymbolCache() {
        useNameSymbolCache = new ConcurrentHashMap<>();
        needNameSymbolCache = new ConcurrentHashMap<>();
        settingJson = new JSONArray();
    }

    private static List<Perl6Symbol> loadModuleSymbols(Project project, Perl6File perl6File, String invocation) {
        String homePath = getSdkHomeByProject(project);
        File moduleSymbols = Perl6Utils.getResourceAsFile("symbols/perl6-module-symbols.p6");
        if (homePath == null) {
            LOG.warn(new ExecutionException("SDK path is not set"));
            return new ArrayList<>();
        } else if (moduleSymbols == null) {
            LOG.warn(new ExecutionException("Necessary distribution file is missing"));
            return new ArrayList<>();
        }
        GeneralCommandLine cmd = Perl6CommandLine.getPerl6CommandLine(
            project.getBasePath(),
            homePath);
        cmd.addParameter(moduleSymbols.getPath());
        cmd.addParameter(invocation);

        List<String> symbols = Perl6CommandLine.execute(cmd);
        return symbols == null ? new ArrayList<>() : new Perl6ExternalNamesParser(project, perl6File, String.join("\n", symbols)).parse().result();
    }

    public static void contributeParentSymbolsFromCore(@NotNull Perl6SymbolCollector collector,
                                                        Perl6File coreSetting,
                                                        String parentName,
                                                       MOPSymbolsAllowed allowed) {
        Perl6SingleResolutionSymbolCollector parentCollector =
          new Perl6SingleResolutionSymbolCollector(parentName, Perl6SymbolKind.TypeOrConstant);
        coreSetting.contributeGlobals(parentCollector, new HashSet<>());
        if (parentCollector.isSatisfied()) {
            Perl6PackageDecl decl = (Perl6PackageDecl)parentCollector.getResult().getPsi();
            decl.contributeMOPSymbols(collector, allowed);
        }
    }
}
