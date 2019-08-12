package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6Contextualizer;
import edument.perl6idea.psi.Perl6ElementFactory;

public class Perl6HashContextSurrounder extends Perl6ContextualizerSurrounder<Perl6Contextualizer> {
    public Perl6HashContextSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected Perl6Contextualizer createElement(Project project) {
        return Perl6ElementFactory.createContextualizer(project, Perl6ElementFactory.HASH_CONTEXTUALIZER);
    }

    @Override
    public String getTemplateDescription() {
        return "%(  )";
    }
}
