package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6WheneverStatement;

public class Perl6WheneverSurrounder extends Perl6ControlSurrounder<Perl6WheneverStatement> {
    public Perl6WheneverSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6WheneverStatement createElement(Project project) {
        return Perl6ElementFactory.createWheneverStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "whenever";
    }
}
