package org.raku.comma.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuIfStatement;

public class RakuIfSurrounder extends RakuConditionalSurrounder<RakuIfStatement> {
    public RakuIfSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuIfStatement createElement(Project project) {
        return RakuElementFactory.createIfStatement(project, true, 1);
    }

    @Override
    public String getTemplateDescription() {
        return "if";
    }
}
