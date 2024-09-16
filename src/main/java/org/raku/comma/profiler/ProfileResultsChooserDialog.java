package org.raku.comma.profiler;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.raku.comma.profiler.compare.ProfileCompareProcessor;
import org.raku.comma.profiler.model.RakuProfileData;
import org.raku.comma.profiler.run.RakuImportRunner;
import org.raku.comma.run.RakuProfileExecutor;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileResultsChooserDialog extends DialogWrapper {
    private final static Logger LOG = Logger.getInstance(ProfileResultsChooserDialog.class);
    private final Project myProject;
    private final RakuProfileDataManager myDataManager;
    private final JBTable myProfilesTable;
    private final ListTableModel<RakuProfileData> myProfilesTableModel;

    public ProfileResultsChooserDialog(Project project) {
        super(project, true);
        myProject = project;
        myDataManager = project.getService(RakuProfileDataManager.class);

        myProfilesTableModel = new ListTableModel<>(new ColumnInfo<RakuProfileData, String>("Profile Name") {
            @Override
            public @Nullable String valueOf(RakuProfileData data) {
                return data.getName();
            }

            @Override
            public boolean isCellEditable(RakuProfileData data) {
                return true;
            }

            @Override
            public void setValue(RakuProfileData data, String value) {
                data.setName(value);
                data.setNameChanged(true);
            }
        });
        myProfilesTable = new JBTable(myProfilesTableModel);
        myProfilesTable.getEmptyText().appendText("No profile results found.");
        initResults();
        setOKButtonText("Show Result");
        setTitle("Choose Raku Profile Data Results");
        init();
    }

    @Override
    protected void doOKAction() {
        int[] selectedRows = myProfilesTable.getSelectedRows();
        List<RakuProfileData> data = new ArrayList<>();
        for (int rowIndex : selectedRows) {
            data.add(myProfilesTableModel.getItem(rowIndex));
        }
        if (data.size() == 0 || data.size() > 2)
            return;
        RakuImportRunner profile = new RakuImportRunner(data);
        Executor executor = new RakuProfileExecutor();
        try {
            ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.create(myProject, executor, profile);
            builder.buildAndExecute();
        }
        catch (ExecutionException e1) {
            Messages.showErrorDialog(myProject, e1.getMessage(), "Loading Failed");
        }
    }

    @Override
    protected @NotNull Action getCancelAction() {
        return new DialogWrapperAction("Close") {
            @Override
            protected void doAction(ActionEvent e) {
                doCancelAction();
            }
        };
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (myProfilesTable.getSelectedRowCount() > 2 || myProfilesTable.getSelectedRowCount() == 0)
            return new ValidationInfo("One or two profiler results must be selected to display");
        return super.doValidate();
    }

    @Override
    protected @Nullable JComponent createNorthPanel() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new DeleteSelectedAction());
        group.add(new CompareSelectedAction());
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("Perl6ProfileResultsChooser", group, true);
        toolbar.setTargetComponent(myProfilesTable);
        return toolbar.getComponent();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel result = new JPanel(new MigLayout());
        result.add(new JLabel("<html>The last 10 profiles are retained by default.<br>Double-click on a profile to name it.<br>" +
                              "Hold Ctrl to select two or more profiles to compare.<br>" +
                              "Named profiles will never be deleted automatically.</html>"),
                   "wrap");
        JScrollPane pane = new JBScrollPane(myProfilesTable);
        result.add(pane, "growx, growy, pushx, pushy");
        return result;
    }

    private void initResults() {
        RakuProfileData[] profileSnapshots = myDataManager.getProfileResults().toArray(new RakuProfileData[0]);
        for (RakuProfileData data : profileSnapshots) {
            try {
                data.initialize();
            }
            catch (IOException | SQLException e) {
                LOG.warn("Could not initialize profiler results '" + data.getName() + "', cause: " + e.getMessage());
            }
        }
        myProfilesTableModel.setItems(Arrays.asList(profileSnapshots));
    }

    private class DeleteSelectedAction extends AnAction {
        DeleteSelectedAction() {
            super("Delete", "Delete selected profile result", PlatformIcons.DELETE_ICON);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            int[] selectedData = myProfilesTable.getSelectedRows();
            for (int data : selectedData) {
                RakuProfileData selectedProfile = myProfilesTableModel.getItem(data);
                myDataManager.removeProfileResult(selectedProfile);
            }
            myProfilesTableModel.setItems(new ArrayList<>(myDataManager.getProfileResults()));
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(myProfilesTable.getSelectedRowCount() != 0);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.EDT;
        }
    }

    private class CompareSelectedAction extends AnAction {
        CompareSelectedAction() { super("Compare selected profiles (select two profiles)", "Compare selected profile results", PlatformIcons.CHECK_ICON); }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            int[] selectedData = myProfilesTable.getSelectedRows();
            RakuProfileData[] profiles = {myProfilesTableModel.getItem(selectedData[1]), myProfilesTableModel.getItem(selectedData[0])};
            try {
                ProfileCompareProcessor.ProfileCompareResults results = new ProfileCompareProcessor(profiles).process();
                new ProfileCompareDialog(myProject, profiles, results).show();
            } catch (SQLException | IOException ex) {
                Messages.showErrorDialog(myProject, "Could not compare profiles: " + ex.getMessage(), "Profile Comparison Failed");
            }
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(myProfilesTable.getSelectedRows().length == 2);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.EDT;
        }
    }
}
