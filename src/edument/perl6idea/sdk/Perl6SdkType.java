package edument.perl6idea.sdk;

import com.intellij.openapi.projectRoots.*;
import edument.perl6idea.Perl6Icons;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Perl6SdkType extends SdkType {
    private static final String NAME = "Perl 6 SDK";

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
        // TODO: Windows/Mac support
        final String command = "perl6";
        final String path = System.getenv("PATH");
        for (String root : path.split(File.pathSeparator)) {
            final File file = new File(root, command);
            if (file.exists()) {
                return file.getParentFile().getAbsolutePath();
            }
        }
        return null;
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
            // TODO: send a proper log warning here?
            e.printStackTrace();
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
}
