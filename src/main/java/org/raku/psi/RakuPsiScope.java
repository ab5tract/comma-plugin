package org.raku.psi;

import com.intellij.psi.PsiElement;
import org.raku.psi.symbols.RakuSymbolCollector;
import org.raku.psi.symbols.RakuLexicalSymbolContributor;

import java.util.*;

public interface RakuPsiScope extends RakuPsiElement {
    default List<RakuLexicalSymbolContributor> getSymbolContributors() {
        List<RakuLexicalSymbolContributor> results = new ArrayList<>();
        Queue<RakuPsiElement> visit = new LinkedList<>();
        visit.add(this);
        while (!visit.isEmpty()) {
            RakuPsiElement current = visit.remove();
            boolean addChildren = false;
            if (current == this) {
                addChildren = true;
            } else {
                if (current instanceof RakuLexicalSymbolContributor)
                    results.add((RakuLexicalSymbolContributor)current);
                if (!(current instanceof RakuPsiScope))
                    addChildren = true;
            }
            if (addChildren)
                for (PsiElement e : current.getChildren())
                    if (e instanceof RakuPsiElement)
                        visit.add((RakuPsiElement) e);
        }
        return results;
    }

    default List<RakuPsiDeclaration> getDeclarations() {
        List<RakuPsiDeclaration> decls = new ArrayList<>();
        Queue<RakuPsiElement> visit = new LinkedList<>();
        visit.add(this);
        while (!visit.isEmpty()) {
            RakuPsiElement current = visit.remove();
            boolean addChildren = false;
            if (current == this) {
                addChildren = true;
            } else {
                if (current instanceof RakuPsiDeclaration)
                    decls.add((RakuPsiDeclaration)current);
                if (!(current instanceof RakuPsiScope))
                    addChildren = true;
            }
            if (addChildren)
                for (PsiElement e : current.getChildren())
                    if (e instanceof RakuPsiElement)
                        visit.add((RakuPsiElement) e);
        }
        return decls;
    }

    default void contributeScopeSymbols(RakuSymbolCollector collector) {
    }
}
