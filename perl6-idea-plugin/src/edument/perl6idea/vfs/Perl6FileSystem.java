package edument.perl6idea.vfs;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.testFramework.LightVirtualFile;
import edument.perl6idea.utils.Perl6CommandLine;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Perl6FileSystem extends VirtualFileSystem {
    public static final String PROTOCOL = "raku";
    private static final Logger LOG = Logger.getInstance(Perl6FileSystem.class);
    private Map<String, Map<String, VirtualFile>> cache = new ConcurrentHashMap<>();

    public static Perl6FileSystem getInstance() {
        return (Perl6FileSystem)VirtualFileManager.getInstance().getFileSystem(PROTOCOL);
    }

    @NotNull
    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    public Collection<VirtualFile> findFilesByPath(@NotNull Project project, @NotNull String path) {
        return cache.computeIfAbsent(path, (name) -> {
            try {
                Map<String, VirtualFile> packageFiles = new ConcurrentHashMap<>();
                File locateScript = Perl6Utils.getResourceAsFile("zef/locate.p6");
                if (locateScript == null) {
                    throw new ExecutionException("File locator is called with corrupted resources bundle, aborting");
                }
                Perl6CommandLine cmd = new Perl6CommandLine(project);
                cmd.setWorkDirectory(System.getProperty("java.io.tmpdir"));
                cmd.addParameters(locateScript.getPath());
                cmd.addParameters(path);
                for (String compUnit : cmd.executeAndRead()) {
                    String[] pieces = compUnit.split("=");
                    if (pieces.length == 2) {
                        String fileContents = Files.lines(Paths.get(pieces[1]), StandardCharsets.UTF_8).collect(Collectors.joining("\n"));
                        ;
                        new File(pieces[1]);
                        VirtualFile file = new LightVirtualFile(pieces[0] + ".pm6", fileContents);
                        packageFiles.put(pieces[0], file);
                    }
                }

                //return new Perl6LibraryVirtualFile(this, path, path, packageFiles.values().toArray(VirtualFile.EMPTY_ARRAY));
                return packageFiles;
            }
            catch (ExecutionException | IOException e) {
                LOG.warn(e);
            }
            return new ConcurrentHashMap<>();
        }).values();
    }

    @Nullable
    @Override
    public VirtualFile findFileByPath(@NotNull String path) {
        return null;
    }

    @Override
    public void refresh(boolean asynchronous) {}

    @Nullable
    @Override
    public VirtualFile refreshAndFindFileByPath(@NotNull String path) {
        return findFileByPath(path);
    }

    @Override
    public void addVirtualFileListener(@NotNull VirtualFileListener listener) {

    }

    @Override
    public void removeVirtualFileListener(@NotNull VirtualFileListener listener) {

    }

    @Override
    protected void deleteFile(Object requestor, @NotNull VirtualFile vFile) throws IOException {
        throw new IOException("Cannot modify");
    }

    @Override
    protected void moveFile(Object requestor, @NotNull VirtualFile vFile, @NotNull VirtualFile newParent) throws IOException {
        throw new IOException("Cannot modify");
    }

    @Override
    protected void renameFile(Object requestor, @NotNull VirtualFile vFile, @NotNull String newName) throws IOException {
        throw new IOException("Cannot modify");
    }

    @NotNull
    @Override
    protected VirtualFile createChildFile(Object requestor, @NotNull VirtualFile vDir, @NotNull String fileName) throws IOException {
        throw new IOException("Cannot modify");
    }

    @NotNull
    @Override
    protected VirtualFile createChildDirectory(Object requestor, @NotNull VirtualFile vDir, @NotNull String dirName) throws IOException {
        throw new IOException("Cannot modify");
    }

    @NotNull
    @Override
    protected VirtualFile copyFile(Object requestor,
                                   @NotNull VirtualFile virtualFile,
                                   @NotNull VirtualFile newParent,
                                   @NotNull String copyName) throws IOException {
        throw new IOException("Cannot modify");
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
