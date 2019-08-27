package edument.perl6idea.surrountWith.descriptors;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Variable;

public class Perl6RegexNamedSurrounder implements Perl6RegexSurrounder {
    @Override
    public String getTemplateDescription() {
        return "$<> = [] (Regex)";
    }

    @Override
    public PsiElement createAtom(Project project) {
        return Perl6ElementFactory.createRegexVariable(project);
    }

    @Override
    public TextRange postprocess(Project project, Editor editor, PsiElement regexAtom) {
        Perl6Variable variable = PsiTreeUtil.findChildOfType(regexAtom, Perl6Variable.class);
        if (variable == null) return null;

        int offset = variable.getTextOffset() + 2;
        editor.getCaretModel().moveToOffset(offset);
        editor.getDocument().deleteString(offset, offset + 1);
        return new TextRange(offset, offset);
    }
}
