package org.raku.comma.intention;

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
import org.raku.comma.psi.*;
import org.raku.comma.utils.RakuPsiUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TernaryExpandingIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RakuInfixApplication infix = obtainTernary(element);
        if (infix == null) return;

        PsiElement[] wrap = infix.getOperands();
        if (wrap.length != 2) return;
        PsiElement condition = wrap[0];
        PsiElement falseBranch = wrap[1];
        RakuInfix middle = PsiTreeUtil.getChildOfType(infix, RakuInfix.class);
        if (middle == null) return;
        PsiElement trueBranch = RakuPsiUtil.skipSpaces(middle.getFirstChild().getNextSibling(), true);
        if (trueBranch == null) return;

        RakuIfStatement newIf = RakuElementFactory.createIfStatement(project, true, 2);
        RakuConditionalBranch[] branches = newIf.getBranches();
        branches[0].condition.replace(condition);
        boolean isStatementForm = infix.getParent() instanceof RakuStatement;
        String branchPattern = isStatementForm ? "if True {\n%s;\n}" : "if True { %s; }";
        branches[0].block.replace(PsiTreeUtil.findChildOfType(
          RakuElementFactory.createStatementFromText(project, String.format(branchPattern, trueBranch.getText())), RakuBlock.class));
        branches[1].block.replace(PsiTreeUtil.findChildOfType(
          RakuElementFactory.createStatementFromText(project, String.format(branchPattern, falseBranch.getText())), RakuBlock.class));

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
            RakuStatement doWrapper = RakuElementFactory.createStatementFromText(project, "do " + newIf.getText());
            infix.replace(doWrapper.getFirstChild());
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return obtainTernary(element) != null;
    }

    @Nullable
    private static RakuInfixApplication obtainTernary(@NotNull PsiElement element) {
        RakuInfixApplication app = PsiTreeUtil.getParentOfType(element, RakuInfixApplication.class);
        while (app != null) {
            if (app.getOperator().equals("??"))
                return app;
            app = PsiTreeUtil.getParentOfType(app, RakuInfixApplication.class);
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
