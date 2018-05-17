package edument.perl6idea.contribution;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Perl6ClassNameContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        return Perl6GlobalTypeStubIndex.getInstance().getAllKeys(project).toArray(new String[0]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        Perl6GlobalTypeStubIndex index = Perl6GlobalTypeStubIndex.getInstance();
        List<NavigationItem> results = new ArrayList<>();
        for (String globalType : index.getAllKeys(project))
            results.addAll(index.get(globalType, project, GlobalSearchScope.projectScope(project)));
        return results.toArray(new NavigationItem[0]);
    }
}
