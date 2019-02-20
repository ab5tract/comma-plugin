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
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
        setupNavigationSelectorListener(calleeTable);
        setupNavigationSelectorListener(callerTable);
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

    private void setupNavigationSelectorListener(JBTable table) {
        table.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() != MouseEvent.BUTTON1)
                        return;
                    if (e.getClickCount() != 2)
                        return;
                    int index = table.rowAtPoint(e.getPoint());
                    if (index < 0)
                        return;
                    // We have a row the user clicked on, get its model index
                    int relatedCallTableRow = table.convertRowIndexToModel(index);
                    // Get routine id of the call that we want to jump to
                    Perl6ProfileModel relatedCallsTableModel = (Perl6ProfileModel)table.getModel();
                    int callNodeId = relatedCallsTableModel.getNodeId(relatedCallTableRow);
                    // To jump, we need not a routine id, but its position in the navigation table
                    Perl6ProfileModel navigationModel = (Perl6ProfileModel)callsNavigation.getModel();
                    int navigationModelIndex = navigationModel.getNavigationIndexByCallId(callNodeId);
                    // It is a model index, so we need to convert it to view-able one
                    if (navigationModelIndex < 0) return;
                    int routineIndexToJumpTo = callsNavigation.convertRowIndexToView(navigationModelIndex);
                    if (routineIndexToJumpTo >= 0) {
                        callsNavigation.setRowSelectionInterval(routineIndexToJumpTo, routineIndexToJumpTo);
                        Rectangle cellRect = callsNavigation.getCellRect(routineIndexToJumpTo, 0, true);
                        callsNavigation.scrollRectToVisible(cellRect);
                        updateCallData();
                    }
                }
            });
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
        Perl6ProfileModel model = new Perl6ProfileNavigationModel(calls);
        callsNavigation.setModel(model);


        callsNavigation.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    updateCallData();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    updateCallData();
                }
            }
        });

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
                    if (e.getButton() != MouseEvent.BUTTON1)
                        return;
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
                callsNavigation.requestFocus();
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
