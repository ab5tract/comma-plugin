package edument.perl6idea.profiler;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.components.JBList;
import com.intellij.util.PlatformIcons;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import edument.perl6idea.profiler.run.Perl6ImportRunner;
import edument.perl6idea.run.Perl6ProfileExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Deque;
import java.util.List;

public class ProfileResultsChooserDialog extends DialogWrapper {
    private final static Logger LOG = Logger.getInstance(ProfileResultsChooserDialog.class);
    private final Project myProject;
    private final JBList<Perl6ProfileData> myResultsList;
    private final Perl6ProfileDataManager myDataManager;

    public ProfileResultsChooserDialog(Project project) {
        super(project, true);
        myProject = project;
        myDataManager = ServiceManager.getService(project, Perl6ProfileDataManager.class);

        myResultsList = new JBList<>();
        myResultsList.getEmptyText().appendText("No profile results found.");
        myResultsList.setCellRenderer(new SimpleListCellRenderer<>() {
            @Override
            public void customize(@NotNull JList<? extends Perl6ProfileData> list,
                                  Perl6ProfileData value,
                                  int index,
                                  boolean selected,
                                  boolean hasFocus) {
                setText(value.getName());
            }
        });
        initResults();
        setOKButtonText("Show Result");
        setTitle("Choose Raku Profile Data Results");
        init();
    }

    @Override
    protected void doOKAction() {
        Perl6ProfileData data = myResultsList.getSelectedValue();
        if (data == null)
            return;
        Perl6ImportRunner profile = new Perl6ImportRunner(data);
        Executor executor = new Perl6ProfileExecutor();
        try {
            ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.create(myProject, executor, profile);
            builder.buildAndExecute();
        }
        catch (ExecutionException e1) {
            Messages.showErrorDialog(myProject, e1.getMessage(), "Loading Failed");
        }
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (myResultsList.getSelectedValuesList().size() != 1)
            return new ValidationInfo("Only one profiler result must be selected to display");
        return super.doValidate();
    }

    @Override
    protected @Nullable JComponent createNorthPanel() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new DeleteResultsAction());
        group.add(new DeleteAllAction());
        return ActionManager.getInstance().createActionToolbar("Perl6ProfileResultsChooser", group, true).getComponent();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return ScrollPaneFactory.createScrollPane(myResultsList);
    }

    private void initResults() {
        Perl6ProfileData[] profileSnapshots = myDataManager.getProfileResults().toArray(new Perl6ProfileData[0]);
        for (Perl6ProfileData data : profileSnapshots) {
            try {
                data.initialize();
            }
            catch (IOException|SQLException e) {
                LOG.warn("Could not initialize profiler results '" + data.getName() + "', cause: " + e.getMessage());
            }
        }
        myResultsList.setListData(profileSnapshots);
    }

    private class DeleteResultsAction extends AnAction {
        DeleteResultsAction() {
            super("Delete", "Delete selected profile result", PlatformIcons.DELETE_ICON);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            List<Perl6ProfileData> selectedData = myResultsList.getSelectedValuesList();
            for (Perl6ProfileData data : selectedData) {
                myDataManager.removeProfileResult(data);
                myResultsList.setListData(selectedData.toArray(new Perl6ProfileData[0]));
            }
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(myResultsList.getSelectedValuesList().size() != 0);
        }
    }

    private class DeleteAllAction extends AnAction {
        DeleteAllAction() {
            super("Delete All", "Delete all profile results", AllIcons.Actions.Cancel);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Deque<Perl6ProfileData> selectedData = myDataManager.getProfileResults();
            for (Perl6ProfileData data : selectedData)
                myDataManager.removeProfileResult(data);
            myResultsList.setListData(selectedData.toArray(new Perl6ProfileData[0]));
        }
    }
}
