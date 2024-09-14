package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuIfStatement;

public class RakuWithSurrounder extends RakuConditionalSurrounder<RakuIfStatement> {
    public RakuWithSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuIfStatement createElement(Project project) {
        return RakuElementFactory.createIfStatement(project, false, 1);
    }

    @Override
    public String getTemplateDescription() {
        return "with";
    }
}
