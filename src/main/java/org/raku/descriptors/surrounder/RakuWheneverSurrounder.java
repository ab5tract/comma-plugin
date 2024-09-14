package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuWheneverStatement;

public class RakuWheneverSurrounder extends RakuControlSurrounder<RakuWheneverStatement> {
    public RakuWheneverSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuWheneverStatement createElement(Project project) {
        return RakuElementFactory.createWheneverStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "whenever";
    }
}
