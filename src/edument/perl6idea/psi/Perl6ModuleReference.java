package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
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
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Collection<String> projectModules = ProjectModulesStubIndex.getInstance()
            .getAllKeys(this.getElement().getProject());
        return projectModules.toArray();
    }
}
