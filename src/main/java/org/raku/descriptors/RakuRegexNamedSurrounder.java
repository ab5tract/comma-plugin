package org.raku.descriptors;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuVariable;

public class RakuRegexNamedSurrounder implements RakuRegexSurrounder {
    @Override
    public String getTemplateDescription() {
        return "$<> = [] (Regex)";
    }

    @Override
    public PsiElement createAtom(Project project) {
        return RakuElementFactory.createRegexVariable(project);
    }

    @Override
    public TextRange postprocess(Project project, Editor editor, PsiElement regexAtom) {
        RakuVariable variable = PsiTreeUtil.findChildOfType(regexAtom, RakuVariable.class);
        if (variable == null) return null;

        int offset = variable.getTextOffset() + 2;
        editor.getCaretModel().moveToOffset(offset);
        editor.getDocument().deleteString(offset, offset + 1);
        return new TextRange(offset, offset);
    }
}
