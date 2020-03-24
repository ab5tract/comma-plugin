package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Perl6RegexDriver extends PsiElement {
    default Collection<PsiNamedElement> collectRegexVariables() {
        if (!(this instanceof Perl6PsiElement))
            return new ArrayList<>();
        Perl6Regex regex = PsiTreeUtil.findChildOfType(this, Perl6Regex.class, false);
        if (regex == null) return new ArrayList<>();
        List<PsiNamedElement> symbols = new ArrayList<>();
        for (PsiElement atom : regex.getChildren()) {
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
