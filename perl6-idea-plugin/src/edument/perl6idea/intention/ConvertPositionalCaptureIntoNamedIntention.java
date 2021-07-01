package edument.perl6idea.intention;

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
import com.intellij.refactoring.introduce.inplace.InplaceVariableIntroducer;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.refactoring.RakuNameValidator;
import edument.perl6idea.refactoring.helpers.Perl6IntroduceDialog;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class ConvertPositionalCaptureIntoNamedIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        Perl6RegexCapturePositional group = PsiTreeUtil.getNonStrictParentOfType(element, Perl6RegexCapturePositional.class);
        assert group != null;
        String regexContent = group.getText();
        PsiElement capture = PsiTreeUtil.findChildOfType(
            Perl6ElementFactory
                .createStatementFromText(project, String.format("/$<x>=(%s)/", regexContent.substring(1, regexContent.length() - 1))),
            Perl6RegexVariable.class);
        if (capture != null)
            postProcess(project, editor, group.replace(capture));
    }

    private static void postProcess(Project project, Editor editor, PsiElement element) {
        runRenamingProcess(project, editor, element);
    }

    private static void runRenamingProcess(Project project, Editor editor, PsiElement element) {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            if (element instanceof Perl6RegexVariable)
                ((Perl6RegexVariable)element).setName("$<x>");
            return;
        }
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        Perl6IntroduceDialog dialog = new Perl6IntroduceDialog(
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
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    ((PsiNamedElement)element).setName(dialog.getName());
                });
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
        return PsiTreeUtil.getNonStrictParentOfType(element, Perl6RegexCapturePositional.class) != null;
    }
}
