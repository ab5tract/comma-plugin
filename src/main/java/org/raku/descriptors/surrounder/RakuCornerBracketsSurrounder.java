package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuStatement;
import org.raku.psi.RakuStrLiteral;

public class RakuCornerBracketsSurrounder extends RakuStringQuotesSurrounder<RakuStrLiteral> {
    public RakuCornerBracketsSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuStrLiteral createElement(Project project) {
        return RakuElementFactory.createStrLiteral(project, "｢｣");
    }

    @Override
    protected PsiElement insertStatements(RakuStrLiteral surrounder, PsiElement[] statements) {
        if (statements.length == 1) {
            String textToWrap = "｢" + statements[0].getText() + "｣";
            RakuStatement statementToInsert = RakuElementFactory.createStatementFromText(surrounder.getProject(), textToWrap);
            RakuStrLiteral literalToInsert = PsiTreeUtil.findChildOfType(statementToInsert, RakuStrLiteral.class);
            if (literalToInsert != null) {
                surrounder = (RakuStrLiteral)surrounder.replace(literalToInsert);
            }
        }
        return surrounder;
    }

    @Override
    protected PsiElement getAnchor(RakuStrLiteral surrounder) {
        return null;
    }

    @Override
    public String getTemplateDescription() {
        return "｢ ｣";
    }
}
