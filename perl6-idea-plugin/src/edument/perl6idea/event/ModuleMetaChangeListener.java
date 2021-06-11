package edument.perl6idea.event;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.*;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.util.messages.MessageBusConnection;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ModuleMetaChangeListener implements BulkFileListener {
    private final Perl6MetaDataComponent myMetaData;
    private final Module myModule;
    private final List<String> modulePaths = new ArrayList<>();
    private final List<String> resourcePaths = new ArrayList<>();

    public ModuleMetaChangeListener(Module module) {
        myModule = module;

        MessageBusConnection conn = ApplicationManager.getApplication().getMessageBus().connect();
        conn.subscribe(VirtualFileManager.VFS_CHANGES, this);
        myMetaData = module.getService(Perl6MetaDataComponent.class);
        // Gather module prefix
        populateModulePaths();
        // Gather resources prefix
        VirtualFile metaVFile = myMetaData.getMetaFile();
        if (metaVFile != null) {
            File resourcesFile = Paths.get(metaVFile.getPath()).resolveSibling("resources").toFile();
            if (resourcesFile.exists())
                resourcePaths.add(resourcesFile.getAbsolutePath());
        }
    }

    private void populateModulePaths() {
        ContentEntry @NotNull [] entries = ModuleRootManager.getInstance(myModule).getContentEntries();
        for (ContentEntry entry : entries) {
            SourceFolder[] folders = entry.getSourceFolders();
            for (SourceFolder folder : folders) {
                if (!folder.isTestSource()) {
                    VirtualFile file = folder.getFile();
                    if (file != null)
                        modulePaths.add(Paths.get(file.getPath()).toFile().getAbsolutePath());
                }
            }
        }
    }

    @Nullable
    private String calculateModuleName(String path) {
        for (String modulePath : modulePaths) {
            Matcher m = Pattern.compile(String.format("%s/(.+).(rakumod|pm6)", modulePath)).matcher(path);
            if (m.matches()) {
                return m.group(1)
                    .replaceAll("/", "::")
                    .replaceAll("\\\\", "::");
            }
        }
        return null;
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

    @Override
    public void before(@NotNull List<? extends VFileEvent> list) {}

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        for (VFileEvent event : events) {
            VirtualFile file = event.getFile();
            boolean isForResources = isInResources(file);
            if (file == null ||
                !isForResources &&
                !(FileTypeManager.getInstance().getFileTypeByFile(file) instanceof Perl6ModuleFileType) &&
                !(file instanceof VirtualDirectoryImpl))
                continue;

            if (event instanceof VFileCreateEvent) {
                if (!file.isDirectory())
                    processFileCreate(event);
            }
            else if (event instanceof VFileDeleteEvent) {
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
            }
            else {
                // If it another type of event, we don't want to update LocalFileSystem
                continue;
            }

            if (!ApplicationManager.getApplication().isUnitTestMode()) {
                ApplicationManager.getApplication().invokeLater(
                    () -> {
                        ProjectView.getInstance(myModule.getProject()).refresh();
                        LocalFileSystem.getInstance().refresh(false);
                    });
            }
        }
    }

    private boolean isInResources(VirtualFile file) {
        for (String resourcePath : resourcePaths) {
            if (file.getPath().startsWith(resourcePath))
                return true;
        }
        return false;
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
                myMetaData.removeNamespaceFromProvides(name);
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
                    myMetaData.removeNamespaceFromProvides(name);
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
                    myMetaData.removeNamespaceFromProvides(name);
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
                myMetaData.removeNamespaceFromProvides(name);
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
        String oldModuleName = calculateModuleName(oldPathRaw);
        VirtualFile file = event.getFile();
        if (file == null)
            return;
        if (oldModuleName != null) {
            String newModuleName = calculateModuleName(file.getCanonicalPath());
            updateMetaProvides(oldModuleName, newModuleName);
            return;
        }
        String oldResourceName = calculateResourceName(oldPathRaw);
        if (oldResourceName != null) {
            String newResourceName = calculateResourceName(file.getCanonicalPath());
            updateMetaResources(oldResourceName, newResourceName);
        }
    }

    private void processFileCreate(VFileEvent event) {
        VirtualFile file = event.getFile();
        if (file == null)
            return;
        String newResource = calculateResourceName(file.getCanonicalPath());
        if (newResource != null) {
            updateMetaResources(null, newResource);
        }
    }

    private void processFileDelete(VFileEvent event) {
        VirtualFile file = event.getFile();
        if (file == null)
            return;
        String oldModuleName = calculateModuleName(file.getCanonicalPath());
        if (oldModuleName != null) {
            updateMetaProvides(oldModuleName, null);
            return;
        }
        String oldResource = calculateResourceName(file.getCanonicalPath());
        if (oldResource != null)
            updateMetaResources(oldResource, null);
    }

    private void updateMetaProvides(String oldName, String newName) {
        myMetaData.removeNamespaceFromProvides(oldName);
        if (newName != null) {
            myMetaData.addNamespaceToProvides(newName);
        }
    }

    private void updateMetaResources(String oldName, String newName) {
        if (oldName != null)
            myMetaData.removeResource(oldName);
        if (newName != null) {
            myMetaData.addResource(newName);
        }
    }
}
