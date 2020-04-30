package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenameHandler;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertPositionalCaptureIntoNamedIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        Perl6RegexCapturePositional group = PsiTreeUtil.getNonStrictParentOfType(element, Perl6RegexCapturePositional.class);
        assert group != null;
        String regexContent = group.getText();
        PsiElement capture = PsiTreeUtil.findChildOfType(
            Perl6ElementFactory
                .createStatementFromText(project, String.format("/$<x>=(%s)/", regexContent.substring(1, regexContent.length() - 1))),
            Perl6RegexVariable.class);
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
        VariableInplaceRenameHandler handler = new VariableInplaceRenameHandler();
        editor.getCaretModel().moveToOffset(element.getTextOffset() + 1);
        handler.doRename(element, editor, DataContext.EMPTY_CONTEXT);
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    public String getFamilyName() {
        return "Convert into named capture";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    public String getText() {
        return getFamilyName();
    }

    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return PsiTreeUtil.getNonStrictParentOfType(element, Perl6RegexCapturePositional.class) != null;
    }
}
