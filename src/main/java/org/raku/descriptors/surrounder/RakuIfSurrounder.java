package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuIfStatement;

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
