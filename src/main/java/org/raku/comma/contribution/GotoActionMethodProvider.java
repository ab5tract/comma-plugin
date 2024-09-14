package org.raku.comma.contribution;

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
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.psi.RakuRegexDecl;
import org.raku.comma.psi.RakuRoutineDecl;
import org.raku.comma.psi.stub.index.RakuAllRoutinesStubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GotoActionMethodProvider extends GotoRelatedProvider {
    @NotNull
    @Override
    public List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        // Ensure that we are in a regex.
        RakuRegexDecl regex = PsiTreeUtil.getParentOfType(psiElement, RakuRegexDecl.class, false);
        if (Objects.isNull(regex)) {
            return Collections.emptyList();
        }

        // Ensure that regex is inside of a grammar.
        RakuPackageDecl grammar = PsiTreeUtil.getParentOfType(regex, RakuPackageDecl.class);
        if (Objects.isNull(grammar) || ! grammar.getPackageKind().equals("grammar")) {
            return Collections.emptyList();
        }

        // Look for routines of the same name in the index.
        Project project = psiElement.getProject();
        var index = RakuAllRoutinesStubIndex.getInstance();
        Collection<RakuRoutineDecl> decls = StubIndex.getElements(index.getKey(),
                                                                  regex.getRegexName(),
                                                                  project,
                                                                  GlobalSearchScope.allScope(project),
                                                                  RakuRoutineDecl.class);

        // Take those that are in the same module as us.
        List<GotoRelatedItem> result = new ArrayList<>();
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);
        Module module = fileIndex.getModuleForFile(regex.getContainingFile().getOriginalFile().getVirtualFile());
        decls.forEach(maybeAction -> {
            if (maybeAction.getRoutineKind().equals("method")) {
                VirtualFile actionFile = maybeAction.getContainingFile().getOriginalFile().getVirtualFile();
                if (Objects.nonNull(actionFile) && fileIndex.getModuleForFile(actionFile) == module) {
                    result.add(new GotoRelatedItem(maybeAction, "Action Method"));
                }
            }
        });
        return result;
    }
}
