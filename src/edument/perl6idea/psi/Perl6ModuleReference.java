package edument.perl6idea.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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
        Collection<String> projectModules = ProjectModulesStubIndex.getInstance()
            .getAllKeys(this.getElement().getProject());
        return projectModules.toArray();
    }
}
