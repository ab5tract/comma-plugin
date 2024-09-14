package org.raku.profiler;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.profiler.model.RakuProfileData;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

@State(name = "org.raku.profiler.RakuProfileDataManagerImpl", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class RakuProfileDataManagerImpl implements RakuProfileDataManager, PersistentStateComponent<Element> {
    private final static Logger LOG = Logger.getInstance(RakuProfileDataManagerImpl.class);
    private Path profileStoragePath = null;
    private final Project myProject;
    private final Deque<RakuProfileData> myProfileResults = new ArrayDeque<>(10);

    public RakuProfileDataManagerImpl(@NotNull Project project) {
        myProject = project;
        VirtualFile wsFile = myProject.getWorkspaceFile();
        if (wsFile != null && wsFile.exists()) {
            profileStoragePath = wsFile.toNioPath().resolveSibling("raku-profiles");
            try {
                Files.createDirectories(profileStoragePath);
            }
            catch (IOException e) {
                LOG.warn("Could not create directory to store profile snapshots: " + e.getMessage());
                profileStoragePath = null;
            }
        }
    }

    @Override
    public void loadState(@NotNull Element state) {
        List<Element> children = state.getChildren("suite");
        for (Element child : children) {
            try {
                String name = child.getAttributeValue("name");
                String filename = child.getAttributeValue("filename");
                if (filename != null) {
                    RakuProfileData value = new RakuProfileData(myProject, name, Paths.get(filename), false);
                    String renamed = child.getAttributeValue("isRenamed");
                    value.setNameChanged(renamed != null && !renamed.isEmpty());
                    myProfileResults.addLast(value);
                }
            }
            catch (Exception e) {
                LOG.info(e);
            }
        }
    }

    @Override
    public @Nullable Element getState() {
        Element element = new Element("state");
        if (profileStoragePath == null)
            return element;
        for (RakuProfileData entry : myProfileResults) {
            Element suiteElement = new Element("suite");

            suiteElement.setAttribute("name", entry.getName());
            suiteElement.setAttribute("isRenamed", entry.isNameChanged() ? "1" : "");
            Path pathToSnapshot = Paths.get(entry.getFileName());
            if (!pathToSnapshot.startsWith(profileStoragePath)) {
                try {
                    pathToSnapshot = Files.copy(pathToSnapshot, profileStoragePath.resolve(pathToSnapshot.getFileName()));
                }
                catch (IOException e) {
                    LOG.warn("Could not move profile snapshot '" + entry.getName() + "' to storage: '" + e.getMessage() + "', skipping...");
                    continue;
                }
            }
            suiteElement.setAttribute("filename", pathToSnapshot.toString());
            element.addContent(suiteElement);
        }
        return element;
    }

    @Override
    public Deque<RakuProfileData> getProfileResults() {
        return myProfileResults;
    }

    @Override
    public void saveProfileResult(RakuProfileData data) {
        // We keep at most 10 latest entries
        if (myProfileResults.stream().filter(s -> !s.isNameChanged()).count() == 10) {
            Iterator<RakuProfileData> iterator = myProfileResults.descendingIterator();
            while (iterator.hasNext()) {
                RakuProfileData next = iterator.next();
                if (!next.isNameChanged()) {
                    removeProfileResult(next);
                    break;
                }
            }
        }
        myProfileResults.addFirst(data);
    }

    @Override
    public void removeProfileResult(RakuProfileData data) {
        try {
            Files.deleteIfExists(Paths.get(data.getFileName()));
        }
        catch (IOException e) {
            Logger.getInstance(RakuProfileDataManagerImpl.class).warn("Could not delete profile: " + e.getMessage());
        }
        myProfileResults.remove(data);
    }
}
