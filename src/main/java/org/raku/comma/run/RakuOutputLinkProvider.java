package org.raku.comma.run;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuOutputLinkProvider implements ConsoleFilterProvider {
    @Override
    public Filter @NotNull [] getDefaultFilters(@NotNull Project project) {
        RakuOutputLinkFilter filter = new RakuOutputLinkFilter(project);
        return new Filter[]{filter};
    }
}
