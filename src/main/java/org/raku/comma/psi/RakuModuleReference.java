package org.raku.comma.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.stub.index.RakuStubIndexKeys;
import org.raku.comma.psi.stub.index.ProjectModulesStubIndex;
import org.raku.comma.utils.RakuModuleListFetcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RakuModuleReference extends PsiReferenceBase<RakuModuleName> {
    public RakuModuleReference(@NotNull RakuModuleName moduleName) {
        super(moduleName, new TextRange(0, moduleName.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Project project = this.getElement().getProject();
        Collection<RakuFile> elements = StubIndex.getElements(RakuStubIndexKeys.PROJECT_MODULES, getValue(), project, GlobalSearchScope.allScope(project), RakuFile.class);
        return  elements.isEmpty() ? null : elements.iterator().next();
    }

    @Override
    public Object @NotNull [] getVariants() {
        Project project = this.getElement().getProject();
        Collection<String> projectModules = ProjectModulesStubIndex.getInstance().getAllKeys(project);
        List<String> reallyInThisProject = new ArrayList<>();
        projectModules.forEach(module -> {
            var index = ProjectModulesStubIndex.getInstance();
            Collection<RakuFile> matching = StubIndex.getElements(index.getKey(),
                                                                  module,
                                                                  project,
                                                                  GlobalSearchScope.projectScope(project),
                                                                  RakuFile.class);
            if (! matching.isEmpty()) {
                reallyInThisProject.add(module);
            }
        });

        if (RakuModuleListFetcher.isReady()) {
            reallyInThisProject.addAll(RakuModuleListFetcher.getProvides(myElement.getProject()));
        }
        reallyInThisProject.addAll(RakuModuleListFetcher.PREINSTALLED_MODULES);
        reallyInThisProject.addAll(RakuModuleListFetcher.PRAGMAS);
        return reallyInThisProject.toArray();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        RakuModuleName name = getElement();
        return name.setName(newElementName);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // Our RakuFile, so can calculate new path
        if (element instanceof RakuPsiElement) {
            getElement().setName(((RakuPsiElement)element).getEnclosingPerl6ModuleName());
        }
        return element;
    }
}
