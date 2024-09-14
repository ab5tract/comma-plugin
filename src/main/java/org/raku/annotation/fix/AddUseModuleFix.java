package org.raku.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.raku.utils.RakuUseUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class AddUseModuleFix implements IntentionAction {
    private final String moduleName;

    public AddUseModuleFix(String name) {
        moduleName = name;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Use " + moduleName + " module";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Raku";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return ModuleUtilCore.findModuleForFile(file) != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        RakuUseUtils.addUse(editor, file, moduleName, moduleName);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
