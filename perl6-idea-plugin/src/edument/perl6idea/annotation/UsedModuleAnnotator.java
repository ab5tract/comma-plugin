package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.module.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.annotation.fix.MissingModuleFix;
import edument.perl6idea.module.Perl6MetaDataComponent;
import edument.perl6idea.psi.Perl6ModuleName;
import edument.perl6idea.utils.Perl6ModuleListFetcher;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class UsedModuleAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6ModuleName))
            return;

        String moduleName = element.getText();
        // We don't need to annotate late-bound modules
        if (moduleName.startsWith("::")) return;
        if (Perl6ModuleListFetcher.PREINSTALLED_MODULES.contains(moduleName))
            return;
        if (Perl6ModuleListFetcher.PRAGMAS.contains(moduleName))
            return;

        Module module = ModuleUtilCore.findModuleForPsiElement(element);
        if (module == null) return;

        Perl6MetaDataComponent metaData = module.getComponent(Perl6MetaDataComponent.class);

        PsiReference ref = element.getReference();
        if (ref == null) return;
        PsiElement resolved = ref.resolve();
        if (resolved != null) return;

        boolean isProvided = false;
        for (String dependency : metaData.getDepends(true)) {
            Set<String> provides = Perl6ModuleListFetcher.getProvidesByModuleAsync(element.getProject(), dependency);
            if (provides == null) return;
            isProvided = provides.contains(moduleName);
            if (isProvided) break;
        }
        if (!isProvided)
            holder
                .createErrorAnnotation(element, String.format("Cannot find %s based on dependencies from META6.json", moduleName))
                .registerFix(new MissingModuleFix(element.getProject(), moduleName));
    }
}
