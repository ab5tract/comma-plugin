package edument.perl6idea.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import edument.perl6idea.psi.stub.index.Perl6StubIndexKeys;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Perl6ModuleReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6ModuleReference(@NotNull Perl6ModuleName moduleName) {
        super(moduleName, new TextRange(0, moduleName.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Project project = this.getElement().getProject();
        Collection<Perl6File> matching = ProjectModulesStubIndex.getInstance()
            .get(this.getValue(), project, GlobalSearchScope.projectScope(project));
        return matching.isEmpty() ? null : matching.iterator().next();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = this.getElement().getProject();
        Collection<String> projectModules = ProjectModulesStubIndex.getInstance().getAllKeys(project);
        List<String> reallyInThisProject = new ArrayList<>();
        for (String module : projectModules) {
            Collection<Perl6File> matching = ProjectModulesStubIndex.getInstance()
                .get(module, project, GlobalSearchScope.projectScope(project));
            if (!matching.isEmpty())
                reallyInThisProject.add(module);
        }
        return reallyInThisProject.toArray();
    }
}
