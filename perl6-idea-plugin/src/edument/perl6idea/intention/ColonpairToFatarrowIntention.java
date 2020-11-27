package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.annotation.NamedPairArgumentAnnotator;
import edument.perl6idea.psi.Perl6ColonPair;
import edument.perl6idea.psi.Perl6ElementFactory;
import org.jetbrains.annotations.NotNull;

public class ColonpairToFatarrowIntention implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Convert to fat arrow form";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        Perl6ColonPair pair = PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), Perl6ColonPair.class);
        return pair != null &&
                NamedPairArgumentAnnotator.getSimplifiedText(pair, pair.getKey(), pair.getStatement().getFirstChild()) == null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Perl6ColonPair pair = PsiTreeUtil.getParentOfType(file.findElementAt(editor.getCaretModel().getOffset()), Perl6ColonPair.class);
        assert pair != null;
        pair.replace(Perl6ElementFactory.createFatArrow(project, pair.getKey(), pair.getStatement()));
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
