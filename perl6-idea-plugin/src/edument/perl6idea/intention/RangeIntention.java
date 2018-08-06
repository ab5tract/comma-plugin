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

import static edument.perl6idea.parsing.Perl6TokenTypes.*;

public class RangeIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    static final Set<String> OPS = new HashSet<>(Arrays.asList("..", "..^"));

    @NotNull
    @Override
    public String getText() {
        return "Use simpler range syntax";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        Perl6InfixApplication application = PsiTreeUtil.getParentOfType(element, Perl6InfixApplication.class);
        if (application == null) return;

        element = PsiTreeUtil.findChildOfType(application, Perl6Infix.class);
        if (element == null) return;
        int startCaretOffset = editor.getCaretModel().getOffset();
        // Start of `0..foo`
        int offset = application.getTextOffset();
        // Offset of `foo`, last child of application
        PsiElement last = application.getLastChild();
        int offsetEnd = last.getTextOffset();

        String infixText = element.getText();
        boolean isSimpleRange = infixText.equals("..");
        if (last instanceof Perl6IntLiteral && isSimpleRange) {
            // We can not catch exception here, as Perl6IntLiteral is always a valid number
            int top = Integer.parseInt(last.getText());
            editor.getDocument().replaceString(offset, offsetEnd + last.getTextLength(), "^" + ++top);
        } else if (infixText.equals("..^") && (last instanceof Perl6IntLiteral || last instanceof Perl6Variable)) {
            editor.getDocument().replaceString(offset, offsetEnd, "^");
        } else if (last instanceof Perl6InfixApplication && isSimpleRange) {
            handleInfix(editor, offset, last, offsetEnd, last.getTextLength());
        } else if (last instanceof Perl6ParenthesizedExpr && isSimpleRange) {
            Perl6InfixApplication infixInParens = PsiTreeUtil.findChildOfType(last, Perl6InfixApplication.class);
            if (infixInParens != null)
                handleInfix(editor, offset, infixInParens, offsetEnd, last.getTextLength());
        }
        editor.getCaretModel().moveToOffset(startCaretOffset);
    }

    private static void handleInfix(Editor editor, int offset, PsiElement last, int offsetEnd, int length) {
        PsiElement[] children = last.getChildren();
        if (checkInfix(children)) {
            editor.getDocument().replaceString(
                offset, offsetEnd + length,
                "^" + children[0].getText()
            );
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
        if (!(element.getNode().getElementType() == INFIX || // ..
              element.getNode().getElementType() == INTEGER_LITERAL || // numbers
              element.getNode().getElementType() == VARIABLE || // $foo
              element.getNode().getElementType() == PARENTHESES_OPEN || // (
              element.getNode().getElementType() == PARENTHESES_CLOSE)) // )
            return false;

        Perl6InfixApplication application = PsiTreeUtil.getParentOfType(element, Perl6InfixApplication.class);
        if (application == null)
            return false;

        PsiElement infix = PsiTreeUtil.getChildOfType(application, Perl6Infix.class);
        if (infix == null) return false;
        String op = infix.getText();
        if (!OPS.contains(op)) return false;
        // Get and check possible siblings
        PsiElement temp = infix.getNextSibling();
        while (temp != null) {
            if (temp instanceof Perl6IntLiteral || temp instanceof Perl6Variable ||
                temp instanceof Perl6InfixApplication || temp instanceof Perl6ParenthesizedExpr)
                break;
            temp = temp.getNextSibling();
        }

        PsiElement prev = PsiTreeUtil.getPrevSiblingOfType(infix, Perl6IntLiteral.class);
        Perl6InfixApplication infixInParens = null;
        if (temp instanceof Perl6ParenthesizedExpr)
            infixInParens = PsiTreeUtil.findChildOfType(temp, Perl6InfixApplication.class);
        return prev != null && prev.getText().equals("0") && // Basic condition
               // Int condition
               (temp instanceof Perl6IntLiteral && (infix.getText().equals("..") || infix.getText().equals("..^")) ||
                // Var condition
                temp instanceof Perl6Variable && infix.getText().equals("..^") ||
                // Infix, possible `0..$n-1`
                temp instanceof Perl6InfixApplication && infix.getText().equals("..") ||
                // Parens, possible ..($n-1)
                temp instanceof Perl6ParenthesizedExpr && infix.getText().equals("..") &&
                ( // inner parens expr
                  infixInParens != null && checkInfix(infixInParens.getChildren())
                )
               );
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }
}
