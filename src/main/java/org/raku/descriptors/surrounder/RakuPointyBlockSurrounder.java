package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuPointyBlock;

public class RakuPointyBlockSurrounder extends RakuControlSurrounder<RakuPointyBlock> {
    public RakuPointyBlockSurrounder(boolean isSurrounder) {
        super(isSurrounder);
    }

    @Override
    protected RakuPointyBlock createElement(Project project) {
        return RakuElementFactory.createPointyBlock(project);
    }

    @Override
    public String getTemplateDescription() {
        return "-> {}";
    }

    @Override
    protected boolean isControl() {
        return false;
    }
}
