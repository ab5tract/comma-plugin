package edument.perl6idea.profiler;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Function;
import edument.perl6idea.psi.Perl6File;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class Perl6ProfileView extends JPanel {
    public static final Logger LOG = Logger.getInstance(Perl6ProfileView.class);
    protected Project myProject;
    protected Perl6ProfileData myProfileData;
    protected String myBaseProjectPath;
    private JPanel myPanel1;
    private JCheckBox myShowInternalsCheckBox;
    private JBTable callsNavigation;
    private JBTable callerTable;
    private JBTable calleeTable;

    public Perl6ProfileView(Project project, Perl6ProfileData profileData) {
        myProject = project;
        myProfileData = profileData;
        myBaseProjectPath = myProject.getBaseDir().getCanonicalPath();
        myShowInternalsCheckBox.setSelected(true);
        setupCheckboxHandler();
        setupNavigation();
        updateCallData();
    }

    private void setupCheckboxHandler() {
        myShowInternalsCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateRowFilter();
            }
        });
    }

    private void updateCallData() {
        int callRow = callsNavigation.convertRowIndexToModel(callsNavigation.getSelectedRow());
        if (callRow >= 0) {
            Perl6ProfileModel navigationModel = (Perl6ProfileModel)callsNavigation.getModel();
            int callId = navigationModel.getNodeId(callRow);
            updateCalleeTable(callId);
            updateCallerTable(callId);
        }
    }

    private void updateCalleeTable(int callId) {
        List<Perl6ProfilerNode> calleeList = myProfileData.getCalleeListByCallId(callId);
        calleeTable.setModel(new Perl6ProfileModel(calleeList));
    }

    private void updateCallerTable(int callId) {
        List<Perl6ProfilerNode> callerList = myProfileData.getCallerListByCallId(callId);
        callerTable.setModel(new Perl6ProfileModel(callerList));
    }

    private void setupNavigation() {
        List<Perl6ProfilerNode> calls;
        try {
            calls = myProfileData.getNavigationNodes();
        }
        catch (SQLException e) {
            LOG.warn("Could not build a list of calls: " + e.getMessage());
            return;
        }
        // Setup a model
        Perl6ProfileModel model = new Perl6ProfileModel(calls);
        callsNavigation.setModel(model);

        // Single selection + default sort for all columns
        callsNavigation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        callsNavigation.setAutoCreateRowSorter(true);

        // Default renderer
        Perl6ProfileNodeRenderer profileNodeRenderer = new Perl6ProfileNodeRenderer(myBaseProjectPath);
        callsNavigation.setDefaultRenderer(String.class, profileNodeRenderer);
        callsNavigation.setDefaultRenderer(Integer.class, profileNodeRenderer);

        callsNavigation.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int index = callsNavigation.rowAtPoint(e.getPoint());
                    if (index < 0)
                        return;
                    int row = callsNavigation.convertRowIndexToModel(index);
                    goToCallAtRow(row);
                    updateCallData();
                }
            });

        // Constructs and applies appropriate filter
        updateRowFilter();

        // Select first row
        if (calls.size() > 0) {
            callsNavigation.setRowSelectionInterval(0, 0);
        }
    }

    private void goToCallAtRow(int row) {
        Perl6ProfileModel model = (Perl6ProfileModel)callsNavigation.getModel();
        if (!model.isCellInternal(row, myBaseProjectPath)) {
            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(model.getNodeSourceFile(row));
            if (file != null) {
                PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);
                if (!(psiFile instanceof Perl6File))
                    return;
                psiFile.navigate(true);
                Editor editor = FileEditorManager.getInstance(myProject).getSelectedTextEditor();
                if (editor == null) {
                    LOG.warn("Editor is not opened for some reason, file is" + file.getCanonicalPath());
                    return;
                }
                int offset = StringUtil.lineColToOffset(editor.getDocument().getText(), model.getNodeSourceLine(row) - 1, 0);
                editor.getCaretModel().moveToOffset(offset);
            }
        }
    }

    private void updateRowFilter() {
        Function<Integer, Boolean> isShown = generateVisibleCallsCondition();
        RowFilter <Perl6ProfileModel, Integer> filter = new RowFilter<Perl6ProfileModel, Integer>() {
            @Override
            public boolean include(Entry<? extends Perl6ProfileModel, ? extends Integer> entry) {
                return isShown.fun(entry.getIdentifier());
            }
        };
        ((TableRowSorter<Perl6ProfileModel>)callsNavigation.getRowSorter()).setRowFilter(filter);
    }

    private Function<Integer, Boolean> generateVisibleCallsCondition() {
        /* In future, we will likely have more options besides "Show internal calls"
         * so while we can avoid this method and construction of a Function for the easy case,
         * it may become much more complex, so the logic is generalized here from the start.
         */
        return rowIndex -> {
            Perl6ProfileModel navigationModel = (Perl6ProfileModel)callsNavigation.getModel();
            return myShowInternalsCheckBox.isSelected() ||
                   !navigationModel.isCellInternal(rowIndex, myBaseProjectPath);
        };
    }

    public JPanel getPanel() {
        return myPanel1;
    }
}
