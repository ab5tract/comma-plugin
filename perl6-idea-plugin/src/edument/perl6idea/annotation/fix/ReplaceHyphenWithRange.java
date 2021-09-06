package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import org.jetbrains.annotations.NotNull;

public class ReplaceHyphenWithRange implements IntentionAction {
    private final PsiElement myHyphen;

    public ReplaceHyphenWithRange(@NotNull PsiElement element) {
        myHyphen = element;
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return getFamilyName();
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Replace with range '..'";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement delimiter = Perl6ElementFactory.createRegexRangeDelimiter(project);
        PsiElement replace = myHyphen.replace(delimiter);
        replace.getParent().addAfter(delimiter.copy(), replace);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
