package edument.perl6idea.project;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.ui.navigation.Place;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ProjectStructureConfigurable extends BaseConfigurable implements SearchableConfigurable,
                                                                                   Place.Navigator, Configurable.NoMargin,
                                                                                   Configurable.NoScroll{
    private JPanel myComponent;

    @NotNull
    @Override
    public String getId() {
        return "perl6.project.structure";
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Perl 6 Project Structure";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        myComponent = new JPanel();
        myComponent.add(new JLabel("Settings"));
        return myComponent;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public ActionCallback navigateTo(@Nullable Place place, boolean requestFocus) {
        return null;
    }

    public static Configurable getInstance(final Project project) {
        return ServiceManager.getService(project, Perl6ProjectStructureConfigurable.class);
    }
}
