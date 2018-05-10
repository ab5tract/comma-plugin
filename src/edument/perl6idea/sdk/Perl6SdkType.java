package edument.perl6idea.sdk;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.psi.PsiElement;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.psi.Perl6PsiElement;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Perl6SdkType extends SdkType {
    private static final String NAME = "Perl 6 SDK";
    private static Logger LOG = Logger.getInstance(Perl6SdkType.class);
    private List<String> setting;
    private Map<String, String> moarBuildConfig;

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
        final String command = SystemInfo.isWindows
            ? "perl6.bat"
            : "perl6";
        final String path = System.getenv("PATH");
        for (String root : path.split(File.pathSeparator)) {
            final File file = new File(root, command);
            if (file.exists()) {
                return file.getParentFile().getAbsolutePath();
            }
        }
        return null;
    }

    @Nullable
    private static String getSdkHomeByElement(PsiElement element) {
        return getSekHomeByModule(ModuleUtilCore.findModuleForPsiElement(element));
    }

    @Nullable
    private static String getSekHomeByModule(Module module) {
        if (module == null)
            return null;
        Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
        if (sdk == null || !(sdk.getSdkType() instanceof Perl6SdkType))
            sdk = ProjectRootManager.getInstance(module.getProject()).getProjectSdk();
        return sdk != null && sdk.getSdkType() instanceof Perl6SdkType
               ? sdk.getHomePath()
               : null;
    }

    @Override
    public boolean isValidSdkHome(@NotNull String path) {
        return Paths.get(path, "perl6") != null;
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
        Path binPath = Paths.get(path, "perl6");
        String[] command = {binPath.normalize().toString(), "-e", "say $*PERL.compiler.version"};
        try {
            if (!Files.isDirectory(binPath) && Files.isExecutable(binPath)) {
                Process p = Runtime.getRuntime().exec(command);
                BufferedReader std = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String firstLine = std.readLine();
                if (firstLine != null) {
                    return firstLine;
                }
            }
        } catch (IOException e) {
            LOG.error(e);
            return null;
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
    public Map<String, String> getMoarBuildConfiguration() {
        if (moarBuildConfig != null) return moarBuildConfig;
        String perl6path = findPerl6InPath();
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

    public List<String> getCoreSettingSymbols(Perl6PsiElement element) {
        if (setting != null)
            return setting;
        File coreSymbols = Perl6CommandLine.getResourceAsFile(this,"symbols/perl6-core-symbols.p6");
        String perl6path = getSdkHomeByElement(element);
        System.out.println("Got path as " + perl6path);
        if (perl6path == null) {
            LOG.error("getCoreSettingSymbols is called without Perl 6 SDK set, using fallback");
            return getFallback();
        }
        if (coreSymbols == null) {
            LOG.error("getCoreSettingSymbols is called with corrupted resources bundle, using fallback");
            return getFallback();
        }

        try {
            GeneralCommandLine cmd = Perl6CommandLine.pushFile(
                    Perl6CommandLine.getPerl6CommandLine(
                            System.getProperty("java.io.tmpdir"),
                            perl6path),
                    coreSymbols);
            List<String> subs = Perl6CommandLine.execute(cmd);
            if (subs == null) return getFallback();
            setting = subs;
            return subs;
        } catch (ExecutionException e) {
            LOG.error(e);
            return getFallback();
        }
    }

    private List<String> getFallback() {
        File fallback = Perl6CommandLine.getResourceAsFile(this,"symbols/CORE.fallback");
        if (fallback == null) LOG.error("getCoreSettingSymbols is called with corrupted resources bundle");

        try {
            if (fallback != null) {
                setting = Files.readAllLines(fallback.toPath(), StandardCharsets.UTF_8);
                return setting;
            }
        } catch (IOException e) {
            LOG.error(e);
        }
        return new ArrayList<>();
    }
}
