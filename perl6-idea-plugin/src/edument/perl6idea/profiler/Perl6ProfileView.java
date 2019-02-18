package edument.perl6idea.profiler;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Function;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.sql.SQLException;
import java.util.List;

public class Perl6ProfileView extends JPanel {
    public static final Logger LOG = Logger.getInstance(Perl6ProfileView.class);
    protected Perl6ProfileData myProfileData;
    protected String myBaseProjectPath;
    private JPanel myPanel1;
    private JCheckBox myShowInternalsCheckBox;
    private JBTable callsNavigation;
    private JBTable callerTable;
    private JBTable calleeTable;

    public Perl6ProfileView(Perl6ProfileData profileData, String baseProjectPath) {
        myProfileData = profileData;
        myBaseProjectPath = baseProjectPath;
        myShowInternalsCheckBox.setSelected(true);
        setupCheckboxHandler();
        setupNavigation();
        //setupCalleeTable();
    }

    private void setupCheckboxHandler() {
        myShowInternalsCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateRowFilter();
            }
        });
    }

    private void setupCalleeTable(TableModel model) {
        calleeTable.setModel(model);
        calleeTable.setAutoCreateRowSorter(true);
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
        // Single selection + default sort for all columns
        callsNavigation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        callsNavigation.setAutoCreateRowSorter(true);

        // Default renderer
        Perl6ProfileNodeRenderer profileNodeRenderer = new Perl6ProfileNodeRenderer(myBaseProjectPath);
        callsNavigation.setDefaultRenderer(String.class, profileNodeRenderer);
        callsNavigation.setDefaultRenderer(Integer.class, profileNodeRenderer);

        // Setup a model
        Perl6ProfileModel model = new Perl6ProfileModel(calls);
        callsNavigation.setModel(model);

        // Constructs and applies appropriate filter
        updateRowFilter();
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
        /* In future we will likely have more options besides "Show internal calls"
         * so while we can avoid this method and construction of a Function for easy case,
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
