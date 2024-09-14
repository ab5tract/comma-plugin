package org.raku.comma.event.handlers;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent;
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent;
import org.raku.comma.event.RakuProjectFileChangeListener;
import org.raku.comma.metadata.RakuMetaDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RakuResourceFileChangeListener extends RakuProjectFileChangeListener {
    private final List<String> resourcePaths = new ArrayList<>();
    private final RakuMetaDataComponent myMetaData;

    public RakuResourceFileChangeListener(Module module, RakuMetaDataComponent data) {
        myMetaData = data;
        VirtualFile metaVFile = data.getMetaFile();
        if (metaVFile != null) {
            File resourcesFile = metaVFile.toNioPath().resolveSibling("resources").toFile();
            if (resourcesFile.exists())
                resourcePaths.add(resourcesFile.getAbsolutePath());
        }
    }

    @Override
    public boolean shouldProcess(VFileEvent event) {
        VirtualFile file = Objects.requireNonNull(event.getFile());
        if (file.isDirectory() && file.getName().equals("resources") ||
            event instanceof VFileMoveEvent || event instanceof VFilePropertyChangeEvent)
            return true;
        for (String resourcePath : resourcePaths) {
            if (event.getPath().startsWith(resourcePath))
                return true;
        }
        return false;
    }

    @Override
    public void processFileCreate(VFileEvent event) {
        VirtualFile file = event.getFile();
        if (file == null)
            return;
        String newResource = calculateResourceName(file.getCanonicalPath());
        if (newResource != null) {
            updateMetaResources(null, newResource);
        }
    }

    @Override
    public void processDirectoryCreate(VFileEvent event) {
        processDirectoryCreate(event, "resources", resourcePaths);
    }

    @Override
    public void processFileDelete(VFileEvent event) {
        VirtualFile file = Objects.requireNonNull(event.getFile());
        String oldResource = calculateResourceName(file.getCanonicalPath());
        if (oldResource != null)
            updateMetaResources(oldResource, null);
    }

    @Override
    public void processDirectoryDelete(VFileEvent event) {
        VirtualFile file = Objects.requireNonNull(event.getFile());
        // If it's the resources directory itself, purify everything!
        if (file.getName().equals("resources") && resourcePaths.contains(file.getPath())) {
            for (String resource : myMetaData.getResources()) {
                myMetaData.removeResource(resource);
            }
            return;
        }
        // Otherwise the normal procedure, remove everything
        // residing in this directory
        Path path = file.toNioPath();
        for (String resourcePath : resourcePaths) {
            if (path.startsWith(resourcePath)) {
                String prefix = calculateResourcePrefix(Paths.get(resourcePath), path);
                for (String name : myMetaData.getResources()) {
                    if (name.startsWith(prefix))
                        myMetaData.removeResource(name);
                }
            }
        }
    }

    @Override
    public void processFileChange(VFileEvent event) {
        String oldPathRaw = event instanceof VFilePropertyChangeEvent ?
                            ((VFilePropertyChangeEvent)event).getOldPath() :
                            ((VFileMoveEvent)event).getOldPath();
        String newPathRaw = event instanceof VFilePropertyChangeEvent ?
                            ((VFilePropertyChangeEvent)event).getNewPath() :
                            ((VFileMoveEvent)event).getNewPath();
        String oldResourceName = calculateResourceName(oldPathRaw);
        String newResourceName = calculateResourceName(newPathRaw);
        updateMetaResources(oldResourceName, newResourceName);
    }

    @Override
    public void processDirectoryChange(VFileEvent event) {
        if (event instanceof VFileMoveEvent) {
            processDirectoryMove((VFileMoveEvent)event);
        }
        else if (event instanceof VFilePropertyChangeEvent) {
            processDirectoryRename((VFilePropertyChangeEvent)event);
        }
    }

    private void processDirectoryRename(VFilePropertyChangeEvent event) {
        String oldName = (String)event.getOldValue();
        String newName = (String)event.getNewValue();
        String stringNewPath = event.getPath();
        Path eventPath = Paths.get(stringNewPath);
        Path resourcesPath = null;

        for (String resourcePath : resourcePaths) {
            if (eventPath.startsWith(resourcePath)) {
                resourcesPath = Paths.get(resourcePath);
                break;
            }
        }
        if (resourcesPath == null) return;

        Path oldPath = Paths.get(stringNewPath.substring(0, stringNewPath.length() - newName.length()), oldName);

        String newPrefix = calculateResourcePrefix(resourcesPath, eventPath);
        String oldPrefix = calculateResourcePrefix(resourcesPath, oldPath);
        for (String name : myMetaData.getResources()) {
            if (name.startsWith(oldPrefix)) {
                myMetaData.removeResource(name);
                myMetaData.addResource(newPrefix + name.substring(oldPrefix.length()));
            }
        }
    }

    private void processDirectoryMove(VFileMoveEvent event) {
        String directoryName = event.getFile().getName();
        Path oldPath = Paths.get(event.getOldParent().getPath(), directoryName);
        Path newPath = Paths.get(event.getNewParent().getPath(), directoryName);
        Path resourcesPath = null;

        for (String resourcePath : resourcePaths) {
            if (oldPath.startsWith(resourcePath) || newPath.startsWith(resourcePath)) {
                resourcesPath = Paths.get(resourcePath);
                break;
            }
        }
        if (resourcesPath == null) return;

        boolean isFromResources = oldPath.startsWith(resourcesPath);
        boolean isToResources = newPath.startsWith(resourcesPath);

        if (isFromResources && isToResources) {
            String oldPrefix = calculateResourcePrefix(resourcesPath, oldPath);
            String newPrefix = calculateResourcePrefix(resourcesPath, newPath);

            for (String name : myMetaData.getResources()) {
                if (name.startsWith(oldPrefix)) {
                    myMetaData.removeResource(name);
                    myMetaData.addResource(newPrefix + name.substring(oldPrefix.length()));
                }
            }
        }
        else if (isToResources) {
            VirtualFile file = event.getFile();
            VfsUtilCore.visitChildrenRecursively(file, new VirtualFileVisitor<>() {
                @Override
                public boolean visitFile(@NotNull VirtualFile file) {
                    if (!file.isDirectory()) {
                        String resourceName = calculateResourceName(file.getPath());
                        if (!myMetaData.getResources().contains(resourceName))
                            myMetaData.addResource(resourceName);
                    }
                    return true;
                }
            });
        }
        else if (isFromResources) {
            String oldPrefix = calculateResourcePrefix(resourcesPath, oldPath);
            for (String name : myMetaData.getResources())
                if (name.startsWith(oldPrefix))
                    myMetaData.removeResource(name);
        }
    }

    private static String calculateResourcePrefix(Path base, Path eventDirectoryPath) {
        Path subPath = eventDirectoryPath.subpath(
            base.getNameCount(),
            eventDirectoryPath.getNameCount());
        return subPath + "/";
    }

    @Nullable
    private String calculateResourceName(String path) {
        for (String resourcePath : resourcePaths) {
            Matcher m = Pattern.compile(String.format("%s/(.+)", resourcePath)).matcher(path);
            if (m.matches())
                return m.group(1);
        }
        return null;
    }

    private void updateMetaResources(String oldName, String newName) {
        if (oldName != null)
            myMetaData.removeResource(oldName);
        if (newName != null) {
            myMetaData.addResource(newName);
        }
    }
}
