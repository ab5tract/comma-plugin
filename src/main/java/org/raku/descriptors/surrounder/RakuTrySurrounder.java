package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuTry;

public class RakuTrySurrounder extends RakuControlSurrounder<RakuTry> {
    public RakuTrySurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuTry createElement(Project project) {
        return RakuElementFactory.createTryStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "try";
    }

    @Override
    protected boolean isControl() {
        return false;
    }
}
