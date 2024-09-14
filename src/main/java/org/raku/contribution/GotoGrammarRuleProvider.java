package org.raku.contribution;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuPackageDecl;
import org.raku.psi.RakuRegexDecl;
import org.raku.psi.RakuRoutineDecl;
import org.raku.psi.stub.index.RakuAllRegexesStubIndex;
import org.jetbrains.annotations.NotNull;
import org.raku.psi.stub.index.RakuIndexableType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GotoGrammarRuleProvider extends GotoRelatedProvider {
    @NotNull
    @Override
    public List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        // Ensure that we are in a method.
        RakuRoutineDecl method = PsiTreeUtil.getParentOfType(psiElement, RakuRoutineDecl.class, false);
        if (method == null || !method.getRoutineKind().equals("method"))
            return Collections.emptyList();

        // Ensure that it is inside of a class
        RakuPackageDecl clazzzzzz = PsiTreeUtil.getParentOfType(method, RakuPackageDecl.class);
        if (clazzzzzz == null || !clazzzzzz.getPackageKind().equals("class"))
            return Collections.emptyList();

        // Look for regexes of the same name in the index.
        Project project = psiElement.getProject();
        var regexIndex = RakuAllRegexesStubIndex.getInstance();
        Collection<RakuRegexDecl> decls = StubIndex.getElements(regexIndex.getKey(),
                                                                method.getRoutineName(),
                                                                project,
                                                                GlobalSearchScope.allScope(project),
                                                                RakuRegexDecl.class);

        // Take those that are in the same module as us.
        List<GotoRelatedItem> result = new ArrayList<>();
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);
        Module module = fileIndex.getModuleForFile(method.getContainingFile().getOriginalFile().getVirtualFile());
        for (RakuRegexDecl maybeRegex : decls) {
            VirtualFile actionFile = maybeRegex.getContainingFile().getOriginalFile().getVirtualFile();
            if (actionFile != null && fileIndex.getModuleForFile(actionFile) == module)
                result.add(new GotoRelatedItem(maybeRegex, "Grammar Rule"));
        }
        return result;
    }
}
