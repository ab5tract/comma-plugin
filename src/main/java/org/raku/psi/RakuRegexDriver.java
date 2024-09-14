package org.raku.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.*;

public interface RakuRegexDriver extends PsiElement {
    default Collection<PsiNamedElement> collectRegexVariables() {
        if (!(this instanceof RakuPsiElement))
            return new ArrayList<>();
        RakuRegex regex = PsiTreeUtil.findChildOfType(this, RakuRegex.class, false);
        if (regex == null) return new ArrayList<>();
        List<PsiNamedElement> symbols = new ArrayList<>();
        // Positionals
        Deque<PsiElement> toWalk = new ArrayDeque<>(Arrays.asList(regex.getChildren()));
        while (!toWalk.isEmpty()) {
            PsiElement atom = toWalk.removeFirst();
            PsiElement firstChild = atom.getFirstChild();
            if (firstChild instanceof RakuRegexCapturePositional) {
                symbols.add(((RakuRegexCapturePositional)firstChild));
            } else if (firstChild instanceof RakuRegexGroup) {
                toWalk.addAll(Arrays.asList(firstChild.getChildren()));
            }
        }
        // Nameds
        Collection<PsiNamedElement> nameds = PsiTreeUtil.findChildrenOfAnyType(regex, RakuRegexVariable.class, RakuRegexAssertion.class);
        for (PsiNamedElement named : nameds) {
            if (named instanceof RakuRegexAssertion && named.getText().matches("^<\\w.*")) {
                if (PsiTreeUtil.getParentOfType(named, RakuRegexCapturePositional.class, RakuRegex.class) instanceof RakuRegex
                    && named.getName() != null)
                    symbols.add(named);
            } else if (named instanceof RakuRegexVariable)
                symbols.add(named);
        }
        return symbols;
    }
}
