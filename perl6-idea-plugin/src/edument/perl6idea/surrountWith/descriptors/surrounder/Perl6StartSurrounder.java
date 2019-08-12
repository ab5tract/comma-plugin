package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Start;

public class Perl6StartSurrounder extends Perl6ControlSurrounder<Perl6Start> {
    public Perl6StartSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6Start createElement(Project project) {
        return Perl6ElementFactory.createStartStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "start";
    }
}
