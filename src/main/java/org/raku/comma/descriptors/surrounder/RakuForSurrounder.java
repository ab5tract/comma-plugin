package org.raku.comma.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuForStatement;

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
