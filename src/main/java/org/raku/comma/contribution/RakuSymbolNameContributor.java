package org.raku.comma.contribution;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import org.raku.comma.extensions.RakuFrameworkCall;
import org.raku.comma.psi.stub.index.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@InternalIgnoreDependencyViolation
public class RakuSymbolNameContributor implements ChooseByNameContributor {
    @Override
    public String @NotNull [] getNames(Project project, boolean includeNonProjectItems) {
        List<String> result = new ArrayList<>();
        result.addAll(RakuGlobalTypeStubIndex.getInstance().getAllKeys(project));
        result.addAll(RakuLexicalTypeStubIndex.getInstance().getAllKeys(project));
        result.addAll(RakuAllRoutinesStubIndex.getInstance().getAllKeys(project));
        result.addAll(RakuAllRegexesStubIndex.getInstance().getAllKeys(project));
        result.addAll(RakuAllAttributesStubIndex.getInstance().getAllKeys(project));
        result.addAll(RakuAllConstantsStubIndex.getInstance().getAllKeys(project));

        RakuFrameworkCall[] extensions = RakuFrameworkCall.EP_NAME.getExtensions();
        for (RakuFrameworkCall ext : extensions)
            ext.contributeSymbolNames(project, result);

        return ArrayUtil.toStringArray(result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {

        List<NavigationItem> results = new ArrayList<>();

        Stream.of(
            RakuGlobalTypeStubIndex.getInstance(),
            RakuLexicalTypeStubIndex.getInstance(),
            RakuAllRoutinesStubIndex.getInstance(),
            RakuAllRegexesStubIndex.getInstance(),
            RakuAllAttributesStubIndex.getInstance(),
            RakuAllConstantsStubIndex.getInstance()
        ).forEach(index -> results.addAll((List<NavigationItem>) fillItems(index, pattern, project)));

        RakuFrameworkCall[] extensions = RakuFrameworkCall.EP_NAME.getExtensions();
        for (RakuFrameworkCall ext : extensions)
            ext.contributeSymbolItems(project, pattern, results);

        return results.toArray(NavigationItem.EMPTY_NAVIGATION_ITEM_ARRAY);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List fillItems(StringStubIndexExtension index, String pattern, Project project) {
        return Filtering.typeMatch(index.getAllKeys(project), pattern ).stream().map(item ->
            StubIndex.getElements(index.getKey(), item, project, GlobalSearchScope.projectScope(project), RakuIndexableType.class)
        ).toList();
    }
}
