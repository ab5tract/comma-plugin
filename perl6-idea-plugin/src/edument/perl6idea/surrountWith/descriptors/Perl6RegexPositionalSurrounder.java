package edument.perl6idea.surrountWith.descriptors;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6ElementFactory;

public class Perl6RegexPositionalSurrounder implements Perl6RegexSurrounder {
    @Override
    public String getTemplateDescription() {
        return "( ) (Regex)";
    }

    @Override
    public PsiElement createAtom(Project project) {
        return Perl6ElementFactory.createRegexGroup(project, true);
    }
}

