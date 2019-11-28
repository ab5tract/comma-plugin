package edument.perl6idea.vfs;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.impl.ArchiveHandler;
import edument.perl6idea.utils.Perl6CommandLine;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Perl6FileHandler extends ArchiveHandler {
    private final Pattern pathPattern = Pattern.compile("/+(-?\\d+?):(.+?)");
    private String myPath;
    private static Map <String, String> packagesCache = new HashMap<>();
    private final Logger LOG = Logger.getInstance(Perl6FileHandler.class);

    public Perl6FileHandler(@NotNull String path) {
        super(path);
        myPath = path;
    }

    @NotNull
    @Override
    protected Map<String, EntryInfo> createEntriesMap() {
        HashMap<String, EntryInfo> entries = new HashMap<>();

        Matcher matcher = pathPattern.matcher(myPath);
        Sdk sdk;
        if (matcher.matches()) {
            sdk = getSdkByMatch(matcher.group(1));
        } else {
            LOG.warn("Sdk is empty for path: [" + myPath + "], skipping");
            return entries;
        }

        try {
            List<String> providesList = executeLocateScript(sdk, matcher.group(2));

            EntryInfo root = new EntryInfo("", true, 0L, 1L, null);
            entries.put("", root);

            for (String provideItem : providesList) {
                String[] provideInfo = provideItem.split("=");
                packagesCache.put(provideInfo[0], provideInfo[1]);
                entries.put(provideInfo[0] + ".pm6", new EntryInfo(provideInfo[0] + ".pm6", false, 1L, 1L, root));
            }
        }
        catch (ExecutionException e) {
            LOG.warn(e);
            return entries;
        }

        return entries;
    }

    @NotNull
    private static List<String> executeLocateScript(Sdk sdk, String argument) throws ExecutionException {
        File locateScript = Perl6Utils.getResourceAsFile("zef/locate.p6");
        if (locateScript == null)
            throw new ExecutionException("Resource bundle is corrupted: locate script is missing");
        Perl6CommandLine cmd = new Perl6CommandLine(sdk);
        cmd.setWorkDirectory(System.getProperty("java.io.tmpdir"));
        cmd.addParameters(locateScript.getAbsolutePath());
        cmd.addParameter(argument);
        return cmd.executeAndRead();
    }

    @Nullable
    private static Sdk getSdkByMatch(String sdkHash) {
        final Sdk[] projectSdks = ProjectJdkTable.getInstance().getAllJdks();
        for (Sdk tempSdk : projectSdks) {
            int tempSdkHash = tempSdk.getName().hashCode();
            if (tempSdkHash == Integer.parseInt(sdkHash)) {
                return tempSdk;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public byte[] contentsToByteArray(@NotNull String relativePath) {
        getEntriesMap();
        try {
            String packageName = relativePath.substring(0, relativePath.length() - 4);
            if (packagesCache.containsKey(packageName))
                return Files.readAllBytes(Paths.get(packagesCache.get(packageName)));
        } catch (IOException e) {
            LOG.warn(e);
        }
        return new byte[0];
    }
}
