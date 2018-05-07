package edument.perl6idea.contribution;

import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.ID;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6SymbolLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

abstract class Perl6NameContributor implements GotoClassContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        Set<String> names = collectNamePool(project, null).keySet();
        return names.toArray(new String[names.size()]);
    }

    @Nullable
    public PsiElement getItemByName(String name, Project project, Set<String> includes) {
        return collectNamePool(project, includes).getOrDefault(name, null);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        Map<String, PsiElement> types = collectNamePool(project, null);
        Perl6SymbolLike typeLike = (Perl6SymbolLike) types.get(name);
        return new NavigationItem[]{typeLike};
    }

    private Map<String, PsiElement> collectNamePool(Project project, Set<String> includes) {
        Map<String, PsiElement> typeMap = new HashMap<>();
        Collection<VirtualFile> virtualFiles = new ArrayList<>(
                FileBasedIndex.getInstance().getContainingFiles(
                        ID.create("filetypes"), Perl6ModuleFileType.INSTANCE, GlobalSearchScope.allScope(project)));
        for (VirtualFile virtualFile : virtualFiles) {
            Perl6File file = (Perl6File) PsiManager.getInstance(project).findFile(virtualFile);
            if (file == null) continue;
            String path = virtualFile.getCanonicalPath();
            if (path == null) continue;
            String[] parts = path.split(File.separator + "lib" + File.separator);
            if (parts.length > 1) {
                String modulePrefix = parts[parts.length - 1]
                        .replaceAll(File.separator, "::") // Module file types
                        .replace(".pm6", "");
                if (includes != null && !includes.contains(modulePrefix)) continue;
                typeMap.putAll(getNamesFromFile(file, modulePrefix));
            }
        }
        return typeMap;
    }

    // Must be overloaded in particular contributor instance.
    // Gets symbols to contribute from file based on class|symbol differentiation.
    protected Map<String, PsiElement> getNamesFromFile(Perl6File file, String prefix) {
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedName(NavigationItem navigationItem) {
        return navigationItem.getName();
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator() {
        return "::";
    }
}
