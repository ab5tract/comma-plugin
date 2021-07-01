package edument.perl6idea.vfs;

import com.intellij.openapi.util.io.FileAttributes;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem;
import com.intellij.openapi.vfs.newvfs.VfsImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Perl6FileSystem extends ArchiveFileSystem {
    public static final String PROTOCOL = "raku";

    public static Perl6FileSystem getInstance() {
        return (Perl6FileSystem)VirtualFileManager.getInstance().getFileSystem(PROTOCOL);
    }

    @NotNull
    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    @Nullable
    @Override
    public VirtualFile findFileByPath(@NotNull String path) {
        return VfsImplUtil.findFileByPath(this, path);
    }

    @Override
    public void refresh(boolean asynchronous) {
        VfsImplUtil.refresh(this, asynchronous);
    }

    @Nullable
    @Override
    public VirtualFile refreshAndFindFileByPath(@NotNull String path) {
        return VfsImplUtil.refreshAndFindFileByPath(this, path);
    }

    @Override
    public void deleteFile(Object requestor, @NotNull VirtualFile vFile) throws IOException {
        throw new IOException("Cannot modify");
    }

    @Override
    public void moveFile(Object requestor, @NotNull VirtualFile vFile, @NotNull VirtualFile newParent) throws IOException {
        throw new IOException("Cannot modify");
    }

    @Override
    public void renameFile(Object requestor, @NotNull VirtualFile vFile, @NotNull String newName) throws IOException {
        throw new IOException("Cannot modify");
    }

    @Nullable
    @Override
    public FileAttributes getAttributes(@NotNull VirtualFile file) {
        return new FileAttributes(!file.getPath().endsWith(".pm6"), false, false, false, DEFAULT_LENGTH, DEFAULT_TIMESTAMP, false);
    }

    @Override
    @NotNull
    protected String getRelativePath(VirtualFile file) {
        String path = file.getPath();
        String relativePath = path.substring(extractRootPath(path).length());
        return StringUtil.startsWithChar(relativePath, '/') ? relativePath.substring(1) : relativePath;
    }

    @NotNull
    @Override
    protected String extractRootPath(@NotNull String path) {
        return path.split("!/")[0] + "!/";
    }

    @NotNull
    @Override
    protected String extractLocalPath(@NotNull String rootPath) {
        String localPath = rootPath.split("!/")[0];
        return localPath.startsWith("/") ? localPath.substring(1) : localPath;
    }

    @NotNull
    @Override
    protected String composeRootPath(@NotNull String localPath) {
        return localPath + "!/";
    }

    @NotNull
    @Override
    protected Perl6FileHandler getHandler(@NotNull VirtualFile file) {
        return VfsImplUtil.getHandler(this, file, Perl6FileHandler::new);
    }

    @NotNull
    @Override
    public VirtualFile createChildFile(Object requestor, @NotNull VirtualFile vDir, @NotNull String fileName) throws IOException {
        throw new IOException("Cannot modify");
    }

    @NotNull
    @Override
    public VirtualFile createChildDirectory(Object requestor, @NotNull VirtualFile vDir, @NotNull String dirName) throws IOException {
        throw new IOException("Cannot modify");
    }

    @Override
    public boolean exists(@NotNull VirtualFile file) {
        return getAttributes(file) != null;
    }

    @NotNull
    @Override
    public String[] list(@NotNull VirtualFile file) {
        return getHandler(file).list(getRelativePath(file));
    }

    @Override
    public boolean isDirectory(@NotNull VirtualFile file) {
        if (file.getParent() == null) return true;
        FileAttributes attributes = getAttributes(file);
        return attributes == null || attributes.isDirectory();
    }

    @Override
    public long getTimeStamp(@NotNull VirtualFile file) {
        FileAttributes attributes = getAttributes(file);
        if (attributes != null) return attributes.lastModified;
        return -1L;
    }

    @Override
    public void setTimeStamp(@NotNull VirtualFile file, long timeStamp) throws IOException {
        throw new IOException("Can't modify file");
    }

    @Override
    public boolean isWritable(@NotNull VirtualFile file) {
        return false;
    }

    @Override
    public void setWritable(@NotNull VirtualFile file, boolean writableFlag) throws IOException {
        throw new IOException("Can't modify file");
    }

    @NotNull
    @Override
    public VirtualFile copyFile(Object requestor,
                                @NotNull VirtualFile virtualFile,
                                @NotNull VirtualFile newParent,
                                @NotNull String copyName) throws IOException {
        throw new IOException("Cannot copy file");
    }

    @NotNull
    @Override
    public byte[] contentsToByteArray(@NotNull VirtualFile file) {
        return getHandler(file).contentsToByteArray(getRelativePath(file));
    }

    @NotNull
    @Override
    public InputStream getInputStream(@NotNull VirtualFile file) throws IOException {
        return getHandler(file).getInputStream(getRelativePath(file));
    }

    @NotNull
    @Override
    public OutputStream getOutputStream(@NotNull VirtualFile file, Object requestor, long modStamp, long timeStamp)
        throws IOException {
        throw new IOException("Can't get output stream");
    }

    @Override
    public long getLength(@NotNull VirtualFile file) {
        FileAttributes attributes = getAttributes(file);
        if (attributes != null) return attributes.length;
        return 0L;
    }

    @Nullable
    @Override
    public VirtualFile findFileByPathIfCached(@NotNull String path) {
        return VfsImplUtil.findFileByPathIfCached(this, path);
    }

    @Nullable
    @Override
    public VirtualFile findLocalByRootPath(@NotNull String rootPath) {
        String localPath = extractLocalPath(rootPath);
        return new Perl6LightVirtualFile(localPath, "");
    }

    @Override
    public int getRank() {
        return LocalFileSystem.getInstance().getRank() + 1;
    }
}
