package org.raku.comma.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuWhenStatement;

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
