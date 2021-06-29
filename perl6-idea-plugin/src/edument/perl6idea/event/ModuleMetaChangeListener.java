package edument.perl6idea.event;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.*;
import com.intellij.util.messages.MessageBusConnection;
import edument.perl6idea.event.handlers.RakuModuleFileChangeListener;
import edument.perl6idea.event.handlers.RakuResourceFileChangeListener;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Service
public class ModuleMetaChangeListener implements BulkFileListener {
    private final Module myModule;
    private final RakuProjectFileChangeListener[] myListeners;

    public ModuleMetaChangeListener(Module module) {
        myModule = module;

        MessageBusConnection conn = ApplicationManager.getApplication().getMessageBus().connect();
        conn.subscribe(VirtualFileManager.VFS_CHANGES, this);
        Perl6MetaDataComponent metaData = module.getService(Perl6MetaDataComponent.class);
        myListeners = new RakuProjectFileChangeListener[]{
            new RakuModuleFileChangeListener(myModule, metaData),
            new RakuResourceFileChangeListener(myModule, metaData)
        };
    }

    @Override
    public void before(@NotNull List<? extends VFileEvent> list) {}

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        for (VFileEvent event : events) {
            VirtualFile file = event.getFile();
            if (file == null)
                continue;

            for (RakuProjectFileChangeListener listener : myListeners) {
                if (listener.shouldProcess(event)) {
                    if (event instanceof VFileCreateEvent) {
                        if (file.isDirectory())
                            listener.processDirectoryCreate(event);
                        else
                            listener.processFileCreate(event);
                    }
                    else if (event instanceof VFileDeleteEvent) {
                        if (file.isDirectory())
                            listener.processDirectoryDelete(event);
                        else
                            listener.processFileDelete(event);
                    }
                    else if (event instanceof VFilePropertyChangeEvent &&
                             Objects.equals(((VFilePropertyChangeEvent)event).getPropertyName(), VirtualFile.PROP_NAME) ||
                             event instanceof VFileMoveEvent) {
                        if (file.isDirectory())
                            listener.processDirectoryChange(event);
                        else
                            listener.processFileChange(event);
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
        }
    }
}
