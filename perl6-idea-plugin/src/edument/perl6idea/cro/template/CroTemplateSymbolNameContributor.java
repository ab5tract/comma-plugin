package edument.perl6idea.cro.template;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.contribution.Filtering;
import edument.perl6idea.cro.template.psi.stub.index.CroTemplateMacroIndex;
import edument.perl6idea.cro.template.psi.stub.index.CroTemplateSubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@InternalIgnoreDependencyViolation
public class CroTemplateSymbolNameContributor implements ChooseByNameContributor {
    @Override
    public String @NotNull [] getNames(Project project, boolean includeNonProjectItems) {
        List<String> result = new ArrayList<>();
        result.addAll(CroTemplateMacroIndex.getInstance().getAllKeys(project));
        result.addAll(CroTemplateSubIndex.getInstance().getAllKeys(project));
        return ArrayUtil.toStringArray(result);
    }

    @Override
    public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        List<NavigationItem> results = new ArrayList<>();

        CroTemplateMacroIndex macroIndex = CroTemplateMacroIndex.getInstance();
        for (String macroName : Filtering.typeMatch(macroIndex.getAllKeys(project), pattern))
            results.addAll(macroIndex.get(macroName, project, GlobalSearchScope.projectScope(project)));

        CroTemplateSubIndex subIndex = CroTemplateSubIndex.getInstance();
        for (String subName : Filtering.typeMatch(subIndex.getAllKeys(project), pattern))
            results.addAll(subIndex.get(subName, project, GlobalSearchScope.projectScope(project)));

        return results.toArray(NavigationItem.EMPTY_NAVIGATION_ITEM_ARRAY);
    }
}
