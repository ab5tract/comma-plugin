package edument.perl6idea.profiler.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Function;
import com.intellij.util.PlatformIcons;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.module.Perl6ModuleType;
import edument.perl6idea.profiler.model.*;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class Perl6ProfileRoutinesPanel extends JPanel {
    public static final Logger LOG = Logger.getInstance(Perl6ProfileRoutinesPanel.class);
    protected Project myProject;
    protected Perl6ProfileData myProfileData;
    protected List<String> myModuleNames;
    protected List<String> myModuleBasePaths;
    private JPanel myPanel1;
    private ComboBox<Perl6ProfilerFrameResultFilter> myHideExternalsComboBox;
    private JBTable callsNavigation;
    private JBTable callerTable;
    private JBTable calleeTable;
    private JCheckBox myShowRealNamesCheckBox;
    private String namePattern = "";
    private JTextField myFilterByNameTextField;
    private JSeparator separator1;
    private JSeparator separator2;
    private final Perl6ProfileNodeRenderer myProfileNodeRenderer;

    public Perl6ProfileRoutinesPanel(Project project, Perl6ProfileData profileData) {
        myProject = project;
        myProfileData = profileData;
        // Default renderer
        fillModuleData(myProject);
        myProfileNodeRenderer = new Perl6ProfileNodeRenderer(myModuleBasePaths, myModuleNames, myHideExternalsComboBox);
        myHideExternalsComboBox.addItem(Perl6ProfilerFrameResultFilter.Everything);
        myHideExternalsComboBox.addItem(Perl6ProfilerFrameResultFilter.NoExternals);
        myHideExternalsComboBox.addItem(Perl6ProfilerFrameResultFilter.NoCore);
        myHideExternalsComboBox.setSelectedItem(Perl6ProfilerFrameResultFilter.NoCore);
        myShowRealNamesCheckBox.setSelected(false);
        setupCheckboxHandlers();
        setupNavigation();
        updateCallData();
        setupNavigationFilters();
        setupNavigationSelectorListener(calleeTable);
        setupNavigationSelectorListener(callerTable);
        setupContextMenuActions();
        setupSeparators();
    }

    private void fillModuleData(Project project) {
        myModuleNames = new ArrayList<>();
        myModuleBasePaths = new ArrayList<>();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ContentEntry[] entries = ModuleRootManager.getInstance(module).getContentEntries();
            myModuleBasePaths.addAll(Arrays.stream(entries).map(
                e -> e.getFile() != null ? e.getFile().getPath() : null
            ).filter(s -> s != null).toList());

            String name = module.getModuleTypeName();
            if (name == null || !name.equals(Perl6ModuleType.getInstance().getId()))
                continue;
            Perl6MetaDataComponent metaDataComponent = module.getService(Perl6MetaDataComponent.class);
            if (metaDataComponent == null)
                continue;
            myModuleNames.addAll(metaDataComponent.getProvidedNames());
        }
    }

    private void setupSeparators() {
        separator1.setPreferredSize(new Dimension(2, 10));
        separator2.setPreferredSize(new Dimension(2, 10));
    }

    private void setupContextMenuActions() {
        setupContextMenuForTable(callsNavigation);
        setupContextMenuForTable(calleeTable);
        setupContextMenuForTable(callerTable);
    }

    private static void setupContextMenuForTable(JBTable table) {
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    Point point = SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table);
                    int rowAtPoint = table.rowAtPoint(point);
                    int columnAtPoint = table.columnAtPoint(point);
                    if (rowAtPoint > -1)
                        table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                    if (columnAtPoint > -1) {
                        table.setColumnSelectionInterval(columnAtPoint, columnAtPoint);
                    }
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        populatePopupActions(table, popupMenu);
        table.setComponentPopupMenu(popupMenu);
    }

    private static void populatePopupActions(JBTable table, JPopupMenu popupMenu) {
        JMenuItem deleteItem = new JMenuItem("Copy to Clipboard", PlatformIcons.COPY_ICON);
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object value = table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
                StringSelection stringSelection = new StringSelection(value.toString());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        });
        popupMenu.add(deleteItem);
    }

    private void setupNavigationFilters() {
        myFilterByNameTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                namePattern = myFilterByNameTextField.getText();
                updateRowFilter();
            }
        });
    }

    private void setupCheckboxHandlers() {
        myHideExternalsComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateRowFilter();
            }
        });
        myShowRealNamesCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                toggleRealFilenamesVisibility(callsNavigation, myShowRealNamesCheckBox.isSelected());
                toggleRealFilenamesVisibility(calleeTable, myShowRealNamesCheckBox.isSelected());
                toggleRealFilenamesVisibility(callerTable, myShowRealNamesCheckBox.isSelected());
            }
        });
    }

    private static void toggleRealFilenamesVisibility(JBTable table, boolean value) {
        Perl6ProfileModel model = (Perl6ProfileModel)table.getModel();
        model.setShowRealFileNames(value);
        table.updateUI();
    }

    private void updateCallData() {
        int row = callsNavigation.getSelectedRow();
        if (row < 0)
            return;
        int callRow = callsNavigation.convertRowIndexToModel(row);
        if (callRow >= 0) {
            Perl6ProfileModel navigationModel = (Perl6ProfileModel)callsNavigation.getModel();
            int callId = navigationModel.getNodeId(callRow);
            updateCalleeTable(callId);
            updateCallerTable(callId);
        }
    }

    private void updateCalleeTable(int callId) {
        List<Perl6ProfileCall> calleeList = myProfileData.getCalleeListByCallId(callId);
        calleeTable.setModel(new Perl6ProfileModel(myProject, calleeList));
        TableColumnModel columnModel = calleeTable.getColumnModel();
        columnModel.getColumn(0).setCellRenderer(myProfileNodeRenderer);
        columnModel.getColumn(1).setCellRenderer(myProfileNodeRenderer);
        columnModel.getColumn(2).setCellRenderer(new PercentageTableCellRenderer());
        columnModel.getColumn(3).setCellRenderer(myProfileNodeRenderer);
    }

    private void updateCallerTable(int callId) {
        List<Perl6ProfileCall> callerList = myProfileData.getCallerListByCallId(callId);
        callerTable.setModel(new Perl6ProfileModel(myProject, callerList));
        TableColumnModel columnModel = callerTable.getColumnModel();
        columnModel.getColumn(0).setCellRenderer(myProfileNodeRenderer);
        columnModel.getColumn(1).setCellRenderer(myProfileNodeRenderer);
        columnModel.getColumn(2).setCellRenderer(new PercentageTableCellRenderer());
        columnModel.getColumn(3).setCellRenderer(myProfileNodeRenderer);
    }

    private void setupNavigationSelectorListener(JBTable table) {
        table.addMouseListener(
            new MouseAdapter() {
                @Override
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
        List<Perl6ProfileCall> calls;
        try {
            calls = myProfileData.getNavigationNodes();
        }
        catch (SQLException e) {
            LOG.warn("Could not build a list of calls: " + e.getMessage());
            return;
        }
        // Setup a model
        Perl6ProfileNavigationModel model = new Perl6ProfileNavigationModel(myProject, calls);
        callsNavigation.setModel(model);
        // Single selection + default sort for all columns
        callsNavigation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        callsNavigation.setRowSorter(new Perl6ProfileOutputTableRowSorter(model));
        TableColumnModel tableColumnModel = callsNavigation.getColumnModel();
        tableColumnModel.getColumn(0).setCellRenderer(myProfileNodeRenderer);
        tableColumnModel.getColumn(1).setCellRenderer(myProfileNodeRenderer);
        tableColumnModel.getColumn(2).setCellRenderer(new PercentageTableCellRenderer());
        tableColumnModel.getColumn(3).setCellRenderer(new PercentageTableCellRenderer());
        tableColumnModel.getColumn(4).setCellRenderer(myProfileNodeRenderer);
        callsNavigation.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    updateCallData();
                }
            }
        });
        callsNavigation.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() != MouseEvent.BUTTON1)
                        return;
                    int index = callsNavigation.rowAtPoint(e.getPoint());
                    if (index < 0)
                        return;
                    int row = callsNavigation.convertRowIndexToModel(index);
                    updateCallData();
                    if (e.getClickCount() == 2)
                        goToCallAtRow(row);
                }
            });

        // Constructs and applies appropriate filter
        updateRowFilter();

        // Select first row
        if (callsNavigation.getRowCount() > 0) {
            callsNavigation.setRowSelectionInterval(0, 0);
        }
    }

    private void goToCallAtRow(int row) {
        Perl6ProfileModel model = (Perl6ProfileModel)callsNavigation.getModel();
        if (!model.isCellInternal(row, myModuleNames, myModuleBasePaths,
                                  (Perl6ProfilerFrameResultFilter)myHideExternalsComboBox.getSelectedItem())) {
            String sourceFilePath = model.getNodeSourceFile(row);
            VirtualFile file = null;
            if (sourceFilePath.startsWith("site#")) {
                String[] pathAndModule = sourceFilePath.split(" ");
                if (pathAndModule.length == 2) {
                    String moduleNameKey = pathAndModule[1].substring(1, pathAndModule[1].length() - 1);
                    Collection<Perl6File> indexedFile =
                        ProjectModulesStubIndex.getInstance().get(moduleNameKey, myProject, GlobalSearchScope.allScope(myProject));
                    if (!indexedFile.isEmpty())
                        file = indexedFile.iterator().next().getVirtualFile();
                }
            }
            if (file == null)
                file = LocalFileSystem.getInstance().findFileByPath(sourceFilePath);
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
                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                callsNavigation.requestFocus();
            }
        }
    }

    private void updateRowFilter() {
        Function<Integer, Boolean> isShown = generateVisibleCallsCondition();
        RowFilter<Perl6ProfileModel, Integer> filter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends Perl6ProfileModel, ? extends Integer> entry) {
                return isShown.fun(entry.getIdentifier());
            }
        };
        ((TableRowSorter<Perl6ProfileModel>)callsNavigation.getRowSorter()).setRowFilter(filter);
    }

    private Function<Integer, Boolean> generateVisibleCallsCondition() {
        return rowIndex -> {
            Perl6ProfileModel navigationModel = (Perl6ProfileModel)callsNavigation.getModel();
            boolean isExternalCheck = !navigationModel.isCellInternal(rowIndex, myModuleNames, myModuleBasePaths,
                                                                      (Perl6ProfilerFrameResultFilter)myHideExternalsComboBox.getSelectedItem());
            boolean patternCheck = true;

            if (!namePattern.isEmpty()) {
                Pattern pattern = Pattern.compile(".*?" + Pattern.quote(namePattern) + ".*?");
                patternCheck = pattern.matcher(navigationModel.getNodeName(rowIndex)).matches();
            }

            return isExternalCheck && patternCheck;
        };
    }

    public JPanel getPanel() {
        return myPanel1;
    }
}
