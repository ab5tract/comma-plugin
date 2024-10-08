package org.raku.comma.event.handlers;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent;
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import org.raku.comma.event.RakuProjectFileChangeListener;
import org.raku.comma.filetypes.RakuModuleFileType;
import org.raku.comma.services.project.RakuMetaDataComponent;
import org.raku.comma.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RakuModuleFileChangeListener extends RakuProjectFileChangeListener {
    private final List<String> modulePaths = new ArrayList<>();
    private final Project project;

    public RakuModuleFileChangeListener(Module module) {
        project = module.getProject();

        ContentEntry @NotNull [] entries = ModuleRootManager.getInstance(module).getContentEntries();
        for (ContentEntry entry : entries) {
            SourceFolder[] folders = entry.getSourceFolders();
            for (SourceFolder folder : folders) {
                if (!folder.isTestSource()) {
                    VirtualFile file = folder.getFile();
                    if (file != null && file.getName().equals("lib")) {
                        modulePaths.add(file.toNioPath().toFile().getAbsolutePath());
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldProcess(VFileEvent event) {
        VirtualFile file = Objects.requireNonNull(event.getFile());
        return event instanceof VFileMoveEvent || event instanceof VFilePropertyChangeEvent ||
                FileTypeManager.getInstance().getFileTypeByFile(file) instanceof RakuModuleFileType ||
                file instanceof VirtualDirectoryImpl;
    }

    @Override
    public void processDirectoryCreate(VFileEvent event) {
        processDirectoryCreate(event, "lib", modulePaths);
    }

    @Override
    public void processFileDelete(VFileEvent event) {
        VirtualFile file = Objects.requireNonNull(event.getFile());
        String oldModuleName = calculateModuleName(file.getCanonicalPath());
        if (oldModuleName != null) {
            updateMetaProvides(oldModuleName, null, null);
        }
    }

    @Override
    public void processDirectoryDelete(VFileEvent event) {
        var metadata = project.getService(RakuMetaDataComponent.class);
        VirtualFile file = Objects.requireNonNull(event.getFile());
        Path path = file.toNioPath();
        for (String modulePath : modulePaths) {
            if (path.startsWith(modulePath)) {
                String prefix = calculateModulePrefix(Paths.get(modulePath), path);
                for (String name : metadata.getProvidedNames()) {
                    if (name.startsWith(prefix)) {
                        metadata.removeNamespaceFromProvides(name);
                    }
                }
            }
        }
    }

    @Override
    public void processFileChange(VFileEvent event) {
        String oldPathRaw = event instanceof VFilePropertyChangeEvent
                                    ? ((VFilePropertyChangeEvent) event).getOldPath()
                                    : ((VFileMoveEvent) event).getOldPath();
        String oldModuleName = calculateModuleName(oldPathRaw);
        VirtualFile file = Objects.requireNonNull(event.getFile());
        String newModuleName = calculateModuleName(file.getCanonicalPath());
        updateMetaProvides(oldModuleName, newModuleName, file.getExtension());
    }

    @Override
    public void processDirectoryChange(VFileEvent event) {
        if (event instanceof VFileMoveEvent) {
            processDirectoryMove((VFileMoveEvent) event);
        } else if (event instanceof VFilePropertyChangeEvent) {
            processDirectoryRename((VFilePropertyChangeEvent) event);
        }
    }

    private void processDirectoryRename(VFilePropertyChangeEvent event) {
        String oldName = (String) event.getOldValue();
        String newName = (String) event.getNewValue();
        String stringNewPath = event.getPath();
        Path eventPath = Paths.get(stringNewPath);
        Path libPath = null;

        for (String modulePath : modulePaths) {
            if (eventPath.startsWith(modulePath)) {
                libPath = Paths.get(modulePath);
                break;
            }
        }
        if (libPath == null) return;

        Path oldPath = Paths.get(stringNewPath.substring(0, stringNewPath.length() - newName.length()), oldName);

        var metadata = project.getService(RakuMetaDataComponent.class);

        String newPrefix = calculateModulePrefix(libPath, eventPath);
        String oldPrefix = calculateModulePrefix(libPath, oldPath);
        for (String name : metadata.getProvidedNames()) {
            if (name.startsWith(oldPrefix)) {
                metadata.removeNamespaceFromProvides(name);
                String ext = oldName.split("\\.")[1];
                metadata.addNamespaceToProvides(newPrefix + name.substring(oldPrefix.length()), ext);
            }
        }
    }

    private void processDirectoryMove(VFileMoveEvent event) {
        String directoryName = event.getFile().getName();
        Path oldPath = Paths.get(event.getOldParent().getPath(), directoryName);
        Path newPath = Paths.get(event.getNewParent().getPath(), directoryName);
        Path libPath = null;

        for (String modulePath : modulePaths) {
            if (oldPath.startsWith(modulePath) || newPath.startsWith(modulePath)) {
                libPath = Paths.get(modulePath);
                break;
            }
        }
        if (libPath == null) return;

        boolean isFromLib = oldPath.startsWith(libPath);
        boolean isToLib = newPath.startsWith(libPath);

        var metadata = project.getService(RakuMetaDataComponent.class);

        if (isFromLib && isToLib) {
            String oldPrefix = calculateModulePrefix(libPath, oldPath);
            String newPrefix = calculateModulePrefix(libPath, newPath);

            for (String name : metadata.getProvidedNames()) {
                if (name.startsWith(oldPrefix)) {
                    metadata.removeNamespaceFromProvides(name);
                    metadata.addNamespaceToProvides(newPrefix + name.substring(oldPrefix.length()), RakuUtils.getNameExtension(name));
                }
            }
        } else if (isToLib) {
            VirtualFile file = event.getFile();
            VfsUtilCore.visitChildrenRecursively(file, new VirtualFileVisitor<>() {
                @Override
                public boolean visitFile(@NotNull VirtualFile file) {
                    var metadata = project.getService(RakuMetaDataComponent.class);
                    if (FileTypeManager.getInstance().getFileTypeByFile(file) instanceof RakuModuleFileType) {
                        metadata.addNamespaceToProvides(calculateModuleName(file.getPath()), file.getExtension());
                    }
                    return true;
                }
            });
        } else if (isFromLib) {
            String oldPrefix = calculateModulePrefix(libPath, oldPath);
            for (String name : metadata.getProvidedNames()) {
                if (name.startsWith(oldPrefix)) {
                    metadata.removeNamespaceFromProvides(name);
                }
            }
        }
    }

    private static String calculateModulePrefix(Path base, Path eventDirectoryPath) {
        Path subPath = eventDirectoryPath.subpath(base.getNameCount(),
                                                  eventDirectoryPath.getNameCount());
        StringJoiner joiner = new StringJoiner("::");
        for (Path part : subPath) {
            joiner.add(part.toString());
        }
        return joiner + "::";
    }

    @Nullable
    private String calculateModuleName(String path) {
        for (String modulePath : modulePaths) {
            // FIXME this is still dangerous even with \ escaped
            Matcher m = Pattern.compile(String.format("%s/(.+).(rakumod|pm6)", modulePath.replaceAll("\\\\", "\\\\\\\\")))
                               .matcher(path);
            if (m.matches()) {
                return m.group(1)
                        .replaceAll("/", "::")
                        .replaceAll("\\\\", "::");
            }
        }
        return null;
    }

    private void updateMetaProvides(@Nullable String oldName, @Nullable String newName, @Nullable String ext) {
        var metadata = project.getService(RakuMetaDataComponent.class);
        if (oldName != null) {
            metadata.removeNamespaceFromProvides(oldName);
        }
        if (newName != null) {
            metadata.addNamespaceToProvides(newName, ext);
        }
    }
}
