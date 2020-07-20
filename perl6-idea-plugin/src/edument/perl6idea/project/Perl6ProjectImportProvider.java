package edument.perl6idea.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.module.Perl6ModuleBuilder;
import edument.perl6idea.project.projectWizard.components.ProjectNameStep;
import edument.perl6idea.project.projectWizard.components.SdkSettingsStep;
import edument.perl6idea.project.projectWizard.StepSequence;
import edument.perl6idea.sdk.Perl6SdkType;
import net.miginfocom.swing.MigLayout;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Perl6ProjectImportProvider {
    protected Perl6ProjectBuilder myBuilder;

    public Perl6ProjectImportProvider() {
        myBuilder = new Perl6ProjectBuilder(null);
    }

    public boolean canImportFromFile(VirtualFile file) {
        String fileName = file.getName();
        return fileName.equals("META6.json") || fileName.equals("META.info");
    }

    public ModuleWizardStep[] createSteps(WizardContext context) {
        myBuilder = new Perl6ProjectBuilder(context);
        final Condition<SdkTypeId> condition = (sdkType) -> sdkType instanceof Perl6SdkType;
        return new ModuleWizardStep[] {
          new ProjectNameStep(context),
          new SdkSettingsStep(context, new Perl6ModuleBuilder(), condition, null) {
              @Override
              public JComponent getComponent() {
                  JComponent oldPanel = super.getComponent();
                  JPanel newPanel = new JPanel(new MigLayout());
                  newPanel.add(new JLabel("Select a Raku compiler to use"), "wrap 20");
                  newPanel.add(oldPanel);
                  return newPanel;
              }
          }};
    }

    @NonNls
    @NotNull
    public String getId() {
        return getBuilder().getName();
    }

    public String getName() {
        return myBuilder.getName();
    }

    public Perl6ProjectBuilder getBuilder() {
        return myBuilder;
    }

    public void addSteps(StepSequence sequence, WizardContext context, String id) {
        ModuleWizardStep[] steps = createSteps(context);
        for (ModuleWizardStep step : steps) {
            sequence.addSpecificStep(id, step);
        }
    }

    @Language("HTML")
    public static String getDescription() {
        return "<html>Select <b>Raku</b> project file (META6.json or META.info) or directory.</html>";
    }
}
