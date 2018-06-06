package edument.perl6idea.project;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.GeneralProjectSettingsElement;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectStructureElementConfigurable;
import com.intellij.openapi.roots.ui.configuration.projectRoot.StructureConfigurableContext;
import com.intellij.openapi.roots.ui.configuration.projectRoot.daemon.ProjectStructureElement;
import com.intellij.openapi.ui.DetailsComponent;
import com.intellij.project.ProjectKt;
import com.intellij.util.ui.JBUI;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Perl6ProjectConfigurable extends ProjectStructureElementConfigurable<Project> implements DetailsComponent.Facade {
    private final Project myProject;
    private final GeneralProjectSettingsElement mySettingsElement;
    private DetailsComponent myDetailsComponent;
    private JPanel myPanel;
    private JTextField myProjectName;
    private Perl6SdkConfigurable myProjectSdkConfigurable;

    public Perl6ProjectConfigurable(Project project, StructureConfigurableContext context,
                                    ProjectSdksModel model) {
        myProject = project;
        mySettingsElement = new GeneralProjectSettingsElement(context);
        initUI(model);
    }

    private void initUI(ProjectSdksModel model) {
        myPanel = new JPanel(new MigLayout());
        if (ProjectKt.isDirectoryBased(myProject)) {
            final JPanel namePanel = new JPanel(new MigLayout());
            final JLabel label =
              new JLabel("<html><body><b>Project name:</b></body></html>", SwingConstants.LEFT);
            namePanel.add(label, BorderLayout.NORTH);

            myProjectName = new JTextField();
            myProjectName.setColumns(40);

            final JPanel nameFieldPanel = new JPanel();
            nameFieldPanel.setLayout(new BoxLayout(nameFieldPanel, BoxLayout.X_AXIS));
            nameFieldPanel.add(Box.createHorizontalStrut(4));
            nameFieldPanel.add(myProjectName);

            namePanel.add(nameFieldPanel, BorderLayout.CENTER);
            final JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrapper.add(namePanel);
            wrapper.setAlignmentX(0);
            myPanel.add(wrapper, "wrap");
        }
        myProjectSdkConfigurable = new Perl6SdkConfigurable(myProject, model);
        myPanel.add(myProjectSdkConfigurable.createComponent());

        myPanel.setBorder(JBUI.Borders.empty(0, 10));

        myProjectSdkConfigurable.addChangeListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    @Nullable
    @Override
    public ProjectStructureElement getProjectStructureElement() {
        return mySettingsElement;
    }

    @Override
    public void setDisplayName(String name) {
    }

    @Override
    public Project getEditableObject() {
        return null;
    }

    @Override
    public String getBannerSlogan() {
        return String.format("General Settings for Project '%s'", myProject.getName());
    }

    @Override
    public JComponent createOptionsPanel() {
        myDetailsComponent = new DetailsComponent(false, false);
        myDetailsComponent.setContent(myPanel);
        myDetailsComponent.setText(getBannerSlogan());
        return myDetailsComponent.getComponent();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Project";
    }

    @Nullable
    @Override
    public Icon getIcon(boolean expanded) {
        return AllIcons.Nodes.Project;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public DetailsComponent getDetailsComponent() {
        return myDetailsComponent;
    }
}
