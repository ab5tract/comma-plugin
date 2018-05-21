package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolContributor;

import java.util.*;

public interface Perl6PsiScope extends Perl6PsiElement {
    default List<Perl6SymbolContributor> getSymbolContributors() {
        List<Perl6SymbolContributor> results = new ArrayList<>();
        Queue<Perl6PsiElement> visit = new LinkedList<>();
        visit.add(this);
        while (!visit.isEmpty()) {
            Perl6PsiElement current = visit.remove();
            boolean addChildren = false;
            if (current == this) {
                addChildren = true;
            } else {
                if (current instanceof Perl6SymbolContributor)
                    results.add((Perl6SymbolContributor)current);
                if (!(current instanceof Perl6PsiScope))
                    addChildren = true;
            }
            if (addChildren)
                for (PsiElement e : current.getChildren())
                    if (e instanceof Perl6PsiElement)
                        visit.add((Perl6PsiElement) e);
        }
        return results;
    }

    default List<Perl6PsiDeclaration> getDeclarations() {
        List<Perl6PsiDeclaration> decls = new ArrayList<>();
        Queue<Perl6PsiElement> visit = new LinkedList<>();
        visit.add(this);
        while (!visit.isEmpty()) {
            Perl6PsiElement current = visit.remove();
            boolean addChildren = false;
            if (current == this) {
                addChildren = true;
            } else {
                if (current instanceof Perl6PsiDeclaration)
                    decls.add((Perl6PsiDeclaration)current);
                if (!(current instanceof Perl6PsiScope))
                    addChildren = true;
            }
            if (addChildren)
                for (PsiElement e : current.getChildren())
                    if (e instanceof Perl6PsiElement)
                        visit.add((Perl6PsiElement) e);
        }
        return decls;
    }

    default void contributeExtraSymbols(Perl6SymbolCollector collector) {
    }
}
