package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.module.Perl6MetaDataComponent;
import edument.perl6idea.utils.Perl6ModuleListFetcher;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class MissingModuleFix implements IntentionAction {
    private String moduleName;

    public MissingModuleFix(String holder) {
        moduleName = holder;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return String.format("Add module %s to META6.json", moduleName);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Add module to META6.json";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return moduleName != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Module module = ModuleUtilCore.findModuleForFile(file);
        if (module == null) throw new IncorrectOperationException("Cannot be used in files outside of a module");
        Perl6MetaDataComponent metaData = module.getComponent(Perl6MetaDataComponent.class);
        metaData.addDepends(moduleName);
        DaemonCodeAnalyzer.getInstance(project).restart(file);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
