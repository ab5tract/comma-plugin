package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TernaryExpandingIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        Perl6InfixApplication infix = obtainTernary(element);
        if (infix == null) return;

        PsiElement[] wrap = infix.getOperands();
        if (wrap.length != 2) return;
        PsiElement condition = wrap[0];
        PsiElement falseBranch = wrap[1];
        Perl6Infix middle = PsiTreeUtil.getChildOfType(infix, Perl6Infix.class);
        if (middle == null) return;
        PsiElement trueBranch = Perl6PsiUtil.skipSpaces(middle.getFirstChild().getNextSibling(), true);
        if (trueBranch == null) return;

        Perl6IfStatement newIf = Perl6ElementFactory.createIfStatement(project, true, 2);
        Perl6ConditionalBranch[] branches = newIf.getBranches();
        branches[0].condition.replace(condition);
        boolean isStatementForm = infix.getParent() instanceof Perl6Statement;
        String branchPattern = isStatementForm ? "if True {\n%s;\n}" : "if True { %s; }";
        branches[0].block.replace(PsiTreeUtil.findChildOfType(Perl6ElementFactory.createStatementFromText(project, String.format(branchPattern, trueBranch.getText())), Perl6Block.class));
        branches[1].block.replace(PsiTreeUtil.findChildOfType(Perl6ElementFactory.createStatementFromText(project, String.format(branchPattern, falseBranch.getText())), Perl6Block.class));

        // Check if the ternary is at statement-level or a part of expression
        if (isStatementForm) {
            PsiElement newInfix = infix.replace(newIf);
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
            CodeStyleManager.getInstance(project).reformat(newInfix);
        }
        else {
            PsiElement newline = newIf.getChildren()[1].getNextSibling();
            if (newline.getText().equals("\n"))
                newline.replace(PsiParserFacade.getInstance(project).createWhiteSpaceFromText(" "));
            Perl6Statement doWrapper = Perl6ElementFactory.createStatementFromText(project, "do " + newIf.getText());
            infix.replace(doWrapper.getFirstChild());
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return obtainTernary(element) != null;
    }

    @Nullable
    private static Perl6InfixApplication obtainTernary(@NotNull PsiElement element) {
        Perl6InfixApplication app = PsiTreeUtil.getParentOfType(element, Perl6InfixApplication.class);
        while (app != null) {
            if (app.getOperator().equals("??"))
                return app;
            app = PsiTreeUtil.getParentOfType(app, Perl6InfixApplication.class);
        }
        return null;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Convert to 'if' block";
    }
}
