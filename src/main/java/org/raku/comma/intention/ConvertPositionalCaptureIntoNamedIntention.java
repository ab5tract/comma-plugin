package org.raku.comma.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuRegexCapturePositional;
import org.raku.comma.psi.RakuRegexVariable;
import org.raku.comma.refactoring.RakuNameValidator;
import org.raku.comma.refactoring.helpers.RakuIntroduceDialog;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class ConvertPositionalCaptureIntoNamedIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RakuRegexCapturePositional group = PsiTreeUtil.getNonStrictParentOfType(element, RakuRegexCapturePositional.class);
        assert group != null;
        String regexContent = group.getText();
        PsiElement capture = PsiTreeUtil.findChildOfType(
          RakuElementFactory
                .createStatementFromText(project, String.format("/$<x>=(%s)/", regexContent.substring(1, regexContent.length() - 1))),
          RakuRegexVariable.class);
        if (capture != null)
            postProcess(project, editor, group.replace(capture));
    }

    private static void postProcess(Project project, Editor editor, PsiElement element) {
        runRenamingProcess(project, editor, element);
    }

    private static void runRenamingProcess(Project project, Editor editor, PsiElement element) {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            if (element instanceof RakuRegexVariable)
                ((RakuRegexVariable)element).setName("$<x>");
            return;
        }
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        RakuIntroduceDialog dialog = new RakuIntroduceDialog(
            project, "New named capture name",
            new RakuNameValidator() {
                @Override
                public boolean isNameValid(String name) {
                    return name.startsWith("$<") && name.endsWith(">");
                }
            }, null,
            Collections.singletonList("$<x>"));
        ApplicationManager.getApplication().invokeLater(() -> {
            if (!dialog.showAndGet())
                return;
            if (element instanceof PsiNamedElement)
                WriteCommandAction.runWriteCommandAction(project, "Raku Intention", null,
                                                         () -> ((PsiNamedElement)element).setName(dialog.getName()));
        });
    }

    @Override
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    public String getFamilyName() {
        return "Convert into named capture";
    }

    @Override
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    public String getText() {
        return getFamilyName();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return PsiTreeUtil.getNonStrictParentOfType(element, RakuRegexCapturePositional.class) != null;
    }
}
