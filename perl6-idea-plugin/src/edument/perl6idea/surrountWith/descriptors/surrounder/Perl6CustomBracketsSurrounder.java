package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StrLiteral;

import java.util.Arrays;

public class Perl6CustomBracketsSurrounder extends Perl6StringQuotesSurrounder<Perl6StrLiteral> {
    public Perl6CustomBracketsSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6StrLiteral createElement(Project project) {
        return Perl6ElementFactory.createStrLiteral(project, "｢｣");
    }

    @Override
    protected PsiElement insertStatements(Perl6StrLiteral surrounder, PsiElement[] statements) {
        if (statements.length == 1) {
            String textToWrap = "｢" + statements[0].getText() + "｣";
            Perl6Statement statementToInsert = Perl6ElementFactory.createStatementFromText(surrounder.getProject(), textToWrap);
            Perl6StrLiteral literalToInsert = PsiTreeUtil.findChildOfType(statementToInsert, Perl6StrLiteral.class);
            if (literalToInsert != null) {
                surrounder = (Perl6StrLiteral)surrounder.replace(literalToInsert);
            }
        }
        return surrounder;
    }

    @Override
    protected PsiElement getAnchor(Perl6StrLiteral surrounder) {
        return null;
    }

    @Override
    public String getTemplateDescription() {
        return "｢ ｣";
    }
}
