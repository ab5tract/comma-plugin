package edument.perl6idea.profiler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import edument.perl6idea.profiler.compare.ProfileCompareProcessor;
import edument.perl6idea.profiler.compare.ProfileCompareTab;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ProfileCompareDialog extends DialogWrapper {
    private final ProfileCompareProcessor.ProfileCompareResults myResults;

    public ProfileCompareDialog(Project project, ProfileCompareProcessor.ProfileCompareResults results) {
        super(project, true);
        myResults = results;
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        final JBTabbedPane tabbedPane = new JBTabbedPane();
        int i = 0;
        List<ProfileCompareTab> tabs = myResults.getTabs();
        for (ProfileCompareTab tab : tabs) {
            final int _i = i;
            tabbedPane.insertTab(tab.tabName, null, new JBLabel("Loading", AnimatedIcon.Default.INSTANCE, SwingConstants.LEFT), null, i++);
            tab.onDataReady(data -> {
                System.out.println("Data is ready! #" + _i + " (" + tab.dataProvider.getClass().getName() + ")");
                JBTable table = new JBTable(new DefaultTableModel(data, tab.getTableColumns()));
                table.setDefaultEditor(Object.class, null);
                ApplicationManager.getApplication().invokeLater(() -> {
                    System.out.println("Later... #" + _i + " (" + tab.dataProvider.getClass().getName() + ")");
                    tabbedPane.setComponentAt(_i, new JBScrollPane(table));
                }, ModalityState.any());
            });
        }

        return tabbedPane;
    }
}
