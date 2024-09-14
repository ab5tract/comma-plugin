package org.raku.comma.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.comma.psi.RakuElementFactory;
import org.raku.comma.psi.RakuStart;

public class RakuStartSurrounder extends RakuControlSurrounder<RakuStart> {
    public RakuStartSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuStart createElement(Project project) {
        return RakuElementFactory.createStartStatement(project);
    }

    @Override
    public String getTemplateDescription() {
        return "start";
    }

    @Override
    protected boolean isControl() {
        return false;
    }
}
