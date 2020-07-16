package edument.perl6idea.project.structure.module.dependency.panel;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ComboBoxTableRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.*;
import com.intellij.ui.table.JBTable;
import com.intellij.util.IconUtil;
import com.intellij.util.containers.ArrayListSet;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.JBUI;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.utils.Perl6ModuleListFetcher;
import gnu.trove.TIntArrayList;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Perl6DependenciesPanelImpl extends JPanel {
    private final DependenciesTableModel myModel;
    private final JBTable myEntryTable;
    private final Project myProject;

    public Perl6DependenciesPanelImpl(ModuleConfigurationState state, Project project) {
        super(new BorderLayout());
        myProject = project;
        myModel = new DependenciesTableModel(state);
        myEntryTable = new JBTable(myModel);
        TableRowSorter<DependenciesTableModel> sorter = new TableRowSorter<>(myModel);
        sorter.setComparator(0, (Comparator<Perl6DependencyTableItem>)(o1, o2) -> o1.getEntry().compareTo(o2.getEntry()));
        myEntryTable.setRowSorter(sorter);
        sorter.setSortKeys(Arrays.asList(
          new RowSorter.SortKey(1, SortOrder.ASCENDING),
          new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        myEntryTable.setShowGrid(false);
        myEntryTable.setDragEnabled(false);
        myEntryTable.setIntercellSpacing(new Dimension(0, 0));
        myEntryTable.setDefaultRenderer(Perl6DependencyTableItem.class, new TableItemRenderer());

        JComboBox<Perl6DependencyScope> scopeEditor = new ComboBox<>(new EnumComboBoxModel<>(Perl6DependencyScope.class));
        myEntryTable.setDefaultEditor(Perl6DependencyScope.class, new DefaultCellEditor(scopeEditor));
        myEntryTable.setDefaultRenderer(
          Perl6DependencyScope.class,
          new ComboBoxTableRenderer<>(Perl6DependencyScope.values()));

        myEntryTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setFixedScopeColumnWidth();
        myEntryTable.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(10000);

        add(createTableWithButtons(), BorderLayout.CENTER);
    }

    private Component createTableWithButtons() {
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(myEntryTable);
        decorator.setAddAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton button) {
                Perl6DependencyAddAction action = new Perl6DependencyAddAction(
                  myProject, myModel);
                boolean isOk = action.showAndGet();
                if (isOk)
                    myModel.addRow(new Perl6DependencyTableItem(
                      action.myNameField.getText(),
                      (Perl6DependencyScope)action.myScopeCombo.getSelectedItem()
                    ));
            }
        }).addExtraAction(new AnActionButton("Edit", null, IconUtil.getEditIcon()) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                // Should not happen
                if (getSelectedItem() == null) return;
                Perl6DependencyAddAction action = new Perl6DependencyAddAction(
                  myProject, myModel, getSelectedItem());
                boolean isOk = action.showAndGet();
                if (isOk) {
                    int rowIndex = myEntryTable.getSelectedRow();
                    myModel.removeRow(rowIndex);
                    myModel.insertRow(rowIndex, new Perl6DependencyTableItem(
                      action.myNameField.getText(),
                      (Perl6DependencyScope)action.myScopeCombo.getSelectedItem()
                    ));
                }
            }

            @Override
            public boolean isEnabled() {
                return getSelectedItem() != null;
            }
        });
        return decorator.createPanel();
    }

    private void setFixedScopeColumnWidth() {
        final TableColumn column = myEntryTable.getTableHeader().getColumnModel().getColumn(1);
        final FontMetrics fontMetrics = myEntryTable.getFontMetrics(myEntryTable.getFont());
        final int width = fontMetrics.stringWidth(
          String.format(" %s      ", Perl6DependencyScope.BUILD_DEPENDS)) + JBUI.scale(4);
        column.setPreferredWidth(width);
        column.setMinWidth(width);
        column.setResizable(false);
    }

    public void initFromModel() {
        forceInitFromModel();
    }

    private void forceInitFromModel() {
        Set<Perl6DependencyTableItem> oldSelection = new HashSet<>();
        for (int i : myEntryTable.getSelectedRows())
            ContainerUtil.addIfNotNull(oldSelection, getItemAt(i));

        myModel.clear();
        myModel.init();
        myModel.fireTableDataChanged();
        TIntArrayList newSelection = new TIntArrayList();
        for (int i = 0; i < myModel.getRowCount(); i++) {
            if (oldSelection.contains(getItemAt(i)))
                newSelection.add(i);
        }
        TableUtil.selectRows(myEntryTable, newSelection.toNativeArray());
    }

    public void rootsChanged() {
        forceInitFromModel();
    }

    public boolean isModified() {
        return myModel.isModified();
    }

    public DependenciesTableModel getModel() {
        return myModel;
    }

    private Perl6DependencyTableItem getItemAt(int selectedRow) {
        return myModel.getItem(myEntryTable.convertRowIndexToModel(selectedRow));
    }

    private Perl6DependencyTableItem getSelectedItem() {
        if (myEntryTable.getSelectedRowCount() != 1) return null;
        return getItemAt(myEntryTable.getSelectedRow());
    }

    public void stopEditing() {
        TableUtil.stopEditing(myEntryTable);
    }

    private static class TableItemRenderer extends ColoredTableCellRenderer {
        @Override
        protected void customizeCellRenderer(JTable table,
                                             @Nullable Object value,
                                             boolean selected,
                                             boolean hasFocus,
                                             int row,
                                             int column) {
            setPaintFocusBorder(false);
            setFocusBorderAroundIcon(true);
        }
    }

    private static class Perl6DependencyAddAction extends DialogWrapper {
        private final DependenciesTableModel myModel;
        private Project myProject;
        private Set<Perl6DependencyTableItem> alreadyAdded;
        private JPanel myPanel;
        private ComboBox<Perl6DependencyScope> myScopeCombo = new ComboBox<>(Perl6DependencyScope.values());
        private TextFieldWithAutoCompletion<String> myNameField;

        @Nullable
        @Override
        protected ValidationInfo doValidate() {
            if (alreadyAdded.contains(
              new Perl6DependencyTableItem(
                myNameField.getText(),
                (Perl6DependencyScope)myScopeCombo.getSelectedItem())
            )) {
                return new ValidationInfo("This dependency already exists");
            }
            return null;
        }

        protected Perl6DependencyAddAction(Project project,
                                           DependenciesTableModel model) {
            super(project, false);
            myModel = model;
            myProject = project;
            alreadyAdded = new HashSet<>(model.getItems());
            init();
            setTitle("Add Dependency");
        }

        public Perl6DependencyAddAction(Project project, DependenciesTableModel model, Perl6DependencyTableItem item) {
            super(project, false);
            myModel = model;
            myProject = project;
            alreadyAdded = new HashSet<>(model.getItems());
            init();
            setTitle("Edit Dependency");
            myNameField.setText(item.getEntry());
            myScopeCombo.setSelectedItem(item.getScope());
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            myPanel = new JPanel(new MigLayout());
            myPanel.add(new JLabel("Name:"));
            myNameField = TextFieldWithAutoCompletion.create(myProject, new HashSet<>(), false, null);
            myNameField.setMinimumSize(new Dimension(250, 20));
            myPanel.add(myNameField, "wrap");
            myPanel.add(new JLabel("Scope:"));
            myPanel.add(myScopeCombo);
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(new Task.Backgroundable(myProject, "Getting Raku Modules List") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    Set<String> names = Perl6ModuleListFetcher.getNames(myProject);
                    Set<String> localNames = new ArrayListSet<>();
                    Module currentlyEditedModule = myModel.getState().getRootModel().getModule();
                    Module[] modules = ModuleManager.getInstance(myProject).getModules();
                    for (Module module : modules) {
                        if (module.equals(currentlyEditedModule))
                            continue;
                        Perl6MetaDataComponent component = module.getService(Perl6MetaDataComponent.class);
                        if (component != null && component.getName() != null)
                            localNames.add(component.getName());
                    }
                    myNameField.setVariants(Stream.concat(names.stream(), localNames.stream()).collect(Collectors.toSet()));
                }
            }, new EmptyProgressIndicator());
            return myPanel;
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return myNameField;
        }
    }
}
