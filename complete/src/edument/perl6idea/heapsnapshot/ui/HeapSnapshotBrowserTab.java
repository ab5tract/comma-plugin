package edument.perl6idea.heapsnapshot.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import edument.perl6idea.heapsnapshot.HeapSnapshotCollection;
import edument.perl6idea.heapsnapshot.SnapshotData;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static edument.perl6idea.heapsnapshot.SnapshotData.getTypename;

public class HeapSnapshotBrowserTab extends JPanel {
    static final Logger LOG = Logger.getInstance(HeapSnapshotBrowserTab.class);

    protected final HeapSnapshotCollection snapshotCollection;
    private final int[] indices;
    private final JTextField filterField = new JTextField();

    public HeapSnapshotBrowserTab(HeapSnapshotCollection snapshotCollection) {
        super(new MigLayout("", "[][grow, fill]", ""));
        this.snapshotCollection = snapshotCollection;
        this.indices = IntStream.range(0, snapshotCollection.snapshotList.size()).toArray();

        JPanel tableContainer = new JPanel(new MigLayout("", "[grow, fill]", ""));
        tableContainer.setBorder(JBUI.Borders.empty());
        loadSnapshotInTable(tableContainer, -1);

        add(getComboBox(tableContainer), "wrap, span 2, grow");
        add(new JLabel("Filter by name: "));
        add(filterField, "wrap");
        add(tableContainer, "grow, span 2");
    }

    private Component getComboBox(JPanel tableContainer) {
        List<String> strings = Arrays.stream(indices)
          .mapToObj(i -> "Snapshot #" + i) // TODO time it was taken?
          .collect(Collectors.toList());
        strings.add(0, "(No snapshot)");

        final int[] selectedIndex = { -1 };

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(strings.toArray(String[]::new));
        ComboBox<String> box = new ComboBox<>(model);
        box.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() != ItemEvent.SELECTED)
                    return;

