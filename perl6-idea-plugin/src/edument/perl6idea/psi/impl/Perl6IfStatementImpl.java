package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6IfStatement;
import edument.perl6idea.psi.Perl6PointyBlock;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static edument.perl6idea.parsing.Perl6TokenTypes.STATEMENT_CONTROL;

public class Perl6IfStatementImpl extends ASTWrapperPsiElement implements Perl6IfStatement {
    public Perl6IfStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getLeadingStatementControl() {
        PsiElement control = findChildByType(STATEMENT_CONTROL);
        return control == null ? "" : control.getText();
    }

    @Override
    public boolean isTopicalizing() {
        return getLeadingStatementControl().equals("with") &&
                PsiTreeUtil.getChildOfType(this, Perl6PointyBlock.class) == null;
    }

    @Override
    public Perl6Type inferTopicType() {
        // Condition is first non-token thing.
        Perl6PsiElement condition = PsiTreeUtil.getChildOfType(this, Perl6PsiElement.class);
        return condition == null ? Perl6IfStatement.super.inferTopicType() : condition.inferType();
    }

    private enum PartType { None, If, With, Else }

    @Override
    public IfStructure getStructure() {
        List<IfPart> parts = new ArrayList<>();
        PartType currentPartType = PartType.None;
        Perl6PsiElement currentCondition = null;
        Perl6PsiElement currentBody = null;
        Perl6PsiElement elseBody = null;
        PsiElement current = getFirstChild();
        while ((current = Perl6PsiUtil.skipSpaces(current, true, true)) != null) {
            if (current.getNode().getElementType() == STATEMENT_CONTROL) {
                // Commit any current part (cannot be else).
                if (currentPartType != PartType.None) {
                    parts.add(new IfPart(currentPartType == PartType.With, currentCondition, currentBody));
                }
                // Set up next part type.
                String whatIsIt = current.getText();
                if (whatIsIt.equals("else")) {
                    currentPartType = PartType.Else;
                }
                else if (whatIsIt.equals("if") || whatIsIt.equals("elsif")) {
                    currentPartType = PartType.If;
                }
                else {
                    currentPartType = PartType.With;
                }
                currentCondition = null;
                currentBody = null;
            }
            else if (current instanceof Perl6PsiElement) {
                if (currentPartType == PartType.Else) {
                    elseBody = (Perl6PsiElement)current;
                }
                else if (currentPartType != PartType.None) {
                    if (currentCondition == null)
                        currentCondition = (Perl6PsiElement)current;
                    else
                        currentBody = (Perl6PsiElement)current;
                }
            }
            current = current.getNextSibling();
        }
        if (currentPartType == PartType.If || currentPartType == PartType.With)
            parts.add(new IfPart(currentPartType == PartType.With, currentCondition, currentBody));
        return new IfStructure(parts.toArray(new IfPart[0]), elseBody);
    }
}
