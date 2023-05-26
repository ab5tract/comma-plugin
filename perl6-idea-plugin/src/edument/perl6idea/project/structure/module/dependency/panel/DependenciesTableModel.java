package edument.perl6idea.project.structure.module.dependency.panel;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ItemRemovable;
import com.intellij.util.ui.ListTableModel;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DependenciesTableModel extends ListTableModel<Perl6DependencyTableItem> implements ItemRemovable {
    static final String SCOPE_COLUMN_NAME = "Scope";
    private static final ColumnInfo<Perl6DependencyTableItem, Perl6DependencyScope> SCOPE_COLUMN_INFO =
        new ColumnInfo<>(SCOPE_COLUMN_NAME) {
            @Nullable
            @Override
            public Perl6DependencyScope valueOf(Perl6DependencyTableItem item) {
                return item.getScope();
            }

            @Override
            public void setValue(Perl6DependencyTableItem item, Perl6DependencyScope value) {
                item.setScope(value);
            }

            @Override
            public boolean isCellEditable(Perl6DependencyTableItem item) {
                return true;
            }

            @Override
            public Class<?> getColumnClass() {
                return Perl6DependencyScope.class;
            }
        };
    private final ModuleConfigurationState myState;
    private Set<Perl6DependencyTableItem> myInitialSet = new HashSet<>();

    public DependenciesTableModel(ModuleConfigurationState state) {
        super(new Perl6DependencyTableItemColumnInfo(), SCOPE_COLUMN_INFO);
        myState = state;
        init();
    }

    public void clear() {
        setItems(Collections.emptyList());
    }

    public void init() {
        Module module = myState.getCurrentRootModel().getModule();
        Perl6MetaDataComponent metaData = module.getService(Perl6MetaDataComponent.class);
        List<String> depends = metaData.getDepends(false);
        List<String> testDepends = metaData.getTestDepends(false);
        List<String> buildDepends = metaData.getBuildDepends(false);

        List<Perl6DependencyTableItem> items = new ArrayList<>();

        for (String depend : depends) {
            items.add(new Perl6DependencyTableItem(depend, Perl6DependencyScope.DEPENDS));
            myInitialSet.add(new Perl6DependencyTableItem(depend, Perl6DependencyScope.DEPENDS));
        }
        for (String testDepend : testDepends) {
            items.add(new Perl6DependencyTableItem(testDepend, Perl6DependencyScope.TEST_DEPENDS));
            myInitialSet.add(new Perl6DependencyTableItem(testDepend, Perl6DependencyScope.TEST_DEPENDS));
        }
        for (String buildDepend : buildDepends) {
            items.add(new Perl6DependencyTableItem(buildDepend, Perl6DependencyScope.BUILD_DEPENDS));
            myInitialSet.add(new Perl6DependencyTableItem(buildDepend, Perl6DependencyScope.BUILD_DEPENDS));
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

    private static class Perl6DependencyTableItemColumnInfo extends ColumnInfo<Perl6DependencyTableItem, Perl6DependencyTableItem> {
        Perl6DependencyTableItemColumnInfo() {
            super("");
        }

        @Nullable
        @Override
        public Perl6DependencyTableItem valueOf(Perl6DependencyTableItem item) {
            return item;
        }
    }
}
