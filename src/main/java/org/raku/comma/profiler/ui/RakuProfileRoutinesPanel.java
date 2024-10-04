package org.raku.comma.profiler.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
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
import com.intellij.psi.stubs.StubIndex;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Function;
import com.intellij.util.PlatformIcons;
import org.raku.comma.metadata.RakuMetaDataComponent;
import org.raku.comma.module.RakuModuleType;
import org.raku.comma.profiler.model.*;
import org.raku.comma.psi.RakuFile;
import org.raku.comma.psi.stub.index.ProjectModulesStubIndex;
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
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class RakuProfileRoutinesPanel extends JPanel {
    public static final Logger LOG = Logger.getInstance(RakuProfileRoutinesPanel.class);
    protected Project myProject;
    protected RakuProfileData myProfileData;
    protected List<String> myModuleNames;
    protected List<String> myModuleBasePaths;
    private JPanel myPanel1;
    private ComboBox<RakuProfilerFrameResultFilter> myHideExternalsComboBox;
    private JBTable callsNavigation;
    private JBTable callerTable;
    private JBTable calleeTable;
    private JCheckBox myShowRealNamesCheckBox;
    private String namePattern = "";
    private JTextField myFilterByNameTextField;
    private JSeparator separator1;
    private JSeparator separator2;
    private final RakuProfileNodeRenderer myProfileNodeRenderer;

    public RakuProfileRoutinesPanel(Project project, RakuProfileData profileData) {
        myProject = project;
        myProfileData = profileData;
        // Default renderer
        fillModuleData(myProject);
        myProfileNodeRenderer = new RakuProfileNodeRenderer(myModuleBasePaths, myModuleNames, myHideExternalsComboBox);
        myHideExternalsComboBox.addItem(RakuProfilerFrameResultFilter.Everything);
        myHideExternalsComboBox.addItem(RakuProfilerFrameResultFilter.NoExternals);
        myHideExternalsComboBox.addItem(RakuProfilerFrameResultFilter.NoCore);
        myHideExternalsComboBox.setSelectedItem(RakuProfilerFrameResultFilter.NoCore);
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
                e -> Objects.nonNull(e.getFile()) ? e.getFile().getPath() : null
            ).filter(Objects::nonNull).toList());

            var moduleType = ModuleType.get(module);
            if (! moduleType.equals(RakuModuleType.getInstance())) continue;
            RakuMetaDataComponent metaDataComponent = module.getService(RakuMetaDataComponent.class);
            if (Objects.isNull(metaDataComponent)) continue;
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
        RakuProfileModel model = (RakuProfileModel)table.getModel();
        model.setShowRealFileNames(value);
        table.updateUI();
    }

    private void updateCallData() {
        int row = callsNavigation.getSelectedRow();
        if (row < 0)
            return;
        int callRow = callsNavigation.convertRowIndexToModel(row);
        if (callRow >= 0) {
            RakuProfileModel navigationModel = (RakuProfileModel)callsNavigation.getModel();
            int callId = navigationModel.getNodeId(callRow);
            updateCalleeTable(callId);
            updateCallerTable(callId);
        }
    }

    private void updateCalleeTable(int callId) {
        List<RakuProfileCall> calleeList = myProfileData.getCalleeListByCallId(callId);
        calleeTable.setModel(new RakuProfileModel(myProject, calleeList));
        TableColumnModel columnModel = calleeTable.getColumnModel();
        columnModel.getColumn(0).setCellRenderer(myProfileNodeRenderer);
        columnModel.getColumn(1).setCellRenderer(myProfileNodeRenderer);
        columnModel.getColumn(2).setCellRenderer(new PercentageTableCellRenderer());
        columnModel.getColumn(3).setCellRenderer(myProfileNodeRenderer);
    }

    private void updateCallerTable(int callId) {
        List<RakuProfileCall> callerList = myProfileData.getCallerListByCallId(callId);
        callerTable.setModel(new RakuProfileModel(myProject, callerList));
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
                    RakuProfileModel relatedCallsTableModel = (RakuProfileModel)table.getModel();
                    int callNodeId = relatedCallsTableModel.getNodeId(relatedCallTableRow);
                    // To jump, we need not a routine id, but its position in the navigation table
                    RakuProfileModel navigationModel = (RakuProfileModel)callsNavigation.getModel();
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
        List<RakuProfileCall> calls;
        try {
            calls = myProfileData.getNavigationNodes();
        }
        catch (SQLException e) {
            LOG.warn("Could not build a list of calls: " + e.getMessage());
            return;
        }
        // Setup a model
        RakuProfileNavigationModel model = new RakuProfileNavigationModel(myProject, calls);
        callsNavigation.setModel(model);
        // Single selection + default sort for all columns
        callsNavigation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        callsNavigation.setRowSorter(new RakuProfileOutputTableRowSorter(model));
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
        RakuProfileModel model = (RakuProfileModel)callsNavigation.getModel();
        if (!model.isCellInternal(row, myModuleNames, myModuleBasePaths,
                                  (RakuProfilerFrameResultFilter)myHideExternalsComboBox.getSelectedItem())) {
            String sourceFilePath = model.getNodeSourceFile(row);
            VirtualFile file = null;
            if (sourceFilePath.startsWith("site#")) {
                String[] pathAndModule = sourceFilePath.split(" ");
                if (pathAndModule.length == 2) {
                    String moduleNameKey = pathAndModule[1].substring(1, pathAndModule[1].length() - 1);
                    var index = ProjectModulesStubIndex.getInstance();
                    Collection<RakuFile> indexedFile =
                            StubIndex.getElements(index.getKey(),
                                                  moduleNameKey,
                                                  myProject,
                                                  GlobalSearchScope.allScope(myProject),
                                                  RakuFile.class);
                    if (!indexedFile.isEmpty())
                        file = indexedFile.iterator().next().getVirtualFile();
                }
            }
            if (file == null)
                file = LocalFileSystem.getInstance().findFileByPath(sourceFilePath);
            if (file != null) {
                PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);
                if (!(psiFile instanceof RakuFile))
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

    @SuppressWarnings({ "unchecked" })
    private void updateRowFilter() {
        Function<Integer, Boolean> isShown = generateVisibleCallsCondition();
        RowFilter<RakuProfileModel, Integer> filter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends RakuProfileModel, ? extends Integer> entry) {
                return isShown.fun(entry.getIdentifier());
            }
        };
        ((TableRowSorter<RakuProfileModel>) callsNavigation.getRowSorter()).setRowFilter(filter);
    }

    private Function<Integer, Boolean> generateVisibleCallsCondition() {
        return rowIndex -> {
            RakuProfileModel navigationModel = (RakuProfileModel)callsNavigation.getModel();
            boolean isExternalCheck = !navigationModel.isCellInternal(rowIndex, myModuleNames, myModuleBasePaths,
                                                                      (RakuProfilerFrameResultFilter)myHideExternalsComboBox.getSelectedItem());
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
