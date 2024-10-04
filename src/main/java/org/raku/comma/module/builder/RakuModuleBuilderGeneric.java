package org.raku.comma.module.builder;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Pair;
import org.raku.comma.language.RakuLanguageVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface RakuModuleBuilderGeneric {
    void setupRootModelOfPath(@NotNull ModifiableRootModel model, Path path, RakuLanguageVersion languageVersion);
    default void loadFromDialogData(Map<String, String> data) {}

    String[] getSourceDirectories();

    default List<Pair<String, String>> getSourcePaths(@Nullable String moduleRootPath) {
        List<Pair<String, String>> paths = new ArrayList<>();
        if (moduleRootPath == null) {
            return paths;
        }
        for (String dir : getSourceDirectories()) {
            paths.add(new Pair<>(moduleRootPath, dir));
        }
        return paths;
    }

    void setName(@NotNull String name);
    default void setEntryPoint(@NotNull String entryPoint) {}

    default void modifySettingsStep(SettingsStep step) {}

    default boolean shouldBeMarkedAsRoot(String directoryName) {
        return true;
    }
}
