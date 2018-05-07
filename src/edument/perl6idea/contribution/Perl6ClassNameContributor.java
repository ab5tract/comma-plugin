package edument.perl6idea.contribution;

import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6File;

import java.util.Map;

public class Perl6ClassNameContributor extends Perl6NameContributor {
    protected Map<String, PsiElement> getNamesFromFile(Perl6File file, String prefix) {
        return file.getTypeLike(prefix);
    }
}
