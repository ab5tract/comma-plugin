package edument.perl6idea.module.builder;

import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface Perl6ModuleBuilderGeneric {
    void setupRootModel(@NotNull ModifiableRootModel model);

    String[] getSourceDirectories();

    default List<Pair<String, String>> getSourcePaths(@Nullable String moduleRootPath) {
        List<Pair<String, String>> paths = new ArrayList<>();
        if (moduleRootPath == null)
            return paths;
        for (String dir : getSourceDirectories())
            paths.add(new Pair<>(moduleRootPath, dir));
        return paths;
    }
}
