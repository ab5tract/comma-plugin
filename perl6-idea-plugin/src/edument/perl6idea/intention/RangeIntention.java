package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6IntLiteral;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static edument.perl6idea.parsing.Perl6TokenTypes.INFIX;

public class RangeIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    static final Set<String> OPS = new HashSet<>(Arrays.asList("^..", "..", "..^", "^..^"));

    @NotNull
    @Override
    public String getText() {
        return "Remove redundant zero";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        // Infix application
        PsiElement parent = element.getParent().getParent();
        // Start of `0..foo`
        int offset = parent.getTextOffset();
        // Offset of `foo`, last child of application
        int offsetEnd = parent.getLastChild().getTextOffset();
        editor.getDocument().replaceString(offset, offsetEnd, "^");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (!(element.getNode().getElementType() == INFIX)) return false;
        String op = element.getText();
        if (!OPS.contains(op)) return false;
        element = element.getParent();
        PsiElement prev = PsiTreeUtil.getPrevSiblingOfType(element, Perl6IntLiteral.class);
        return prev != null && prev.getText().equals("0");
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }
}
