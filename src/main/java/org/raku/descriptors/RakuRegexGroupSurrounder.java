package org.raku.descriptors;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.raku.psi.RakuElementFactory;

public class RakuRegexGroupSurrounder implements RakuRegexSurrounder {
    @Override
    public String getTemplateDescription() {
        return "[ ] (Regex)";
    }

    @Override
    public PsiElement createAtom(Project project) {
        return RakuElementFactory.createRegexGroup(project, false);
    }
}
