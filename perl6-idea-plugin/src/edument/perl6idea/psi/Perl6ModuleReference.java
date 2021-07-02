package edument.perl6idea.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.stub.index.Perl6StubIndexKeys;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.utils.Perl6ModuleListFetcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Perl6ModuleReference extends PsiReferenceBase<Perl6ModuleName> {
    public Perl6ModuleReference(@NotNull Perl6ModuleName moduleName) {
        super(moduleName, new TextRange(0, moduleName.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Project project = this.getElement().getProject();
        Collection<Perl6File> elements = StubIndex.getElements(Perl6StubIndexKeys.PROJECT_MODULES, getValue(), project, GlobalSearchScope.allScope(project), Perl6File.class);
        return  elements.isEmpty() ? null : elements.iterator().next();
    }

    @Override
    public Object @NotNull [] getVariants() {
        Project project = this.getElement().getProject();
        Collection<String> projectModules = ProjectModulesStubIndex.getInstance().getAllKeys(project);
        List<String> reallyInThisProject = new ArrayList<>();
        for (String module : projectModules) {
            Collection<Perl6File> matching = ProjectModulesStubIndex.getInstance()
                .get(module, project, GlobalSearchScope.projectScope(project));
            if (!matching.isEmpty())
                reallyInThisProject.add(module);
        }

        if (Perl6ModuleListFetcher.isReady()) {
            reallyInThisProject.addAll(Perl6ModuleListFetcher.getProvides(myElement.getProject()));
        }
        reallyInThisProject.addAll(Perl6ModuleListFetcher.PREINSTALLED_MODULES);
        reallyInThisProject.addAll(Perl6ModuleListFetcher.PRAGMAS);
        return reallyInThisProject.toArray();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        Perl6ModuleName name = getElement();
        return name.setName(newElementName);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // Our Perl6File, so can calculate new path
        if (element instanceof Perl6PsiElement) {
            getElement().setName(((Perl6PsiElement)element).getEnclosingPerl6ModuleName());
        }
        return element;
    }
}
