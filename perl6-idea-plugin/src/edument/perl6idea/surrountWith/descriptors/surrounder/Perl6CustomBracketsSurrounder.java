package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6StrLiteral;

import java.util.Arrays;

public class Perl6CustomBracketsSurrounder extends Perl6StringQuotesSurrounder<Perl6StrLiteral> {
    public Perl6CustomBracketsSurrounder() {
        super();
    }

    @Override
    protected Perl6StrLiteral createElement(Project project) {
        return Perl6ElementFactory.createStrLiteral(project, "｢｣");
    }

    @Override
    protected void insertStatements(Perl6StrLiteral surrounder, PsiElement[] statements) {
        if (statements.length == 1) {
            String textToWrap = "｢" + statements[0].getText() + "｣";
            statements[0].replace(Perl6ElementFactory.createStatementFromText(surrounder.getProject(), textToWrap));
        }

        System.out.print(surrounder.getText());
        System.out.print(Arrays.toString(statements));
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
