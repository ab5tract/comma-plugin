package org.raku.comma.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuWithoutStatement;

public class RakuWithoutSurrounder extends RakuConditionalSurrounder<RakuWithoutStatement> {
    public RakuWithoutSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuWithoutStatement createElement(Project project) {
        return RakuElementFactory.createWithoutStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "without";
    }

}
