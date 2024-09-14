package org.raku.comma.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.comma.psi.RakuContextualizer;
import org.raku.comma.psi.RakuElementFactory;

public class RakuArrayContextSurrounder extends RakuContextualizerSurrounder<RakuContextualizer> {
    public RakuArrayContextSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuContextualizer createElement(Project project) {
        return RakuElementFactory.createContextualizer(project, RakuElementFactory.ARRAY_CONTEXTUALIZER);
    }

    @Override
    public String getTemplateDescription() {
        return "@(  )";
    }
}
