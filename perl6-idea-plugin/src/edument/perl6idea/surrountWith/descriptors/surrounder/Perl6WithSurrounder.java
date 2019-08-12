package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6IfStatement;

public class Perl6WithSurrounder extends Perl6ConditionalSurrounder<Perl6IfStatement> {
    public Perl6WithSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6IfStatement createElement(Project project) {
        return Perl6ElementFactory.createIfStatement(project, false, 1);
    }

    @Override
    public String getTemplateDescription() {
        return "with";
    }
}
