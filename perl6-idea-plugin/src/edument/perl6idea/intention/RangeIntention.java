package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
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
        PsiElement last = parent.getLastChild();
        int offsetEnd = last.getTextOffset();
        Integer top;

        // FIXME
        if (last instanceof Perl6IntLiteral && element.getText().equals("..")) {
            top = Integer.parseInt(last.getText());
            editor.getDocument().replaceString(
                offset, offsetEnd + last.getTextLength(), "^" + ++top);
        } else if (last instanceof Perl6IntLiteral && element.getText().equals("..^")) {
            editor.getDocument().replaceString(
                offset, offsetEnd, "^");
        } else if (last instanceof Perl6Variable && element.getText().equals("..^")) {
            editor.getDocument().replaceString(
                offset, offsetEnd, "^");
        } else if (last instanceof Perl6InfixApplication && element.getText().equals("..")) {
            PsiElement[] children = last.getChildren();
            if (checkInfix(children)) {
                editor.getDocument().replaceString(
                    offset, offsetEnd + last.getTextLength(),
                    "^" + children[0].getText()
                );
            }
        } else if (last instanceof Perl6ParenthesizedExpr && element.getText().equals("..")) {
            Perl6InfixApplication infixInParens = PsiTreeUtil.findChildOfType(last, Perl6InfixApplication.class);
            if (infixInParens != null) {
                PsiElement[] children = infixInParens.getChildren();
                if (checkInfix(children)) {
                    editor.getDocument().replaceString(
                        offset, offsetEnd + last.getTextLength(),
                        "^" + children[0].getText()
                    );
                }
            }
        }
    }

    private static boolean checkInfix(PsiElement[] children) {
        return children.length == 3 &&
               children[0] instanceof Perl6Variable &&
               children[1] instanceof Perl6Infix && children[1].getText().equals("-") &&
               children[2] instanceof Perl6IntLiteral && children[2].getText().equals("1");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (!(element.getNode().getElementType() == INFIX)) return false;
        String op = element.getText();
        if (!OPS.contains(op)) return false;
        element = element.getParent();
        PsiElement prev = PsiTreeUtil.getPrevSiblingOfType(element, Perl6IntLiteral.class);
        PsiElement nextInt = PsiTreeUtil.getPrevSiblingOfType(element, Perl6IntLiteral.class);
        PsiElement nextVar = PsiTreeUtil.getPrevSiblingOfType(element, Perl6Variable.class);
        PsiElement nextInfix = PsiTreeUtil.getPrevSiblingOfType(element, Perl6InfixApplication.class);
        PsiElement nextParens= PsiTreeUtil.getPrevSiblingOfType(element, Perl6ParenthesizedExpr.class);
        return prev != null && prev.getText().equals("0") &&
               (nextInt != null || nextVar != null || nextInfix != null || nextParens != null);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }
}
