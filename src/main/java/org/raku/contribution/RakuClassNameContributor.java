package org.raku.contribution;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import org.raku.psi.stub.index.RakuGlobalTypeStubIndex;
import org.raku.psi.stub.index.RakuIndexableType;
import org.raku.psi.stub.index.RakuLexicalTypeStubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@InternalIgnoreDependencyViolation
public class RakuClassNameContributor implements ChooseByNameContributor {
    @Override
    public String @NotNull [] getNames(Project project, boolean includeNonProjectItems) {
        List<String> result = new ArrayList<>();
        result.addAll(RakuGlobalTypeStubIndex.getInstance().getAllKeys(project));
        result.addAll(RakuLexicalTypeStubIndex.getInstance().getAllKeys(project));
        return ArrayUtil.toStringArray(result);
    }

    @Override
    public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        List<NavigationItem> results = new ArrayList<>();

        RakuGlobalTypeStubIndex globalIndex = RakuGlobalTypeStubIndex.getInstance();
        Filtering.typeMatch(globalIndex.getAllKeys(project), pattern).forEach(globalType ->
            results.addAll(StubIndex.getElements(globalIndex.getKey(),
                                                 globalType,
                                                 project,
                                                 GlobalSearchScope.projectScope(project),
                                                 RakuIndexableType.class))
        );

        RakuLexicalTypeStubIndex lexicalIndex = RakuLexicalTypeStubIndex.getInstance();
        Filtering.typeMatch(lexicalIndex.getAllKeys(project), pattern).forEach(lexicalType ->
            results.addAll(StubIndex.getElements(lexicalIndex.getKey(),
                                                 lexicalType,
                                                 project,
                                                 GlobalSearchScope.projectScope(project),
                                                 RakuIndexableType.class))
        );

        return results.toArray(NavigationItem.EMPTY_NAVIGATION_ITEM_ARRAY);
    }
}
