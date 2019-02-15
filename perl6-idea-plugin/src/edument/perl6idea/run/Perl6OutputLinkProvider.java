package edument.perl6idea.run;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class Perl6OutputLinkProvider implements ConsoleFilterProvider {
    @NotNull
    @Override
    public Filter[] getDefaultFilters(@NotNull Project project) {
        Perl6OutputLinkFilter filter = new Perl6OutputLinkFilter(project);
        return new Filter[]{filter};
    }
}
