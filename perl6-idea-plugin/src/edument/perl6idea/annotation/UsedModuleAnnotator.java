package edument.perl6idea.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.annotation.fix.CreateLocalModuleFix;
import edument.perl6idea.annotation.fix.MissingModuleFix;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.psi.Perl6ColonPair;
import edument.perl6idea.psi.Perl6LongName;
import edument.perl6idea.psi.Perl6ModuleName;
import edument.perl6idea.utils.Perl6ModuleListFetcher;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UsedModuleAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Perl6ModuleName))
            return;

        Perl6LongName moduleNameNode = PsiTreeUtil.findChildOfType(element, Perl6LongName.class);
        if (moduleNameNode == null)
            return;
        String moduleName = moduleNameNode.getFirstChild().getText();

        Collection<Perl6ColonPair> params = PsiTreeUtil.findChildrenOfType(moduleNameNode, Perl6ColonPair.class);
        for (Perl6ColonPair colonPair : params) {
            String key = colonPair.getKey();
            if (Objects.equals(key, "from"))
                return;
        }

        // We don't need to annotate late-bound modules
        if (moduleName.startsWith("::")) return;
        if (Perl6ModuleListFetcher.PREINSTALLED_MODULES.contains(moduleName))
            return;
        if (Perl6ModuleListFetcher.PRAGMAS.contains(moduleName))
            return;

        Module module = ModuleUtilCore.findModuleForPsiElement(element);
        if (module == null) return;

        Perl6MetaDataComponent metaData = module.getService(Perl6MetaDataComponent.class);

        // No need to annotate "missing" modules, if there are
        // no META data available
        if (!metaData.isMetaDataExist())
            return;

        PsiReference ref = element.getReference();
        if (ref == null) return;
        PsiElement resolved = ref.resolve();
        if (resolved != null) return;

        Project project = element.getProject();

        if (!Perl6ModuleListFetcher.isReady()) {
            Perl6ModuleListFetcher.populateModules(project);
            return;
        }

        List<String> dependencies = new ArrayList<>();
        dependencies.addAll(metaData.getDepends(true));
        dependencies.addAll(metaData.getTestDepends(true));
        dependencies.addAll(metaData.getBuildDepends(true));
        for (String dependency : dependencies) {
            Set<String> providesOfDependency = Perl6ModuleListFetcher.getProvidesByModule(project, dependency, new HashSet<>());
            // Maybe it is a part of the distribution, and we can get something out its parent distribution
            if (providesOfDependency.isEmpty()) {
                String parentModuleName = Perl6ModuleListFetcher.getModuleByProvide(project, dependency);
                if (parentModuleName != null) {
                    providesOfDependency = Perl6ModuleListFetcher.getProvidesByModule(project, parentModuleName, new HashSet<>());
                }
            }
            // If a module is in dependencies list, do nothing
            if (providesOfDependency.contains(moduleName))
                return;
        }
        String holderPackage = Perl6ModuleListFetcher.getModuleByProvide(project, moduleName);
        if (holderPackage != null) {
            holder
                .newAnnotation(HighlightSeverity.ERROR, String.format("Cannot find %s based on dependencies from META6.json", moduleName))
                .range(element)
                .withFix(new MissingModuleFix(holderPackage)).create();
        }
        else {
            holder
                .newAnnotation(HighlightSeverity.ERROR, String.format("Cannot find %s in the ecosystem", moduleName))
                .range(element)
                .withFix(new CreateLocalModuleFix(module, (Perl6ModuleName)element)).create();
        }
    }
}
