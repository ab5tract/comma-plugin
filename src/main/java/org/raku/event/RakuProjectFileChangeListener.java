package org.raku.event;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;

import java.io.File;
import java.util.List;

import static org.raku.metadata.RakuMetaDataComponent.META6_JSON_NAME;
import static org.raku.metadata.RakuMetaDataComponent.META_OBSOLETE_NAME;

public abstract class RakuProjectFileChangeListener {
    public void processFileCreate(VFileEvent event) {}
    public void processDirectoryCreate(VFileEvent event) {}
    public void processFileDelete(VFileEvent event) {}
    public void processDirectoryDelete(VFileEvent event) {}
    public void processFileChange(VFileEvent event) {}
    public void processDirectoryChange(VFileEvent event) {}

    public abstract boolean shouldProcess(VFileEvent event);

    // Common helpers
    public void processDirectoryCreate(VFileEvent event, String directoryName, List<String> pathsToWatch) {
        VirtualFile file = event.getFile();
        assert file != null;
        if (file.getName().equals(directoryName) &&
            (file.findFileByRelativePath("../" + META6_JSON_NAME) != null ||
             file.findFileByRelativePath("../" + META_OBSOLETE_NAME) != null
            )) {
            File fileDirectory = file.toNioPath().toFile();
            pathsToWatch.add(fileDirectory.getAbsolutePath());
        }
    }
}
