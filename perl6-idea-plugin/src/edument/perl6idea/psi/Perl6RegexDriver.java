package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.*;

public interface Perl6RegexDriver extends PsiElement {
    default Collection<PsiNamedElement> collectRegexVariables() {
        if (!(this instanceof Perl6PsiElement))
            return new ArrayList<>();
        Perl6Regex regex = PsiTreeUtil.findChildOfType(this, Perl6Regex.class, false);
        if (regex == null) return new ArrayList<>();
        List<PsiNamedElement> symbols = new ArrayList<>();
        // Positionals
        Deque<PsiElement> toWalk = new ArrayDeque<>(Arrays.asList(regex.getChildren()));
        while (!toWalk.isEmpty()) {
            PsiElement atom = toWalk.removeFirst();
            PsiElement firstChild = atom.getFirstChild();
            if (firstChild instanceof Perl6RegexCapturePositional) {
                symbols.add(((Perl6RegexCapturePositional)firstChild));
            } else if (firstChild instanceof Perl6RegexGroup) {
                toWalk.addAll(Arrays.asList(firstChild.getChildren()));
            }
        }
        // Nameds
        Collection<PsiNamedElement> nameds = PsiTreeUtil.findChildrenOfAnyType(regex, Perl6RegexVariable.class, Perl6RegexAssertion.class);
        for (PsiNamedElement named : nameds) {
            if (named instanceof Perl6RegexAssertion && named.getText().matches("^<\\w.*")) {
                if (PsiTreeUtil.getParentOfType(named, Perl6RegexCapturePositional.class, Perl6Regex.class) instanceof Perl6Regex
                    && named.getName() != null)
                    symbols.add(named);
            } else if (named instanceof Perl6RegexVariable)
                symbols.add(named);
        }
        return symbols;
    }
}
