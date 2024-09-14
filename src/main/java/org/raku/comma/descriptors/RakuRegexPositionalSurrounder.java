package org.raku.comma.descriptors;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.raku.comma.psi.RakuElementFactory;

public class RakuRegexPositionalSurrounder implements RakuRegexSurrounder {
    @Override
    public String getTemplateDescription() {
        return "( ) (Regex)";
    }

    @Override
    public PsiElement createAtom(Project project) {
        return RakuElementFactory.createRegexGroup(project, true);
    }
}

