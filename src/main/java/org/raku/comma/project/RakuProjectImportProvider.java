package org.raku.comma.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.comma.module.RakuModuleBuilder;
import org.raku.comma.project.projectWizard.components.ProjectNameStep;
import org.raku.comma.project.projectWizard.components.SdkSettingsStep;
import org.raku.comma.project.projectWizard.StepSequence;
import org.raku.comma.sdk.RakuSdkType;
import net.miginfocom.swing.MigLayout;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RakuProjectImportProvider {
    protected RakuProjectBuilder myBuilder;

    public RakuProjectImportProvider() {
        myBuilder = new RakuProjectBuilder(null);
    }

    public boolean canImportFromFile(VirtualFile file) {
        if (file.isDirectory()) {
            if (file.findChild("META6.json") != null)
                return true;
            if (file.findChild("META.info") != null)
                return true;
        }
        String fileName = file.getName();
        return fileName.equals("META6.json") || fileName.equals("META.info");
    }

    public ModuleWizardStep[] createSteps(WizardContext context) {
        myBuilder = new RakuProjectBuilder(context);
        final Condition<SdkTypeId> condition = (sdkType) -> sdkType instanceof RakuSdkType;
        return new ModuleWizardStep[] {
          new ProjectNameStep(context),
          new SdkSettingsStep(context, new RakuModuleBuilder(), condition, null) {
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

    public RakuProjectBuilder getBuilder() {
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
        return "<html><body>Select <strong>Raku</strong> project file (META6.json or META.info) or a directory where it is placed.</body></html>";
    }
}
