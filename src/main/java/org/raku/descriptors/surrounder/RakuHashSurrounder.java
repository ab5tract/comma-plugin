package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.psi.RakuBlockOrHash;
import org.raku.psi.RakuElementFactory;

public class RakuHashSurrounder extends RakuControlSurrounder<RakuBlockOrHash> {
    public RakuHashSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuBlockOrHash createElement(Project project) {
        return RakuElementFactory.createBlockOrHash(project);
    }

    @Override
    protected boolean isExpression() {
        return true;
    }

    @Override
    protected boolean isControl() {
        return false;
    }

    @Override
    public String getTemplateDescription() {
        return "{ }";
    }
}
