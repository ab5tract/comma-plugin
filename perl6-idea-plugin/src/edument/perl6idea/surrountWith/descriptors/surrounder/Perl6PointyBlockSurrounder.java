package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6PointyBlock;

public class Perl6PointyBlockSurrounder extends Perl6ControlSurrounder<Perl6PointyBlock> {
    public Perl6PointyBlockSurrounder(boolean isSurrounder) {
        super(isSurrounder);
    }

    @Override
    protected Perl6PointyBlock createElement(Project project) {
        return Perl6ElementFactory.createPointyBlock(project);
    }

    @Override
    public String getTemplateDescription() {
        return "-> {}";
    }

    @Override
    protected boolean isControl() {
        return false;
    }
}
