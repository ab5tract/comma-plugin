package edument.perl6idea.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectNameStep;
import com.intellij.ide.util.projectWizard.SdkSettingsStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportProvider;
import net.miginfocom.swing.MigLayout;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ProjectImportProvider extends ProjectImportProvider {
    protected Perl6ProjectImportProvider() {
        super(new Perl6ProjectBuilder());
    }

    @Override
    protected boolean canImportFromFile(VirtualFile file) {
        String fileName = file.getName();
        return fileName.equals("META6.json") || fileName.equals("META.info");
    }

    @Override
    public ModuleWizardStep[] createSteps(WizardContext context) {
        final Condition<SdkTypeId> condition = myBuilder::isSuitableSdkType;
        return new ModuleWizardStep[]{new ProjectNameStep(context),
          new SdkSettingsStep(context, new Perl6ModuleBuilder(), condition, null) {
              @Override
              public JComponent getComponent() {
                  JComponent oldPanel = super.getComponent();
                  JPanel newPanel = new JPanel(new MigLayout());
                  newPanel.add(new JLabel("Select a Perl 6 compiler to use"), "wrap 20");
                  newPanel.add(oldPanel);
                  return newPanel;
              }
          }};
    }

    @Nullable
    @Language("HTML")
    public String getFileSample() {
        return "<b>Perl 6</b> project file (META6.json)";
    }
}
