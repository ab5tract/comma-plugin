package org.raku.comma.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuGivenStatement;

public class RakuGivenSurrounder extends RakuControlSurrounder<RakuGivenStatement> {
    public RakuGivenSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuGivenStatement createElement(Project project) {
        return RakuElementFactory.createGivenStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "given";
    }
}
