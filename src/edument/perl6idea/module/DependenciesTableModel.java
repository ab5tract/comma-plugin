package edument.perl6idea.module;

import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ItemRemovable;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class DependenciesTableModel extends ListTableModel<Perl6DependencyTableItem> implements ItemRemovable {
    static final String SCOPE_COLUMN_NAME = "Scope";
    private static final ColumnInfo<Perl6DependencyTableItem, Perl6DependencyScope> SCOPE_COLUMN_INFO =
      new ColumnInfo<Perl6DependencyTableItem, Perl6DependencyScope>(SCOPE_COLUMN_NAME) {
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
    private ModuleConfigurationState myState;
    private Set<Perl6DependencyTableItem> myInitialSet = new HashSet<>();
    private String myMetaPath;

    public DependenciesTableModel(ModuleConfigurationState state) {
        super(new Perl6DependencyTableItemColumnInfo(), SCOPE_COLUMN_INFO);
        myState = state;
        init();
    }

    public void clear() {
        setItems(Collections.emptyList());
    }

    public void init() {
        if (myMetaPath == null)
            myMetaPath = getModel().getModule().getModuleFilePath();
        JSONObject parsedJson = Perl6ModuleBuilder.getMetaJsonFromModulePath(myMetaPath);
        if (parsedJson == null) return;
        JSONArray depends = getJsonField(parsedJson, "depends");
        JSONArray test_depends = getJsonField(parsedJson,"test-depends");
        JSONArray build_depends = getJsonField(parsedJson,"build-depends");

        List<Perl6DependencyTableItem> items = new ArrayList<>();

        for (int i = 0; i < depends.length(); i++) {
            items.add(new Perl6DependencyTableItem(depends.getString(i), Perl6DependencyScope.DEPENDS));
            myInitialSet.add(new Perl6DependencyTableItem(depends.getString(i), Perl6DependencyScope.DEPENDS));
        }
        for (int i = 0; i < test_depends.length(); i++) {
            items.add(new Perl6DependencyTableItem(test_depends.getString(i), Perl6DependencyScope.TEST_DEPENDS));
            myInitialSet.add(new Perl6DependencyTableItem(test_depends.getString(i), Perl6DependencyScope.TEST_DEPENDS));
        }
        for (int i = 0; i < build_depends.length(); i++) {
            items.add(new Perl6DependencyTableItem(build_depends.getString(i), Perl6DependencyScope.BUILD_DEPENDS));
            myInitialSet.add(new Perl6DependencyTableItem(build_depends.getString(i), Perl6DependencyScope.BUILD_DEPENDS));
        }

        setItems(items);
    }

    public void saveState() {
        myInitialSet = new HashSet<>();
        init();
    }

    public boolean isModified() {
        return !(myInitialSet.containsAll(getItems()) && getItems().containsAll(myInitialSet));
    }

    private static JSONArray getJsonField(JSONObject jsonString, String key) {
        try {
            return jsonString.getJSONArray(key);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    private ModuleRootModel getModel() {
        return myState.getRootModel();
    }

    private static class Perl6DependencyTableItemColumnInfo extends ColumnInfo<Perl6DependencyTableItem, Perl6DependencyTableItem> {
        public Perl6DependencyTableItemColumnInfo() {
            super("");
        }

        @Nullable
        @Override
        public Perl6DependencyTableItem valueOf(Perl6DependencyTableItem item) {
            return item;
        }
    }
}
