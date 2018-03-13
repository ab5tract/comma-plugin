package edument.perl6idea.editor;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

public class SigilCompletionAutoPopupHandler extends TypedHandlerDelegate {
    @Override
    public Result checkAutoPopup(char charTyped, Project project, Editor editor, PsiFile file) {
        LookupImpl lookup = (LookupImpl) LookupManager.getActiveLookup(editor);
        if (lookup != null) {
            if (editor.getSelectionModel().hasSelection()) {
                lookup.performGuardedChange(() -> {
                    EditorModificationUtil.deleteSelectedText(editor);
                });
            }
            return Result.STOP;
        }
        else if (charTyped == '$' || charTyped == '@' || charTyped == '%' || charTyped == '&') {
            AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
            return Result.STOP;
        }
        else {
            return Result.CONTINUE;
        }
    }
}
