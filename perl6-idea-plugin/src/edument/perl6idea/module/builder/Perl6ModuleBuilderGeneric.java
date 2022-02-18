package edument.perl6idea.module.builder;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Pair;
import edument.perl6idea.language.RakuLanguageVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Perl6ModuleBuilderGeneric {
    void setupRootModelOfPath(@NotNull ModifiableRootModel model, Path path, RakuLanguageVersion languageVersion);
    void loadFromDialogData(Map<String, String> data);

    String[] getSourceDirectories();

    default List<Pair<String, String>> getSourcePaths(@Nullable String moduleRootPath) {
        List<Pair<String, String>> paths = new ArrayList<>();
        if (moduleRootPath == null)
            return paths;
        for (String dir : getSourceDirectories())
            paths.add(new Pair<>(moduleRootPath, dir));
        return paths;
    }

    default void modifySettingsStep(SettingsStep step) {}

    default boolean shouldBeMarkedAsRoot(String directoryName) {
        return true;
    }
}
