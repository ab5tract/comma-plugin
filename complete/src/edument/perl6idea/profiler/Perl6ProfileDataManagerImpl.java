package edument.perl6idea.profiler;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;

@State(name = "edument.perl6idea.profiler.Perl6ProfileDataManagerImpl", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class Perl6ProfileDataManagerImpl implements Perl6ProfileDataManager, PersistentStateComponent<Element> {
    private final static Logger LOG = Logger.getInstance(Perl6ProfileDataManagerImpl.class);
    private final Project myProject;
    private final Deque<Perl6ProfileData> myProfileResults = new ArrayDeque<>(10);

    public Perl6ProfileDataManagerImpl(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public void loadState(@NotNull Element state) {
        List<Element> children = state.getChildren("suite");
        for (Element child : children) {
            try {
                String name = child.getAttributeValue("name");
                Perl6ProfileData value = new Perl6ProfileData(
                    myProject, name,
                    new String(Base64.getDecoder().decode(child.getValue()), CharsetToolkit.UTF8_CHARSET)
                );
                myProfileResults.addLast(value);
            } catch (SQLException e) {
                LOG.info(e);
            }
        }
    }

    @Override
    public @Nullable Element getState() {
        Element element = new Element("state");
        for (Perl6ProfileData entry : myProfileResults) {
            Element suiteElement = new Element("suite");
            element.addContent(suiteElement);
            suiteElement.setAttribute("name", entry.getName());
            suiteElement.addContent(Base64.getEncoder().encodeToString(entry.getProfileText()));
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
        if (myProfileResults.size() == 10)
            myProfileResults.removeLast();
        myProfileResults.addFirst(data);
    }

    @Override
    public void removeProfileResult(Perl6ProfileData data) {
        myProfileResults.remove(data);
    }
}
