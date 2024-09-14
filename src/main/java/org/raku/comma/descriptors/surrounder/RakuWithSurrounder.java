package org.raku.comma.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuIfStatement;

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
