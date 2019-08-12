package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6WhenStatement;

public class Perl6WhenSurrounder extends Perl6ControlSurrounder<Perl6WhenStatement> {
    public Perl6WhenSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6WhenStatement createElement(Project project) {
        return Perl6ElementFactory.createWhenStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "when";
    }
}
