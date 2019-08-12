package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6ForStatement;

public class Perl6ForSurrounder extends Perl6ControlSurrounder<Perl6ForStatement> {
    public Perl6ForSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6ForStatement createElement(Project project) {
        return Perl6ElementFactory.createForStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "for";
    }
}
