package edument.perl6idea.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.refactoring.BaseRefactoringProcessor;
import com.intellij.refactoring.extractSuperclass.ExtractSuperBaseDialog;
import com.intellij.ui.EditorComboBox;
import edument.perl6idea.psi.Perl6PackageDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RakuExtractPackageDialog extends ExtractSuperBaseDialog<Perl6PackageDecl, RakuAttributeInfo> {
    private final boolean isRole;
    // dummy for now we need to work around the original parent dialog issues
    private ComponentWithBrowseButton<EditorComboBox> myTargetField;

    protected RakuExtractPackageDialog(@NotNull Project project, Perl6PackageDecl aPackage, List<RakuAttributeInfo> members,
                                       boolean isRole, String refactoringName) {
        super(project, aPackage, members, refactoringName);
        this.isRole = isRole;
        init();
    }

    @Override
    protected ComponentWithBrowseButton<EditorComboBox> createPackageNameField() {
        myTargetField = new ComponentWithBrowseButton<>(new EditorComboBox(""), null);
        return myTargetField;
    }

    @Override
    protected JComponent createNorthPanel() {
        JComponent panel = super.createNorthPanel();
        // a horrible hack to make the original dialog understand we don't need
        // a target "package" field for now.
        // Normal override is prevented by part of the dialog fields being private
        if (panel != null) {
            Container innerPanel = (Container)panel.getComponent(0);
            for (int i = 0; i < innerPanel.getComponentCount(); i++) {
                Component component = innerPanel.getComponent(i);
                if (component instanceof Container) {
                    ((Container)component).remove(myTargetField);
                }
            }
        }
        return panel;
    }

    @Override
    protected JTextField createSourceClassField() {
        JTextField result = new JTextField();
        result.setEditable(false);
        result.setText(mySourceClass.getPackageName());
        return result;
    }

    @Override
    protected JComponent createActionComponent() {
        // We don't really need to check "extract vs rename" logic now
        return Box.createHorizontalBox();
    }

    @Override
    protected String getDocCommentPanelName() {
        return null;
    }

    @Override
    protected String getExtractedSuperNameNotSpecifiedMessage() {
        return "No name specified";
    }

    @Override
    public boolean isExtractSuperclass() {
        // XXX only pure extraction for now
        return true;
    }

    @Override
    protected BaseRefactoringProcessor createProcessor() {
        return null;
    }

    @Override
    protected int getDocCommentPolicySetting() {
        return 0;
    }

    @Override
    protected void setDocCommentPolicySetting(int policy) {}

    @Override
    protected @Nullable String validateName(String name) {
        return null;
    }

    @Override
    protected String getTopLabelText() {
        return "Extract from:";
    }

    @Override
    protected boolean hasPreviewButton() {
        return false;
    }

    @Override
    protected String getClassNameLabelText() {
        return isRole ? "Role name:" : "Parent class name:";
    }

    @Override
    protected String getPackageNameLabelText() {
        return "";
        // return "Target file (leave blank to extract to the same file)";
    }

    @Override
    protected @NotNull String getEntityName() {
        return isRole ? "Role" : "Parent Class";
    }

    @Override
    protected void preparePackage() throws OperationFailedException {
    }

    @Override
    protected String getDestinationPackageRecentKey() {
        return "Perl6ExtractPackageDialog.RECENT_KEYS";
    }

    @Override
    protected String getTargetPackageName() {
        // not really applicable?
        return "";
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        RakuAttributeSelectionPanel
            attributeSelectionPanel = new RakuAttributeSelectionPanel("Declarations to form " + (isRole ? "role" : "parent class"),
                                                                      myMemberInfos);
        panel.add(attributeSelectionPanel, BorderLayout.CENTER);
        return panel;
    }
}
