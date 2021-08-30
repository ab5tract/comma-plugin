package edument.perl6idea.profiler;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.profiler.model.Perl6ProfileData;
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

@State(name = "edument.perl6idea.profiler.Perl6ProfileDataManagerImpl", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class Perl6ProfileDataManagerImpl implements Perl6ProfileDataManager, PersistentStateComponent<Element> {
    private final static Logger LOG = Logger.getInstance(Perl6ProfileDataManagerImpl.class);
    private Path profileStoragePath = null;
    private final Project myProject;
    private final Deque<Perl6ProfileData> myProfileResults = new ArrayDeque<>(10);

    public Perl6ProfileDataManagerImpl(@NotNull Project project) {
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
                    Perl6ProfileData value = new Perl6ProfileData(myProject, name, Paths.get(filename));
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
        for (Perl6ProfileData entry : myProfileResults) {
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
    public Deque<Perl6ProfileData> getProfileResults() {
        return myProfileResults;
    }

    @Override
    public void saveProfileResult(Perl6ProfileData data) {
        // We keep at most 10 latest entries
        if (myProfileResults.stream().filter(s -> !s.isNameChanged()).count() == 10) {
            Iterator<Perl6ProfileData> iterator = myProfileResults.descendingIterator();
            while (iterator.hasNext()) {
                Perl6ProfileData next = iterator.next();
                if (!next.isNameChanged()) {
                    myProfileResults.remove(next);
                    break;
                }
            }
        }
        myProfileResults.addFirst(data);
    }

    @Override
    public void removeProfileResult(Perl6ProfileData data) {
        myProfileResults.remove(data);
    }
}
