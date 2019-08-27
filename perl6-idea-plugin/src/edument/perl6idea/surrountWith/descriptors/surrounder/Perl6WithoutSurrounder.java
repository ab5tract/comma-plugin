package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6WithoutStatement;

public class Perl6WithoutSurrounder extends Perl6ConditionalSurrounder<Perl6WithoutStatement> {
    public Perl6WithoutSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6WithoutStatement createElement(Project project) {
        return Perl6ElementFactory.createWithoutStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "without";
    }

}
