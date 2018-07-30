package edument.perl6idea.contribution;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import edument.perl6idea.psi.stub.index.Perl6LexicalTypeStubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Perl6ClassNameContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        List<String> result = new ArrayList<>();
        result.addAll(Perl6GlobalTypeStubIndex.getInstance().getAllKeys(project));
        result.addAll(Perl6LexicalTypeStubIndex.getInstance().getAllKeys(project));
        return result.toArray(ArrayUtil.EMPTY_STRING_ARRAY);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        List<NavigationItem> results = new ArrayList<>();

        Perl6GlobalTypeStubIndex globalIndex = Perl6GlobalTypeStubIndex.getInstance();
        for (String globalType : Filtering.typeMatch(globalIndex.getAllKeys(project), pattern))
            results.addAll(globalIndex.get(globalType, project, GlobalSearchScope.projectScope(project)));

        Perl6LexicalTypeStubIndex lexicalIndex = Perl6LexicalTypeStubIndex.getInstance();
        for (String lexicalType : Filtering.typeMatch(lexicalIndex.getAllKeys(project), pattern))
            results.addAll(lexicalIndex.get(lexicalType, project, GlobalSearchScope.projectScope(project)));

        return results.toArray(NavigationItem.EMPTY_NAVIGATION_ITEM_ARRAY);
    }
}
