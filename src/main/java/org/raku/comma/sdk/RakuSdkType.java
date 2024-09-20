package org.raku.comma.sdk;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.ExecutionException;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.serviceContainer.AlreadyDisposedException;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.RakuIcons;
import org.raku.comma.actions.ShowRakuProjectStructureAction;
import org.raku.comma.psi.RakuFile;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.psi.RakuPsiElement;
import org.raku.comma.psi.external.ExternalRakuFile;
import org.raku.comma.psi.symbols.*;
import org.raku.comma.psi.type.RakuResolvedType;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUnresolvedType;
import org.raku.comma.services.RakuBackupSDKService;
import org.raku.comma.utils.RakuCommandLine;
import org.raku.comma.utils.RakuUtils;
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
import java.util.concurrent.atomic.AtomicBoolean;

@InternalIgnoreDependencyViolation
public class RakuSdkType extends SdkType {
    private static final String NAME = "Raku SDK";
    public static final String SETTING_FILE_NAME = "SETTINGS.pm6";
    private static final Set<String> BINARY_NAMES = new HashSet<>();
    private boolean sdkIssueNotified = false;
    private static final Logger LOG = Logger.getInstance(RakuSdkType.class);
    private Map<String, String> moarBuildConfig;

    // Project-specific cache with PsiFile instances
    private final Map<String, ProjectSymbolCache> perProjectSymbolCache = new ConcurrentHashMap<>();
    // Global cache for all projects
    private final Map<String, String> useNameSymbolCache = new ConcurrentHashMap<>();
    private final Map<String, String> needNameSymbolCache = new ConcurrentHashMap<>();
    private String settingJson = null;
    private final AtomicBoolean mySettingsStarted = new AtomicBoolean(false);
    private final Set<String> myNeedPackagesStarted = ContainerUtil.newConcurrentSet();
    private final Set<String> myUsePackagesStarted = ContainerUtil.newConcurrentSet();

    static class ProjectSymbolCache {
        // Symbol caches
        private Map<String, RakuFile> useNameFileCache = new ConcurrentHashMap<>();
        private Map<String, RakuFile> needNameFileCache = new ConcurrentHashMap<>();
        private RakuFile setting;
        private Map<RakuSettingTypeId, RakuType> settingTypeCache;

        public void invalidateFileCache() {
            useNameFileCache = new ConcurrentHashMap<>();
            needNameFileCache = new ConcurrentHashMap<>();
            setting = null;
            settingTypeCache = null;
        }
    }

    static {
        BINARY_NAMES.add("perl6");
        BINARY_NAMES.add("perl6.bat");
        BINARY_NAMES.add("perl6.exe");
        BINARY_NAMES.add("raku");
        BINARY_NAMES.add("raku.exe");
        BINARY_NAMES.add("rakudo");
        BINARY_NAMES.add("rakudo.exe");
    }

    private RakuSdkType() {
        super(NAME);
    }

    public static RakuSdkType getInstance() {
        return SdkType.findInstance(RakuSdkType.class);
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return RakuIcons.CAMELIA;
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        // There might be different installations, such as package,
        // rakudobrew, p6env etc, so for now just return the first one
        // from PATH we can find
        return findRakuSdkDirInPath();
    }

    @Nullable
    private static String findRakuSdkDirInPath() {
        final String path = System.getenv("PATH");
        for (String root : path.split(File.pathSeparator)) {
            final String file = findRakuInSdkHome(root);
            if (file != null) return root;
        }
        return null;
    }

