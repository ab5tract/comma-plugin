package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuWhenStatement;

public class RakuWhenSurrounder extends RakuControlSurrounder<RakuWhenStatement> {
    public RakuWhenSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuWhenStatement createElement(Project project) {
        return RakuElementFactory.createWhenStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "when";
    }
}
