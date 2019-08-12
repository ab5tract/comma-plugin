package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6Contextualizer;
import edument.perl6idea.psi.Perl6ElementFactory;

public class Perl6ArrayContextSurrounder extends Perl6ContextualizerSurrounder<Perl6Contextualizer> {
    public Perl6ArrayContextSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6Contextualizer createElement(Project project) {
        return Perl6ElementFactory.createContextualizer(project, Perl6ElementFactory.ARRAY_CONTEXTUALIZER);
    }

    @Override
    public String getTemplateDescription() {
        return "@(  )";
    }
}
