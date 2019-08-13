package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6BlockOrHash;
import edument.perl6idea.psi.Perl6ElementFactory;

public class Perl6HashSurrounder extends Perl6ControlSurrounder<Perl6BlockOrHash> {
    public Perl6HashSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6BlockOrHash createElement(Project project) {
        return Perl6ElementFactory.createBlockOrHash(project);
    }

    @Override
    protected boolean isExpression() {
        return true;
    }

    @Override
    public String getTemplateDescription() {
        return "{ }";
    }
}
