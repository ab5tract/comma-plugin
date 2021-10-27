package edument.perl6idea.heapsnapshot.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import edument.perl6idea.heapsnapshot.HeapSnapshotCollection;
import edument.perl6idea.heapsnapshot.SnapshotData;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HeapSnapshotBrowserTab extends JPanel {
    static final Logger LOG = Logger.getInstance(HeapSnapshotBrowserTab.class);

    private final HeapSnapshotCollection snapshotCollection;
    private int[] indices;

    public static final int KIND_OBJECT = 1;
    public static final int KIND_TYPE_OBJECT = 2;
    public static final int KIND_FRAME = 4;

    /**
     * Wrap typeOrFrameIndex in a data class so we're never confused
     *  if someone is an object ID or a staticFrameData/typeData ID.
     */
    class TypeOrFrameIndex {
        public final int index;
        public final int kind;

        TypeOrFrameIndex(int index, int kind) {
            this.index = index;
            this.kind = kind;
        }

        @Nullable
        public String fetch() {
            return snapshotCollection.stringData.strings.get(fetchIndex());
        }

        public int fetchIndex() {
            return isFrame()
                   ? snapshotCollection.staticFrameData.nameIndices[index]
                   : snapshotCollection.typeData.typenameIndices[index];
        }

        public boolean isFrame() {
            return kind == KIND_FRAME;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeOrFrameIndex other = (TypeOrFrameIndex)o;
            return index == other.index &&
                   kind == other.kind;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, kind);
        }
    }

    public HeapSnapshotBrowserTab(HeapSnapshotCollection snapshotCollection) {
        super(new MigLayout("wrap 1"));
        this.snapshotCollection = snapshotCollection;
        this.indices = IntStream.range(0, snapshotCollection.snapshotList.size()).toArray();

        JPanel tableContainer = new JPanel(new MigLayout("wrap 1"));
        loadSnapshotInTable(tableContainer, -1);

        add(getComboBox(tableContainer));
        add(tableContainer);
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
            return;
        }

        Component loadingIcon = new JBLabel("Loading", AnimatedIcon.Default.INSTANCE, SwingConstants.LEFT);
        panel.add(loadingIcon);

        try {
            SnapshotData data = snapshotCollection.getSnapshotData(index);

            // TODO factor this out
            if (currentData != null)
                currentData.forgetData();
            currentData = data;

            TabData typeToObjects = computeTypeOrFrameMap(data, KIND_OBJECT);
            TabData typeToTypes = computeTypeOrFrameMap(data, KIND_TYPE_OBJECT);
            TabData staticToFrames = computeTypeOrFrameMap(data, KIND_FRAME);

            // Bottom part: Current object
            // (instanced first because needed in a callback)
            JPanel currentObjectPanel = new JPanel(new MigLayout("wrap 1"));
            // The callback when you select an actual object
            IntConsumer selectObject = (int i) -> renderCurrentObject(data, currentObjectPanel, i);
            // Render the empty text tag for now
            selectObject.accept(-1);

            // Top part: Locating objects
            JPanel objectLocationPanel = new JPanel(new MigLayout(""));

            JBTabbedPane locationTabs = new JBTabbedPane();
            locationTabs.setPreferredSize(new Dimension(400, 200));
            JPanel currentObjectLocations = new JPanel(new MigLayout());

            // The function when you select an object location
            BiConsumer<TabData, TypeOrFrameIndex> renderer = (TabData tabData, TypeOrFrameIndex i) -> {
               renderCurrentObjectLocations(currentObjectLocations, selectObject, tabData.map.getOrDefault(i, null));
            };
            // Render the empty text tag for now
            renderCurrentObjectLocations(currentObjectLocations, selectObject, Collections.emptyList());

            locationTabs.addTab("Objects", renderLocationTab(typeToObjects, renderer));
            locationTabs.addTab("Types", renderLocationTab(typeToTypes, renderer));
            locationTabs.addTab("Static Frames", renderLocationTab(staticToFrames, renderer));

            objectLocationPanel.add(locationTabs, "w 200:400:400,h 200:400:400");
            objectLocationPanel.add(new JBScrollPane(currentObjectLocations), "h 200:400:400");

            panel.add(objectLocationPanel);
            panel.add(currentObjectPanel);
        } catch (Exception e) {
            LOG.error(e);
            panel.add(new JLabel("Unable to load the requested snapshot: " + e.getMessage()));
        } finally {
            panel.remove(loadingIcon);
        }
    }

    private static final String[] LOCATION_COLUMNS = new String[] {
        "Name",
        "Count",
        "GC Size",
        "Unmanaged Size"
    };

    private Component renderLocationTab(TabData tabData, BiConsumer<TabData, TypeOrFrameIndex> renderer) {
        Object[][] data = new Object[tabData.map.size()][LOCATION_COLUMNS.length];
        ArrayList<TypeOrFrameIndex> keys = new ArrayList<>(tabData.map.keySet());
        for (int i = 0; i < keys.size(); i++) {
            TypeOrFrameIndex id = keys.get(i);
            data[i][0] = getTypename(id);
            data[i][1] = tabData.map.get(id).size();
            data[i][2] = tabData.sizeByType.get(id);
            data[i][3] = tabData.unmanagedSizeByType.get(id);
        }

        JBTable table = new JBTable(new DefaultTableModel(data, LOCATION_COLUMNS));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                TypeOrFrameIndex index = keys.get(row);
                renderer.accept(tabData, index);
            }
        });
        table.setDefaultEditor(Object.class, null);
        table.setShowGrid(true);
        table.getEmptyText().setText("No items");
        return table;
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

        locationsPanel.add(list);
    }

    private static class TabData {
        public final HashMap<TypeOrFrameIndex, List<Integer>> map;
        public final HashMap<TypeOrFrameIndex, Integer> sizeByType;
        public final HashMap<TypeOrFrameIndex, Integer> unmanagedSizeByType;
        public final int kind;

        private TabData(HashMap<TypeOrFrameIndex, List<Integer>> map,
                        HashMap<TypeOrFrameIndex, Integer> sizeByType,
                        HashMap<TypeOrFrameIndex, Integer> unmanagedSizeByType,
                        int kind) {
            this.map = map;
            this.sizeByType = sizeByType;
            this.unmanagedSizeByType = unmanagedSizeByType;
            this.kind = kind;
        }

        public boolean isType() {
            return kind == KIND_TYPE_OBJECT;
        }
    }

    private TabData computeTypeOrFrameMap(SnapshotData data, int kind) {
        HashMap<TypeOrFrameIndex, List<Integer>> map = new HashMap<>();
        HashMap<TypeOrFrameIndex, Integer> sizeByType = new HashMap<>();
        HashMap<TypeOrFrameIndex, Integer> unmanagedSizeByType = new HashMap<>();
        for (int i = 0; i < data.collectableKind.length; i++) {
            if (data.collectableKind[i] == kind) {
                TypeOrFrameIndex typeOrFrameIndex = getTypeOrFrameIndex(data.collectableTypeOrFrameIndex[i], kind);
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

    private void renderCurrentObject(SnapshotData data, JPanel panel, int i) {
        panel.removeAll();

        if (i == -1) {
          panel.add(new JLabel("No current object"));
          return;
        }

        int kind = data.collectableKind[i];
        panel.add(new JLabel("Object #" + i + " of type " + getTypenameForObject(data, i, kind)));
        panel.add(new JLabel(data.collectableSize[i] + " bytes (GC) + " + data.collectableUnmanagedSize[i] + " bytes (unmanaged)"));
        // TODO...
    }

    private String getTypenameForObject(SnapshotData data, int i, int kind) {
        return getTypename(getTypeOrFrameIndex(data.collectableTypeOrFrameIndex[i], kind));
    }

    private TypeOrFrameIndex getTypeOrFrameIndex(int i, int kind) {
        return new TypeOrFrameIndex(i, kind);
    }

    @NotNull
    private String getTypename(TypeOrFrameIndex id) {
        String entry = id.fetch();
        return (entry == null ? "?" : entry) + "#" + id.index;
    }
}
