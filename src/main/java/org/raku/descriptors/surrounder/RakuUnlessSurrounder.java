package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuUnlessStatement;

public class RakuUnlessSurrounder extends RakuConditionalSurrounder<RakuUnlessStatement> {
    public RakuUnlessSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuUnlessStatement createElement(Project project) {
        return RakuElementFactory.createUnlessStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "unless";
    }
}
