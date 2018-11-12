package edument.perl6idea.event;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent;
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent;
import com.intellij.util.messages.MessageBusConnection;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.module.Perl6MetaDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuleMetaChangeListener implements ModuleComponent, BulkFileListener {
    private final MessageBusConnection conn;
    private final Perl6MetaDataComponent myMetaData;

    public ModuleMetaChangeListener(Module module) {
        conn = ApplicationManager.getApplication().getMessageBus().connect();
        myMetaData = module.getComponent(Perl6MetaDataComponent.class);
    }

    @Nullable
    private static String calculateModuleName(String path) {
        Matcher m = Pattern.compile(".*?/lib/(.+).pm6").matcher(path);
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

            if (file == null || // File might be null
                // It is not a directory and its path does not end with `.pm6`, so skip it
                !file.isDirectory() && !(Objects.equals(file.getExtension(), Perl6ModuleFileType.INSTANCE.getDefaultExtension())))
                continue;

            // TODO Handle directories, not only files

            if (event instanceof VFileDeleteEvent) {
                if (file.isDirectory())
                    System.out.println("Directory is deleted");
                else
                    processFileDelete(event);
            }
            else if (event instanceof VFilePropertyChangeEvent &&
                     Objects.equals(((VFilePropertyChangeEvent)event).getPropertyName(), VirtualFile.PROP_NAME) ||
                     event instanceof VFileMoveEvent) {
                if (file.isDirectory())
                    System.out.println("Directory is moved or renamed");
                else
                    processFileMoveAndRename(event);
            } else {
                // If it another type of event, we don't want to update LocalFileSystem
                continue;
            }

            LocalFileSystem.getInstance().refresh(false);
        }
    }

    private void processFileMoveAndRename(VFileEvent event) {
        String oldPathRaw = event instanceof VFilePropertyChangeEvent ?
                            ((VFilePropertyChangeEvent)event).getOldPath() :
                            ((VFileMoveEvent)event).getOldPath();
        String oldName = calculateModuleName(oldPathRaw);
        String newName = calculateModuleName(event.getPath());
        if (oldName != null && newName != null)
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
            String libBasedModulePath = String.format("lib/%s.pm6", newName.replaceAll("::", "/"));
            myMetaData.addNamespaceToProvides(newName, libBasedModulePath);
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
        return "Perl 6 Module Change Listener";
    }
}
