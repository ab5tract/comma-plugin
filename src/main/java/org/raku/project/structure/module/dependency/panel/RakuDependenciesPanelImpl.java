package org.raku.project.structure.module.dependency.panel;

import com.intellij.openapi.actionSystem.AnAction;
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
import org.raku.metadata.RakuMetaDataComponent;
import org.raku.utils.RakuModuleListFetcher;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RakuDependenciesPanelImpl extends JPanel {
    private final DependenciesTableModel myModel;
    private final JBTable myEntryTable;
    private final Project myProject;

    public RakuDependenciesPanelImpl(ModuleConfigurationState state, Project project) {
        super(new BorderLayout());
        myProject = project;
        myModel = new DependenciesTableModel(state);
        myEntryTable = new JBTable(myModel);
        TableRowSorter<DependenciesTableModel> sorter = new TableRowSorter<>(myModel);
        sorter.setComparator(0, Comparator.comparing(o -> ((RakuDependencyTableItem)o).getEntry()));
        myEntryTable.setRowSorter(sorter);
        sorter.setSortKeys(Arrays.asList(
          new RowSorter.SortKey(1, SortOrder.ASCENDING),
          new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        myEntryTable.setShowGrid(false);
        myEntryTable.setDragEnabled(false);
        myEntryTable.setIntercellSpacing(new Dimension(0, 0));
        myEntryTable.setDefaultRenderer(RakuDependencyTableItem.class, new TableItemRenderer());

        JComboBox<RakuDependencyScope> scopeEditor = new ComboBox<>(new EnumComboBoxModel<>(RakuDependencyScope.class));
        myEntryTable.setDefaultEditor(RakuDependencyScope.class, new DefaultCellEditor(scopeEditor));
        myEntryTable.setDefaultRenderer(
          RakuDependencyScope.class,
          new ComboBoxTableRenderer<>(RakuDependencyScope.values()));

        myEntryTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setFixedScopeColumnWidth();
        myEntryTable.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(10000);

        add(createTableWithButtons(), BorderLayout.CENTER);
    }

    private Component createTableWithButtons() {
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(myEntryTable);
        decorator.setAddAction(button -> {
            RakuDependencyAddAction action = new RakuDependencyAddAction(myProject, myModel);
            boolean isOk = action.showAndGet();
            if (isOk) {
                myModel.addRow(new RakuDependencyTableItem(action.myNameField.getText(),
                                                           (RakuDependencyScope) action.myScopeCombo.getSelectedItem()));
            }
        }).addExtraAction(new AnAction(() -> "Edit", IconUtil.getEditIcon()) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Should not happen
                if (getSelectedItem() == null) return;
                RakuDependencyAddAction action = new RakuDependencyAddAction(
                  myProject, myModel, getSelectedItem());
                boolean isOk = action.showAndGet();
                if (isOk) {
                    int rowIndex = myEntryTable.getSelectedRow();
                    myModel.removeRow(rowIndex);
                    myModel.insertRow(rowIndex, new RakuDependencyTableItem(
                      action.myNameField.getText(),
                      (RakuDependencyScope)action.myScopeCombo.getSelectedItem()
                    ));
                }
            }
        });
        return decorator.createPanel();
    }

    private void setFixedScopeColumnWidth() {
        final TableColumn column = myEntryTable.getTableHeader().getColumnModel().getColumn(1);
        final FontMetrics fontMetrics = myEntryTable.getFontMetrics(myEntryTable.getFont());
        final int width = fontMetrics.stringWidth(
          String.format(" %s      ", RakuDependencyScope.BUILD_DEPENDS)) + JBUI.scale(4);
        column.setPreferredWidth(width);
        column.setMinWidth(width);
        column.setResizable(false);
    }

    public void initFromModel() {
        forceInitFromModel();
    }

    private void forceInitFromModel() {
        Set<RakuDependencyTableItem> oldSelection = new HashSet<>();
        for (int i : myEntryTable.getSelectedRows())
            ContainerUtil.addIfNotNull(oldSelection, getItemAt(i));

        myModel.clear();
        myModel.init();
        myModel.fireTableDataChanged();
        List<Integer> newSelection = new ArrayList<>();
        for (int i = 0; i < myModel.getRowCount(); i++) {
            if (oldSelection.contains(getItemAt(i)))
                newSelection.add(i);
        }
        TableUtil.selectRows(myEntryTable, newSelection.stream().mapToInt(i->i).toArray());
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

    private RakuDependencyTableItem getItemAt(int selectedRow) {
        return myModel.getItem(myEntryTable.convertRowIndexToModel(selectedRow));
    }

    private RakuDependencyTableItem getSelectedItem() {
        if (myEntryTable.getSelectedRowCount() != 1) return null;
        return getItemAt(myEntryTable.getSelectedRow());
    }

    public void stopEditing() {
        TableUtil.stopEditing(myEntryTable);
    }

    private static class TableItemRenderer extends ColoredTableCellRenderer {
        @Override
        protected void customizeCellRenderer(@NotNull JTable table,
                                             @Nullable Object value,
                                             boolean selected,
                                             boolean hasFocus,
                                             int row,
                                             int column) {
            setPaintFocusBorder(false);
            setFocusBorderAroundIcon(true);
        }
    }

    private static class RakuDependencyAddAction extends DialogWrapper {
        private final DependenciesTableModel myModel;
        private final Project myProject;
        private final Set<RakuDependencyTableItem> alreadyAdded;
        private final ComboBox<RakuDependencyScope> myScopeCombo = new ComboBox<>(RakuDependencyScope.values());
        private TextFieldWithAutoCompletion<String> myNameField;

        @Nullable
        @Override
        protected ValidationInfo doValidate() {
            if (alreadyAdded.contains(
              new RakuDependencyTableItem(
                myNameField.getText(),
                (RakuDependencyScope)myScopeCombo.getSelectedItem())
            )) {
                return new ValidationInfo("This dependency already exists");
            }
            return null;
        }

        protected RakuDependencyAddAction(Project project,
                                          DependenciesTableModel model) {
            super(project, false);
            myModel = model;
            myProject = project;
            alreadyAdded = new HashSet<>(model.getItems());
            init();
            setTitle("Add Dependency");
        }

        RakuDependencyAddAction(Project project, DependenciesTableModel model, RakuDependencyTableItem item) {
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
            JPanel panel = new JPanel(new MigLayout());
            panel.add(new JLabel("Name:"));
            myNameField = TextFieldWithAutoCompletion.create(myProject, new HashSet<>(), false, null);
            myNameField.setMinimumSize(new Dimension(250, 20));
            panel.add(myNameField, "wrap");
            panel.add(new JLabel("Scope:"));
            panel.add(myScopeCombo);
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(new Task.Backgroundable(myProject, "Getting Raku Modules List") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    Set<String> names = RakuModuleListFetcher.getNames(myProject);
                    Set<String> localNames = new ArrayListSet<>();
                    Module currentlyEditedModule = myModel.getState().getCurrentRootModel().getModule();
                    Module[] modules = ModuleManager.getInstance(myProject).getModules();
                    for (Module module : modules) {
                        if (module.equals(currentlyEditedModule)) continue;
                        RakuMetaDataComponent component = module.getService(RakuMetaDataComponent.class);
                        if (component != null && component.getName() != null) {
                            localNames.add(component.getName());
                        }
                    }
                    myNameField.setVariants(Stream.concat(names.stream(), localNames.stream()).collect(Collectors.toSet()));
                }
            }, new EmptyProgressIndicator());
            return panel;
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return myNameField;
        }
    }
}
