package edument.perl6idea.contribution;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import edument.perl6idea.psi.stub.index.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Perl6SymbolNameContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        List<String> result = new ArrayList<>();
        result.addAll(Perl6GlobalTypeStubIndex.getInstance().getAllKeys(project));
        result.addAll(Perl6LexicalTypeStubIndex.getInstance().getAllKeys(project));
        result.addAll(Perl6AllRoutinesStubIndex.getInstance().getAllKeys(project));
        result.addAll(Perl6AllRegexesStubIndex.getInstance().getAllKeys(project));
        result.addAll(Perl6AllAttributesStubIndex.getInstance().getAllKeys(project));
        result.addAll(Perl6AllConstantsStubIndex.getInstance().getAllKeys(project));
        return result.toArray(new String[0]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        List<NavigationItem> results = new ArrayList<>();

        Perl6GlobalTypeStubIndex globalIndex = Perl6GlobalTypeStubIndex.getInstance();
        for (String globalType : globalIndex.getAllKeys(project))
            results.addAll(globalIndex.get(globalType, project, GlobalSearchScope.projectScope(project)));

        Perl6LexicalTypeStubIndex lexicalIndex = Perl6LexicalTypeStubIndex.getInstance();
        for (String lexicalType : lexicalIndex.getAllKeys(project))
            results.addAll(lexicalIndex.get(lexicalType, project, GlobalSearchScope.projectScope(project)));

        Perl6AllRoutinesStubIndex routinesIndex = Perl6AllRoutinesStubIndex.getInstance();
        for (String routine : routinesIndex.getAllKeys(project))
            results.addAll(routinesIndex.get(routine, project, GlobalSearchScope.projectScope(project)));

        Perl6AllRegexesStubIndex regexesIndex = Perl6AllRegexesStubIndex.getInstance();
        for (String regex : regexesIndex.getAllKeys(project))
            results.addAll(regexesIndex.get(regex, project, GlobalSearchScope.projectScope(project)));

        Perl6AllAttributesStubIndex attrIndex = Perl6AllAttributesStubIndex.getInstance();
        for (String attr : attrIndex.getAllKeys(project))
            results.addAll(attrIndex.get(attr, project, GlobalSearchScope.projectScope(project)));

        Perl6AllConstantsStubIndex constantIndex = Perl6AllConstantsStubIndex.getInstance();
        for (String constant : constantIndex.getAllKeys(project))
            results.addAll(constantIndex.get(constant, project, GlobalSearchScope.projectScope(project)));

        return results.toArray(new NavigationItem[0]);
    }
}
