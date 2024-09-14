package org.raku.descriptors.surrounder;

import com.intellij.openapi.project.Project;
import org.raku.psi.RakuContextualizer;
import org.raku.psi.RakuElementFactory;

public class RakuHashContextSurrounder extends RakuContextualizerSurrounder<RakuContextualizer> {
    public RakuHashContextSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected RakuContextualizer createElement(Project project) {
        return RakuElementFactory.createContextualizer(project, RakuElementFactory.HASH_CONTEXTUALIZER);
    }

    @Override
    public String getTemplateDescription() {
        return "%(  )";
    }
}
