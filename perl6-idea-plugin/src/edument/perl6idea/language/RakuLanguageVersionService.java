package edument.perl6idea.language;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

@Service
@State(name = "edument.perl6idea.language.RakuLanguageVersionService")
public final class RakuLanguageVersionService implements PersistentStateComponent<Element> {
    private RakuLanguageVersion myVersion;
    private boolean myIsExplicit;

    public RakuLanguageVersion getVersion() {
        return myVersion;
    }

    public void setVersion(RakuLanguageVersion version) {
        myVersion = version;
    }

    public boolean getIsExplicit() {
        return myIsExplicit;
    }

    public void setExplicit(boolean explicit) {
        myIsExplicit = explicit;
    }

    @Override
    public @NotNull Element getState() {
        Element el = new Element("language");
        el.setAttribute("version", myVersion.name());
        el.setAttribute("explicit-for-new", String.valueOf(myIsExplicit));
        return el;
    }

    @Override
    public void loadState(@NotNull Element state) {
        String version = state.getAttributeValue("version");
        if (version != null)
            myVersion = RakuLanguageVersion.valueOf(version);
        else
            myVersion = RakuLanguageVersion.D;
        String is_explicit = state.getAttributeValue("explicit-for-new");
        myIsExplicit = is_explicit == null || is_explicit.equals("true");
    }
}
