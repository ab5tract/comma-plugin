package org.raku.comma.actions;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.actions.BasePlatformRefactoringAction;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.refactoring.RakuExtractPackageHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtractBaseRakuStructureAction extends BasePlatformRefactoringAction {
    private final boolean isRole;

    public ExtractBaseRakuStructureAction(boolean isRole) {
        this.isRole = isRole;
    }

    @Override
    protected @Nullable RefactoringActionHandler getRefactoringHandler(@NotNull RefactoringSupportProvider provider) {
        return new RakuExtractPackageHandler(isRole);
    }

    @Override
    protected boolean isAvailableInEditorOnly() {
        return true;
    }

    @Override
    protected boolean isAvailableOnElementInEditorAndFile(@NotNull PsiElement element,
                                                          @NotNull Editor editor,
                                                          @NotNull PsiFile file,
                                                          @NotNull DataContext context) {
        PsiElement parent = PsiTreeUtil.getParentOfType(element, RakuPackageDecl.class);
        return parent != null;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        removeFirstWordInMainMenu(this, e);
    }

    public static void removeFirstWordInMainMenu(AnAction action, @NotNull AnActionEvent e) {
        if (ActionPlaces.MAIN_MENU.equals(e.getPlace())) {
            String templateText = action.getTemplatePresentation().getText();
            if (templateText != null && templateText.startsWith("Extract")) {
                e.getPresentation().setText(templateText.substring(templateText.indexOf(' ') + 1));
            }
        }
    }
}
