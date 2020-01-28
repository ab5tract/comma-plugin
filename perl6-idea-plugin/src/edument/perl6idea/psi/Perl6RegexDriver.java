package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Perl6RegexDriver {
    default Collection<PsiNamedElement> collectRegexVariables() {
        if (!(this instanceof Perl6PsiElement))
            return new ArrayList<>();
        Collection<Perl6RegexAtom> atoms = PsiTreeUtil.findChildrenOfType((PsiElement)this, Perl6RegexAtom.class);
        List<PsiNamedElement> symbols = new ArrayList<>();
        for (Perl6RegexAtom atom : atoms) {
            PsiElement firstChild = atom.getFirstChild();
            if (firstChild instanceof Perl6RegexCapturePositional) {
                symbols.add(((Perl6RegexCapturePositional)firstChild));
            } else if (firstChild instanceof Perl6RegexVariable) {
                symbols.add((Perl6RegexVariable)firstChild);
            } else if (firstChild instanceof Perl6RegexAssertion && firstChild.getText().matches("^<\\w.*")) {
                Perl6RegexAssertion regexAssertion = (Perl6RegexAssertion)firstChild;
                if (regexAssertion.getName() != null)
                    symbols.add(regexAssertion);
            }
        }
        return symbols;
    }
}
