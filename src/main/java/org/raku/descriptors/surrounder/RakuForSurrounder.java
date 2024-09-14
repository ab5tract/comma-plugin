package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuForStatement;

public class RakuForSurrounder extends RakuControlSurrounder<RakuForStatement> {
    public RakuForSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuForStatement createElement(Project project) {
        return RakuElementFactory.createForStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "for";
    }
}