                int index = box.getSelectedIndex() - 1 /* Label 0 is "(No Snapshot)", which we represent as 0 */;
                if (selectedIndex[0] != index) {
                    if (index == 0 && currentData != null)
                        currentData.forgetData();
                    selectedIndex[0] = index;
                    loadSnapshotInTable(tableContainer, index);
                }
            }
        });

        return box;
    }

    private SnapshotData currentData = null;

    private void loadSnapshotInTable(JPanel panel, int index) {
        panel.removeAll();
        if (index == -1) {
            panel.add(new JLabel("Select a snapshot"));
            updateUI();
            return;
        }

        Component loadingIcon = new JBLabel("Loading", AnimatedIcon.Default.INSTANCE, SwingConstants.LEFT);
        panel.add(loadingIcon);
        panel.updateUI();
        JSplitPane split = null;

        try {
            SnapshotData data = snapshotCollection.getSnapshotData(index);
            currentData = data;

            TabData typeToObjects = computeTypeOrFrameMap(data, SnapshotData.KIND_OBJECT);
            TabData typeToTypes = computeTypeOrFrameMap(data, SnapshotData.KIND_TYPE_OBJECT);
            TabData staticToFrames = computeTypeOrFrameMap(data, SnapshotData.KIND_FRAME);

            // Bottom part: Current object
            // (instanced first because needed in a callback)
            JPanel currentObjectPanel = new JPanel(new MigLayout("wrap 1", "[grow,fill]", ""));
            currentObjectPanel.setBorder(JBUI.Borders.empty());
            // The callback when you select an actual object
            IntConsumer selectObject = (int i) -> renderCurrentObject(data, currentObjectPanel, i);
            // Render the empty text tag for now
            selectObject.accept(-1);

            // Top part: Locating objects
            JPanel objectLocationPanel = new JPanel(new MigLayout("", "[grow,fill]", ""));
            objectLocationPanel.setBorder(JBUI.Borders.empty());

            JBTabbedPane locationTabs = new JBTabbedPane();
            locationTabs.setPreferredSize(new Dimension(400, 200));
            JPanel currentObjectLocations = new JPanel(new MigLayout("", "[grow,fill]", ""));
            currentObjectLocations.setBorder(JBUI.Borders.empty());

            // The function when you select an object location
            BiConsumer<TabData, SnapshotData.TypeOrFrameIndex> renderer = (TabData tabData, SnapshotData.TypeOrFrameIndex i) -> {
                renderCurrentObjectLocations(currentObjectLocations, selectObject, tabData.map.getOrDefault(i, null));
            };
            // Render the empty text tag for now
            renderCurrentObjectLocations(currentObjectLocations, selectObject, Collections.emptyList());

            locationTabs.addTab("Objects", renderLocationTab(typeToObjects, renderer));
            locationTabs.addTab("Types", renderLocationTab(typeToTypes, renderer));
            locationTabs.addTab("Frames", renderLocationTab(staticToFrames, renderer));

            objectLocationPanel.add(locationTabs, "grow");
            objectLocationPanel.add(currentObjectLocations, "grow");

            split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            split.setBorder(JBUI.Borders.empty());
            split.setTopComponent(objectLocationPanel);
            split.setBottomComponent(currentObjectPanel);
            panel.add(split, "grow, push");
        }
        catch (Exception e) {
            LOG.error(e);
            panel.add(new JLabel("Unable to load the requested snapshot: " + e.getMessage()));
            panel.updateUI();
        }
        finally {
            panel.remove(loadingIcon);
            panel.updateUI();
            if (split != null)
                split.setDividerLocation(0.5);
        }
    }

    private static final String[] LOCATION_COLUMNS = new String[]{
      "Name",
      "Count",
      "GC Size",
      "Unmanaged Size"
    };

    private Component renderLocationTab(TabData tabData, BiConsumer<TabData, SnapshotData.TypeOrFrameIndex> renderer) {
        Object[][] data = new Object[tabData.map.size()][LOCATION_COLUMNS.length];
        List<SnapshotData.TypeOrFrameIndex> keys = new ArrayList<>(tabData.map.keySet());
        for (int i = 0; i < keys.size(); i++) {
            SnapshotData.TypeOrFrameIndex id = keys.get(i);
            data[i][0] = getTypename(id);
            data[i][1] = tabData.map.get(id).size();
            data[i][2] = tabData.sizeByType.get(id);
            data[i][3] = tabData.unmanagedSizeByType.get(id);
        }

        DefaultTableModel model = new DefaultTableModel(data, LOCATION_COLUMNS) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return String.class;
                }
                return Integer.class;
            }
        };
        JBTable table = new JBTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                SnapshotData.TypeOrFrameIndex index = keys.get(row);
                renderer.accept(tabData, index);
            }
        });
        table.setAutoCreateRowSorter(true);
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>)table.getRowSorter();
        sorter.setRowFilter(getRowFilter());
        filterField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                sorter.setRowFilter(getRowFilter());
            }
        });
        table.setRowSorter(sorter);
        table.setDefaultEditor(Object.class, null);
        table.setShowGrid(true);
        table.getEmptyText().setText("No items");
        return new JBScrollPane(table);
    }

    @NotNull
    private RowFilter<DefaultTableModel, Integer> getRowFilter() {
        return new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                try {
                    Object value = entry.getModel().getValueAt(entry.getIdentifier(), 0);
                    if (value instanceof String) {
                        return ((String)value).contains(filterField.getText());
                    }
                } catch (Exception ignored) {
                }
                return false;
            }
        };
    }

    private void renderCurrentObjectLocations(JPanel locationsPanel,
                                              IntConsumer selectObject,
                                              @NotNull List<Integer> locations) {
        locationsPanel.removeAll();

        if (locations.isEmpty()) {
            locationsPanel.add(new JLabel("This doesn't appear anywhere"));
            return;
        }
        String[] objectNames = locations.stream().map(i -> "Object #" + i).toArray(String[]::new);
        JBList<String> list = new JBList<>(objectNames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(event -> {
            int row = event.getFirstIndex();
            selectObject.accept(locations.get(row));
        });

        JBScrollPane comp = new JBScrollPane(list);
        comp.setBorder(new TitledBorder("Instances"));
        locationsPanel.add(comp);
        locationsPanel.updateUI();
    }

    private static class TabData {
        public final HashMap<SnapshotData.TypeOrFrameIndex, List<Integer>> map;
        public final HashMap<SnapshotData.TypeOrFrameIndex, Integer> sizeByType;
        public final HashMap<SnapshotData.TypeOrFrameIndex, Integer> unmanagedSizeByType;
        public final int kind;

        private TabData(HashMap<SnapshotData.TypeOrFrameIndex, List<Integer>> map,
                        HashMap<SnapshotData.TypeOrFrameIndex, Integer> sizeByType,
                        HashMap<SnapshotData.TypeOrFrameIndex, Integer> unmanagedSizeByType,
                        int kind) {
            this.map = map;
            this.sizeByType = sizeByType;
            this.unmanagedSizeByType = unmanagedSizeByType;
            this.kind = kind;
        }

        public boolean isType() {
            return kind == SnapshotData.KIND_TYPE_OBJECT;
        }
    }

    private TabData computeTypeOrFrameMap(SnapshotData data, int kind) {
        HashMap<SnapshotData.TypeOrFrameIndex, List<Integer>> map = new HashMap<>();
        HashMap<SnapshotData.TypeOrFrameIndex, Integer> sizeByType = new HashMap<>();
        HashMap<SnapshotData.TypeOrFrameIndex, Integer> unmanagedSizeByType = new HashMap<>();
        for (int i = 0; i < data.collectableKind.length; i++) {
            if (data.collectableKind[i] == kind) {
                SnapshotData.TypeOrFrameIndex typeOrFrameIndex = data.getTypeOrFrameIndex(data.collectableTypeOrFrameIndex[i], kind);
                map.putIfAbsent(typeOrFrameIndex, new ArrayList<>());
                map.get(typeOrFrameIndex).add(i);

                int size = sizeByType.getOrDefault(typeOrFrameIndex, 0);
                size += data.collectableSize[i];
                sizeByType.put(typeOrFrameIndex, size);

                int unmanagedSize = unmanagedSizeByType.getOrDefault(typeOrFrameIndex, 0);
                unmanagedSize += data.collectableUnmanagedSize[i];
                unmanagedSizeByType.put(typeOrFrameIndex, unmanagedSize);
            }
        }
        return new TabData(map, sizeByType, unmanagedSizeByType, kind);
    }

    private void renderCurrentObject(SnapshotData data, JPanel panel, int idx) {
        panel.removeAll();

        if (idx == -1) {
            panel.add(new JLabel("No current object"));
            return;
        }

        int kind = data.collectableKind[idx];
        panel.add(new JLabel("<html><b>Object #" + idx + " of type " + data.getTypenameForObject(data, idx, kind) + "</b></html>"));
        panel.add(new JLabel(data.collectableSize[idx] + " bytes (GC) + " + data.collectableUnmanagedSize[idx] + " bytes (unmanaged)"));

        JSplitPane splitPane = new JSplitPane();
        JBScrollPane refsScroll = getReferencesComponent(data, panel, idx);
        splitPane.setLeftComponent(refsScroll);
        JComponent pathComponent = getPathComponent(data, panel, idx);
        splitPane.setRightComponent(pathComponent);
        panel.add(splitPane);
        splitPane.setBorder(JBUI.Borders.empty());
        panel.updateUI();
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.5));
    }

    @NotNull
    private JBScrollPane getReferencesComponent(SnapshotData data, JPanel panel, int idx) {
        List<SnapshotData.Reference> refs = data.getReferences(idx);
        JBList<SnapshotData.Reference> listOfRefs = new JBList<>(refs);
        listOfRefs.setCellRenderer(getReferenceRenderer(data));
        listOfRefs.addMouseListener(
          new MouseAdapter() {
              @Override
              public void mouseClicked(MouseEvent e) {
                  if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                      int row = listOfRefs.getSelectedIndex();
                      renderCurrentObject(data, panel, (int)refs.get(row).target);
                  }
              }
          }
        );
        TitledBorder title1 = BorderFactory.createTitledBorder("References");
        JBScrollPane refsScroll = new JBScrollPane(listOfRefs);
        refsScroll.setBorder(title1);
        return refsScroll;
    }

    @NotNull
    private static JBScrollPane getPathComponent(SnapshotData data, JPanel panel, int idx) {
        Deque<String> pathPieces = data.getShortestPath(idx);
        // Transform the deque
        List<String> path = new ArrayList<>();
        path.add(pathPieces.removeFirst());
        while (pathPieces.size() >= 2) {
            String route = pathPieces.removeFirst();
            String target = pathPieces.removeFirst();
            path.add(String.format("  --[ %s ]--> %s", route, target));
        }
        JBList<String> listOfPath = new JBList<>(path);
        TitledBorder title2 = BorderFactory.createTitledBorder("Shortest Path");
        JBScrollPane pathScroll = new JBScrollPane(listOfPath);
        pathScroll.setBorder(title2);
        return pathScroll;
    }

    @NotNull
    private DefaultListCellRenderer getReferenceRenderer(SnapshotData data) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SnapshotData.Reference) {
                    String targetDescr = data.describeCol((int)((SnapshotData.Reference)value).target);
                    switch (((SnapshotData.Reference)value).kind) {
                        case 2: { // string
                            setText(
                              snapshotCollection.stringData.strings.get(((SnapshotData.Reference)value).index) + ": '" + targetDescr + "'");
                            break;
                        }
                        case 1: { // index
                            setText("Index " + ((SnapshotData.Reference)value).index + ": '" + targetDescr + "'");
                            break;
                        }
                        default: { // 0 is unknown
                            setText(targetDescr);
                        }
                    }
                }
                return component;
            }
        };
    }
}
