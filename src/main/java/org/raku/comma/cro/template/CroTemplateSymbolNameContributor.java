package org.raku.comma.cro.template;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import org.raku.comma.contribution.Filtering;
import org.raku.comma.cro.template.psi.CroTemplateMacro;
import org.raku.comma.cro.template.psi.CroTemplateSub;
import org.raku.comma.cro.template.psi.stub.index.CroTemplateMacroIndex;
import org.raku.comma.cro.template.psi.stub.index.CroTemplateSubIndex;
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
        Filtering.typeMatch(macroIndex.getAllKeys(project), pattern).forEach(
            macroName -> results.addAll(
                StubIndex.getElements(macroIndex.getKey(),
                                      macroName,
                                      project,
                                      GlobalSearchScope.projectScope(project),
                                      CroTemplateMacro.class)
            ));

        CroTemplateSubIndex subIndex = CroTemplateSubIndex.getInstance();
        Filtering.typeMatch(subIndex.getAllKeys(project), pattern).forEach(
            subName -> results.addAll(
                StubIndex.getElements(subIndex.getKey(),
                                      subName,
                                      project,
                                      GlobalSearchScope.projectScope(project),
                                      CroTemplateSub.class)
            ));

        return results.toArray(NavigationItem.EMPTY_NAVIGATION_ITEM_ARRAY);
    }
}
