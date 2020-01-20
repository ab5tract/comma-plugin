package edument.perl6idea.contribution;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6Regex;
import edument.perl6idea.psi.Perl6RegexDecl;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.stub.index.Perl6AllRegexesStubIndex;
import edument.perl6idea.psi.stub.index.Perl6AllRoutinesStubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GotoGrammarRuleProvider extends GotoRelatedProvider {
    @NotNull
    @Override
    public List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        // Ensure that we are in a method.
        Perl6RoutineDecl method = PsiTreeUtil.getParentOfType(psiElement, Perl6RoutineDecl.class, false);
        if (method == null || !method.getRoutineKind().equals("method"))
            return Collections.emptyList();

        // Ensure that it is inside of a class
        Perl6PackageDecl clazzzzzz = PsiTreeUtil.getParentOfType(method, Perl6PackageDecl.class);
        if (clazzzzzz == null || !clazzzzzz.getPackageKind().equals("class"))
            return Collections.emptyList();

        // Look for regexes of the same name in the index.
        Project project = psiElement.getProject();
        Collection<Perl6RegexDecl> decls = Perl6AllRegexesStubIndex.getInstance()
                .get(method.getRoutineName(), project, GlobalSearchScope.allScope(project));

        // Take those that are in the same module as us.
        List<GotoRelatedItem> result = new ArrayList<>();
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);
        Module module = fileIndex.getModuleForFile(method.getContainingFile().getOriginalFile().getVirtualFile());
        for (Perl6RegexDecl maybeRegex : decls) {
            VirtualFile actionFile = maybeRegex.getContainingFile().getOriginalFile().getVirtualFile();
            if (actionFile != null && fileIndex.getModuleForFile(actionFile) == module)
                result.add(new GotoRelatedItem(maybeRegex, "Grammar rule"));
        }
        return result;
    }
}
