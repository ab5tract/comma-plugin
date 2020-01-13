package edument.perl6idea.event;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent;
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent;
import com.intellij.util.messages.MessageBusConnection;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuleMetaChangeListener implements ModuleComponent, BulkFileListener {
    private final MessageBusConnection conn;
    private final Perl6MetaDataComponent myMetaData;
    private final Module myModule;

    public ModuleMetaChangeListener(Module module) {
        conn = ApplicationManager.getApplication().getMessageBus().connect();
        myMetaData = module.getComponent(Perl6MetaDataComponent.class);
        myModule = module;
    }

    @Nullable
    private static String calculateModuleName(String path) {
        String regexPattern = String.format(".*?/lib/(.+).%s", Perl6ModuleFileType.INSTANCE.getDefaultExtension());
        Matcher m = Pattern.compile(regexPattern).matcher(path);
        if (m.matches()) {
            return m.group(1)
                    .replaceAll("/", "::")
                    .replaceAll("\\\\", "::");
        }
        return null;
    }

    @Override
    public void before(@NotNull List<? extends VFileEvent> list) {}

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        for (VFileEvent event : events) {
            VirtualFile file = event.getFile();
            if (file == null || !(FileTypeManager.getInstance().getFileTypeByFile(file) instanceof Perl6ModuleFileType))
                continue;

            if (event instanceof VFileDeleteEvent) {
                if (file.isDirectory())
                    processDirectoryDelete(event);
                else
                    processFileDelete(event);
            }
            else if (event instanceof VFilePropertyChangeEvent &&
                     Objects.equals(((VFilePropertyChangeEvent)event).getPropertyName(), VirtualFile.PROP_NAME) ||
                     event instanceof VFileMoveEvent) {
                if (file.isDirectory())
                    processDirectoryMoveAndRename(event);
                else
                    processFileMoveAndRename(event);
            } else {
                // If it another type of event, we don't want to update LocalFileSystem
                continue;
            }

            ApplicationManager.getApplication().invokeLater(
                () -> {
                    ProjectView.getInstance(myModule.getProject()).refresh();
                    LocalFileSystem.getInstance().refresh(false);
                });
        }
    }

    private void processDirectoryMoveAndRename(VFileEvent event) {
        Path libPath = getRootPath();
        if (libPath == null) return;

        if (event instanceof VFileMoveEvent) {
            processDirectoryMove((VFileMoveEvent)event, libPath);
        } else if (event instanceof VFilePropertyChangeEvent) {
            processDirectoryRename((VFilePropertyChangeEvent)event, libPath);
        }
    }

    private void processDirectoryRename(VFilePropertyChangeEvent event, Path libPath) {
        String oldName = (String)event.getOldValue();
        String newName = (String)event.getNewValue();
        String stringNewPath = event.getPath();
        Path eventPath = Paths.get(stringNewPath);
        if (!eventPath.startsWith(libPath)) return;
        Path oldPath = Paths.get(stringNewPath.substring(0, stringNewPath.length() - newName.length()), oldName);

        String newPrefix = calculateModulePrefix(libPath, eventPath);
        String oldPrefix = calculateModulePrefix(libPath, oldPath);
        for (String name : myMetaData.getProvidedNames()) {
            if (name.startsWith(oldPrefix)) {
                myMetaData.removeNamespaceToProvides(name);
                myMetaData.addNamespaceToProvides(newPrefix + name.substring(oldPrefix.length()));
            }
        }
    }

    private void processDirectoryMove(VFileMoveEvent event, Path libPath) {
        String directoryName = event.getFile().getName();
        Path oldPath = Paths.get(event.getOldParent().getPath(), directoryName);
        Path newPath = Paths.get(event.getNewParent().getPath(), directoryName);
        boolean isFromLib  = oldPath.startsWith(libPath);
        boolean isToLib    = newPath.startsWith(libPath);

        if (isFromLib && isToLib) {
            String oldPrefix = calculateModulePrefix(libPath, oldPath);
            String newPrefix = calculateModulePrefix(libPath, newPath);

            for (String name : myMetaData.getProvidedNames()) {
                if (name.startsWith(oldPrefix)) {
                    myMetaData.removeNamespaceToProvides(name);
                    myMetaData.addNamespaceToProvides(newPrefix + name.substring(oldPrefix.length()));
                }
            }
        } else if (isToLib) {
            VirtualFile file = event.getFile();
            VfsUtilCore.visitChildrenRecursively(file, new VirtualFileVisitor<Object>() {
                @Override
                public boolean visitFile(@NotNull VirtualFile file) {
                    if (FileTypeManager.getInstance().getFileTypeByFile(file) instanceof Perl6ModuleFileType) {
                        myMetaData.addNamespaceToProvides(calculateModuleName(file.getPath()));
                    }
                    return true;
                }
            });
        } else if (isFromLib) {
            String oldPrefix = calculateModulePrefix(libPath, oldPath);
            for (String name : myMetaData.getProvidedNames())
                if (name.startsWith(oldPrefix))
                    myMetaData.removeNamespaceToProvides(name);
        }
    }

    private void processDirectoryDelete(VFileEvent event) {
        Path path = Paths.get(event.getPath());
        Path libPath = getRootPath();
        if (libPath == null) return;
        if (!path.startsWith(libPath)) return;
        String prefix = calculateModulePrefix(libPath, path);
        for (String name : myMetaData.getProvidedNames()) {
            if (name.startsWith(prefix)) {
                myMetaData.removeNamespaceToProvides(name);
            }
        }
    }

    private static String calculateModulePrefix(Path base, Path eventDirectoryPath) {
        Path subpath = eventDirectoryPath.subpath(
            base.getNameCount(),
            eventDirectoryPath.getNameCount());
        StringJoiner joiner = new StringJoiner("::");
        for (Path part : subpath) {
            joiner.add(part.toString());
        }
        return joiner.toString() + "::";
    }

    private Path getRootPath() {
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(myModule).getSourceRoots();
        for (VirtualFile root : sourceRoots) {
            if (root.getName().equals("lib")) return Paths.get(root.getPath());
        }
        return null;
    }

    private void processFileMoveAndRename(VFileEvent event) {
        String oldPathRaw = event instanceof VFilePropertyChangeEvent ?
                            ((VFilePropertyChangeEvent)event).getOldPath() :
                            ((VFileMoveEvent)event).getOldPath();
        String oldName = calculateModuleName(oldPathRaw);
        String newName = calculateModuleName(event.getPath());
        if (oldName != null)
            updateMeta(oldName, newName);
    }

    private void processFileDelete(VFileEvent event) {
        String oldName = calculateModuleName(event.getPath());
        if (oldName != null)
            updateMeta(oldName, null);
    }

    private void updateMeta(String oldName, String newName) {
        myMetaData.removeNamespaceToProvides(oldName);
        if (newName != null) {
            myMetaData.addNamespaceToProvides(newName);
        }
    }

    @Override
    public void initComponent() {
        conn.subscribe(VirtualFileManager.VFS_CHANGES, this);
    }

    @Override
    public void disposeComponent() {
        conn.disconnect();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Raku Module Change Listener";
    }
}
