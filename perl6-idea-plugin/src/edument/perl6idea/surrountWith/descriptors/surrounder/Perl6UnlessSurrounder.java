package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6UnlessStatement;

public class Perl6UnlessSurrounder extends Perl6ConditionalSurrounder<Perl6UnlessStatement> {
    public Perl6UnlessSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6UnlessStatement createElement(Project project) {
        return Perl6ElementFactory.createUnlessStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "unless";
    }
}
