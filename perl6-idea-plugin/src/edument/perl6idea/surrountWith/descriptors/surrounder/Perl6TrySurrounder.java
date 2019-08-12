package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Try;

public class Perl6TrySurrounder extends Perl6ControlSurrounder<Perl6Try> {
    public Perl6TrySurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6Try createElement(Project project) {
        return Perl6ElementFactory.createTryStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "try";
    }
}
