package org.raku.comma.rename;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuInputValidator implements NamesValidator {
    @Override
    public boolean isKeyword(@NotNull String name, Project project) {
        return false;
    }

    @Override
    public boolean isIdentifier(@NotNull String name, Project project) {
        return !name.endsWith("-") && !name.startsWith("-") && !(Character.isDigit(name.charAt(0)));
    }
}