    @Nullable
    public static String findRakuInSdkHome(String home) {
        for (String command : BINARY_NAMES) {
            final File file = new File(home, command);
            if (file.exists() && file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    @Nullable
    public static String getSdkHomeByProject(@NotNull Project project) {
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        return sdk != null && sdk.getSdkType() instanceof RakuSdkType
                   ? sdk.getHomePath()
                   : secondarySDKHome(project);
    }

    public static String secondarySDKHome(@NotNull Project project) {
        RakuBackupSDKService service = project.getService(RakuBackupSDKService.class);
        return service.getProjectSdkPath(project.getProjectFilePath());
    }

    @Override
    public boolean isValidSdkHome(@NotNull String path) {
        for (String exe : BINARY_NAMES) {
            File file = Paths.get(path, exe).toFile();
            if (file.exists() && file.isFile() && file.canExecute()) return true;
        }
        return false;
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
        String binPath = findRakuInSdkHome(path);
        if (binPath == null) return null;
        String[] command = {binPath, "-e", "say $*PERL.compiler.version"};
        String line = null;
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        try {
            Process process = processBuilder.start();
            try (InputStreamReader in = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8);
                 BufferedReader processOutputReader = new BufferedReader(in))
            {
                line = processOutputReader.readLine();
                if (process.waitFor() != 0) return null;
            }
        } catch (IOException | InterruptedException e) {
            reactToSDKIssue(null);
        }
        return line;
    }

    @NotNull
    @Override
    public String suggestSdkName(@Nullable String currentSdkName, @NotNull String sdkHome) {
        String version = getVersionString(sdkHome);
        if (version == null) {
            return "Unknown at " + sdkHome;
        }
        return "Raku " + version;
    }

    public static String suggestSdkName(@NotNull String sdkHome) {
        String binPath = findRakuInSdkHome(sdkHome);
        if (binPath == null) return null;
        String[] command = {binPath, "-e", "say $*RAKU.compiler.version"};
        String version = null;
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        try {
            Process process = processBuilder.start();
            try (InputStreamReader in = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8);
                 BufferedReader processOutputReader = new BufferedReader(in))
            {
                version = processOutputReader.readLine();
                if (process.waitFor() != 0) return null;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (version == null) {
            throw new RuntimeException("Unknown Raku SDK at " + sdkHome);
        }

        return "Raku " + version;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Raku SDK";
    }

    @Nullable
    public Map<String, String> getMoarBuildConfiguration(Project project) {
        if (moarBuildConfig != null)
            return moarBuildConfig;
        List<String> subs;

        try {
            RakuCommandLine cmd = new RakuCommandLine(project);
            cmd.setWorkDirectory(System.getProperty("java.io.tmpdir"));
            cmd.addParameter("--show-config");
            subs = cmd.executeAndRead(null);
            Map<String, String> buildConfig = new TreeMap<>();

            for (String line : subs) {
                int equalsPosition = line.indexOf('=');
                if (equalsPosition > 0) {
                    String key = line.substring(0, equalsPosition);
                    String value = line.substring(equalsPosition + 1);
                    buildConfig.put(key, value);
                }
            }
            moarBuildConfig = buildConfig;
        } catch (ExecutionException e) {
            reactToSDKIssue(project);
            return null;
        }

        return moarBuildConfig;
    }

    public RakuFile getCoreSettingFile(Project project) {
        ProjectSymbolCache cache = perProjectSymbolCache.computeIfAbsent(project.getName(), (key) -> new ProjectSymbolCache());
        if (cache.setting != null) {
            return cache.setting;
        }
        if (settingJson != null) {
            cache.setting = makeSettingSymbols(project, settingJson);
            return cache.setting;
        }

        File coreSymbols = RakuUtils.getResourceAsFile("symbols/raku-core-symbols.raku");
        File coreDocs = RakuUtils.getResourceAsFile("docs/core.json");
        String rakuPath = getSdkHomeByProject(project);

        if (rakuPath == null || coreSymbols == null || coreDocs == null) {
            String errorMessage = rakuPath == null
                                              ? "getCoreSettingFile is called without Raku SDK set, using fallback"
                                              : coreSymbols == null
                                                            ? "getCoreSettingFile is called with corrupted resources bundle, using fallback"
                                                            : "getCoreSettingFile is called with corrupted resources bundle";
            reactToSDKIssue(project, errorMessage);
            return getFallback(project);
        }

        try {
            if (mySettingsStarted.compareAndSet(false, true)) {
                RakuCommandLine cmd = new RakuCommandLine(project);
                cmd.setWorkDirectory(System.getProperty("java.io.tmpdir"));
                cmd.addParameter(coreSymbols.getAbsolutePath());
                cmd.addParameter(coreDocs.getAbsolutePath());
                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    try {
                        String settingLines = String.join("\n", cmd.executeAndRead(coreSymbols));
                        try {
                            coreSymbols.delete();
                            coreDocs.delete();
                        } catch (Exception ignored) {}

                        if (settingLines.isEmpty()) {
                            reactToSDKIssue(project, "getCoreSettingFile got no symbols from Raku, using fallback");
                            getFallback(project);
                        } else {
                            cache.setting = makeSettingSymbols(project, settingLines);
                        }
                        triggerCodeAnalysis(project);
                    } catch (AssertionError e) {
                        // If the project was already disposed, do not die in a background thread
                    }
                });
            } else {
                try {
                    coreSymbols.delete();
                    coreDocs.delete();
                } catch (Exception ignored) {
                }
            }
            return new ExternalRakuFile(project, new LightVirtualFile("DUMMY"));
        } catch (ExecutionException e) {
            reactToSDKIssue(project);
            return getFallback(project);
        }
    }

    public RakuType getCoreSettingType(Project project, RakuSettingTypeId type) {
        // Ensure we have the setting type cache.
        ProjectSymbolCache cache = perProjectSymbolCache.computeIfAbsent(project.getName(), (key) -> new ProjectSymbolCache());
        if (cache.settingTypeCache == null) {
            RakuFile setting = cache.setting;
            if (setting != null) {
                Map<RakuSettingTypeId, RakuType> typeCache = new HashMap<>();
                for (RakuSettingTypeId typeId : RakuSettingTypeId.values()) {
                    String typeName = unmangleTypeId(typeId.name());
                    RakuSymbol symbol = setting.resolveLexicalSymbol(RakuSymbolKind.TypeOrConstant, typeName);
                    if (symbol != null) {
                        PsiElement resolution = symbol.getPsi();
                        if (resolution instanceof RakuPsiElement) {
                            typeCache.put(typeId, new RakuResolvedType(typeName, (RakuPsiElement) resolution));
                        }
                    }
                }
                cache.settingTypeCache = typeCache;
            }
        }

        // Try to resolve.
        Map<RakuSettingTypeId, RakuType> typeCache = cache.settingTypeCache;
        if (typeCache != null) {
            RakuType result = typeCache.get(type);
            if (result != null) return result;
        }

        // Return an unresolved type if all else fails.
        return new RakuUnresolvedType(unmangleTypeId(type.name()));
    }

    private static String unmangleTypeId(String id) {
        return id.replace("__", "::");
    }

    private synchronized void reactToSDKIssue(@Nullable Project project) {
        reactToSDKIssue(project, "Cannot use currently set SDK to obtain necessary symbols");
    }

    public synchronized void reactToSDKIssue(@Nullable Project project, String message) {
        if (!sdkIssueNotified) {
            sdkIssueNotified = true;
            Notification notification = NotificationGroupManager.getInstance()
                                                                .getNotificationGroup("raku.sdk.errors.group")
                                                                .createNotification(message, NotificationType.WARNING);
            notification.addAction(new AnAction("Configure Raku SDK") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    notification.expire();
                    new ShowRakuProjectStructureAction().actionPerformed(e);
                }
            });
            Notifications.Bus.notify(notification, project);
        }
    }

    private static void triggerCodeAnalysis(Project project) {
        ApplicationManager.getApplication().runReadAction(() -> {
            if (project.isDisposed()) return;
            FileEditor[] editors = FileEditorManager.getInstance(project).getSelectedEditors();
            for (FileEditor editor : editors) {
                if (editor != null && editor.getFile() != null) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(editor.getFile());
                    if (psiFile != null) {
                        DaemonCodeAnalyzer.getInstance(project).restart(psiFile);
                    }
                }
            }
        });
    }

    private RakuFile getFallback(Project project) {
        File fallback = RakuUtils.getResourceAsFile("symbols/CORE.fallback");
        if (fallback == null) {
            reactToSDKIssue(project,
                            "getCoreSettingFile is called with corrupted resources bundle, try to set a proper SDK for this project");
            return new ExternalRakuFile(project, new LightVirtualFile(SETTING_FILE_NAME));
        }

        try {
            ProjectSymbolCache cache = perProjectSymbolCache.computeIfAbsent(project.getName(), (key) -> new ProjectSymbolCache());
            return cache.setting = makeSettingSymbols(project, Files.readString(fallback.toPath()));
        } catch (IOException e) {
            reactToSDKIssue(project);
            return new ExternalRakuFile(project, new LightVirtualFile(SETTING_FILE_NAME));
        }
    }

    private RakuFile makeSettingSymbols(Project project, String json) {
        try {
            settingJson = json;
            return makeSettingSymbols(project, new JSONArray(json));
        } catch (JSONException e) {
            reactToSDKIssue(project);
        }
        return new ExternalRakuFile(project, new LightVirtualFile(SETTING_FILE_NAME));
    }

    private static RakuFile makeSettingSymbols(Project project, JSONArray settingJson) {
        if (project.isDisposed())
            return null;
        ExternalRakuFile perl6File = new ExternalRakuFile(project, new LightVirtualFile(SETTING_FILE_NAME));
        RakuExternalNamesParser parser = new RakuExternalNamesParser(project, perl6File, settingJson).parse();
        perl6File.setSymbols(parser.result());
        return perl6File;
    }

    public RakuFile getPsiFileForModule(Project project, String name, String invocation) {
        ProjectSymbolCache cache = perProjectSymbolCache.computeIfAbsent(project.getName(), (key) -> new ProjectSymbolCache());
        Map<String, RakuFile> fileCache = invocation.startsWith("use")
                                          ? cache.useNameFileCache
                                          : cache.needNameFileCache;
        // If we have anything in file cache, return it
        if (fileCache.containsKey(name))
            return fileCache.get(name);

        // if not, check if we have symbol cache, if yes, parse, save and return it
        Map<String, String> symbolCache = invocation.startsWith("use")
                                          ? useNameSymbolCache
                                          : needNameSymbolCache;
        Set<String> packagesStarted = invocation.startsWith("use")
                                      ? myUsePackagesStarted
                                      : myNeedPackagesStarted;
        if (symbolCache.containsKey(name)) {
            return fileCache.compute(name, (n, v) -> constructExternalPsiFile(project, n, new JSONArray(symbolCache.get(n))));
        }


        if (!packagesStarted.contains(name)) {
            packagesStarted.add(name);
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    // if no symbol cache, compute as usual
                    fileCache.compute(name, (n, v) -> constructExternalPsiFile(project, n, invocation, symbolCache));
                    triggerCodeAnalysis(project);
                } catch (AssertionError e) {
                    // If the project was already disposed, do not die in a background thread
                }
            });
        }
        return new ExternalRakuFile(project, new LightVirtualFile("DUMMY"));
    }

    private static RakuFile constructExternalPsiFile(Project project, String name, JSONArray externalsJSON) {
        ExternalRakuFile perl6File = null;
        try {
            LightVirtualFile dummy = new LightVirtualFile(name + ".pm6");
            perl6File = new ExternalRakuFile(project, dummy);
            RakuExternalNamesParser parser = new RakuExternalNamesParser(project, perl6File, externalsJSON);
            perl6File.setSymbols(parser.parse().result());
        } catch (AlreadyDisposedException ignored) {
        }
        return perl6File;
    }

    private static RakuFile constructExternalPsiFile(Project project,
                                                     String name,
                                                     String invocation,
                                                     Map<String, String> symbolCache)
    {
        LightVirtualFile dummy = new LightVirtualFile(name + ".pm6");
        ExternalRakuFile perl6File = new ExternalRakuFile(project, dummy);
        List<RakuSymbol> symbols = loadModuleSymbols(project, perl6File, name, invocation, symbolCache, false);
        perl6File.setSymbols(symbols);
        return perl6File;
    }

    public void invalidateCaches(Project project) {
        if (perProjectSymbolCache.containsKey(project.getName())) {
            perProjectSymbolCache.get(project.getName()).invalidateFileCache();
            perProjectSymbolCache.remove(project.getName());
        }
    }

    public void invalidateFileCaches(Project project) {
        if (perProjectSymbolCache.containsKey(project.getName())) {
            perProjectSymbolCache.get(project.getName()).invalidateFileCache();
        }
    }

    public static List<RakuSymbol> loadModuleSymbols(Project project,
                                                     RakuFile perl6File,
                                                     String name, String invocation,
                                                     Map<String, String> symbolCache,
                                                     boolean addLib)
    {
        if (invocation.equals("use nqp")) {
            return getNQPSymbols(project, perl6File);
        }

        String homePath = getSdkHomeByProject(project);
        File moduleSymbols = RakuUtils.getResourceAsFile("symbols/raku-module-symbols.raku");
        if (homePath == null) {
            LOG.info(new ExecutionException("SDK path is not set"));
            return new ArrayList<>();
        } else if (moduleSymbols == null) {
            LOG.info(new ExecutionException("Necessary distribution file is missing"));
            return new ArrayList<>();
        }
        try {
            RakuCommandLine cmd = new RakuCommandLine(project);
            cmd.setWorkDirectory(project.getBasePath());
            if (addLib) {
                cmd.addParameter("-I.");
            }
            cmd.addParameters(moduleSymbols.getPath(), invocation);
            String text = String.join("\n", cmd.executeAndRead(moduleSymbols));
            JSONArray symbols;
            try {
                symbols = new JSONArray(text);
                symbolCache.put(name, text);
            } catch (JSONException ex) {
                return new ArrayList<>();
            }
            return new RakuExternalNamesParser(project, perl6File, symbols).parse().result();
        } catch (ExecutionException e) {
            return new ArrayList<>();
        }
    }

    private static List<RakuSymbol> getNQPSymbols(Project project, RakuFile perl6File) {
        List<String> ops = new ArrayList<>();
        File nqpSymbols = RakuUtils.getResourceAsFile("symbols/nqp.ops");
        if (nqpSymbols == null) {
            ops.add("[]");
        } else {
            Path nqpSymbolsPath = nqpSymbols.toPath();
            try {
                ops = Files.readAllLines(nqpSymbolsPath);
                nqpSymbols.delete();
            } catch (IOException e) {
                ops.add("[]");
            }
        }

        return new RakuExternalNamesParser(project, perl6File, String.join("\n", ops)).parse().result();
    }

    public static void contributeParentSymbolsFromCore(@NotNull RakuSymbolCollector collector,
                                                       RakuFile coreSetting,
                                                       String parentName,
                                                       MOPSymbolsAllowed allowed)
    {
        RakuSingleResolutionSymbolCollector parentCollector = new RakuSingleResolutionSymbolCollector(parentName,
                                                                                                      RakuSymbolKind.TypeOrConstant);
        coreSetting.contributeGlobals(parentCollector, new HashSet<>());
        if (parentCollector.isSatisfied()) {
            RakuPackageDecl decl = (RakuPackageDecl) parentCollector.getResult().getPsi();
            decl.contributeMOPSymbols(collector, allowed);
        }
    }
}
