package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6GivenStatement;

public class Perl6GivenSurrounder extends Perl6ControlSurrounder<Perl6GivenStatement> {
    public Perl6GivenSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6GivenStatement createElement(Project project) {
        return Perl6ElementFactory.createGivenStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "given";
    }
}
