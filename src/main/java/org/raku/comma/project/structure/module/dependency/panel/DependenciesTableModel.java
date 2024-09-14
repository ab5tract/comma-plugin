package org.raku.comma.project.structure.module.dependency.panel;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ItemRemovable;
import com.intellij.util.ui.ListTableModel;
import org.raku.comma.metadata.RakuMetaDataComponent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DependenciesTableModel extends ListTableModel<RakuDependencyTableItem> implements ItemRemovable {
    static final String SCOPE_COLUMN_NAME = "Scope";
    private static final ColumnInfo<RakuDependencyTableItem, RakuDependencyScope> SCOPE_COLUMN_INFO =
        new ColumnInfo<>(SCOPE_COLUMN_NAME) {
            @Nullable
            @Override
            public RakuDependencyScope valueOf(RakuDependencyTableItem item) {
                return item.getScope();
            }

            @Override
            public void setValue(RakuDependencyTableItem item, RakuDependencyScope value) {
                item.setScope(value);
            }

            @Override
            public boolean isCellEditable(RakuDependencyTableItem item) {
                return true;
            }

            @Override
            public Class<?> getColumnClass() {
                return RakuDependencyScope.class;
            }
        };
    private final ModuleConfigurationState myState;
    private Set<RakuDependencyTableItem> myInitialSet = new HashSet<>();

    public DependenciesTableModel(ModuleConfigurationState state) {
        super(new RakuDependencyTableItemColumnInfo(), SCOPE_COLUMN_INFO);
        myState = state;
        init();
    }

    public void clear() {
        setItems(Collections.emptyList());
    }

    public void init() {
        Module module = myState.getCurrentRootModel().getModule();
        RakuMetaDataComponent metaData = module.getService(RakuMetaDataComponent.class);
        List<String> depends = metaData.getDepends(false);
        List<String> testDepends = metaData.getTestDepends(false);
        List<String> buildDepends = metaData.getBuildDepends(false);

        List<RakuDependencyTableItem> items = new ArrayList<>();

        for (String depend : depends) {
            items.add(new RakuDependencyTableItem(depend, RakuDependencyScope.DEPENDS));
            myInitialSet.add(new RakuDependencyTableItem(depend, RakuDependencyScope.DEPENDS));
        }
        for (String testDepend : testDepends) {
            items.add(new RakuDependencyTableItem(testDepend, RakuDependencyScope.TEST_DEPENDS));
            myInitialSet.add(new RakuDependencyTableItem(testDepend, RakuDependencyScope.TEST_DEPENDS));
        }
        for (String buildDepend : buildDepends) {
            items.add(new RakuDependencyTableItem(buildDepend, RakuDependencyScope.BUILD_DEPENDS));
            myInitialSet.add(new RakuDependencyTableItem(buildDepend, RakuDependencyScope.BUILD_DEPENDS));
        }

        setItems(items);
    }

    public void saveState() {
        myInitialSet = new HashSet<>();
        init();
    }

    public ModuleConfigurationState getState() {
        return myState;
    }

    public boolean isModified() {
        return !(myInitialSet.containsAll(getItems()) && new HashSet<>(getItems()).containsAll(myInitialSet));
    }

    private static class RakuDependencyTableItemColumnInfo extends ColumnInfo<RakuDependencyTableItem, RakuDependencyTableItem> {
        RakuDependencyTableItemColumnInfo() {
            super("");
        }

        @Nullable
        @Override
        public RakuDependencyTableItem valueOf(RakuDependencyTableItem item) {
            return item;
        }
    }
}
