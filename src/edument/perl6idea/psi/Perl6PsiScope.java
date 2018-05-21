package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;

import java.util.*;

public interface Perl6PsiScope extends Perl6PsiElement {
    default List<Object> walkTree(Class<?> type) {
        List<Object> elements = new ArrayList<>();
        Queue<Perl6PsiElement> visit = new LinkedList<>();
        visit.add(this);
        while (!visit.isEmpty()) {
            Perl6PsiElement current = visit.remove();
            boolean addChildren = false;
            if (current == this) {
                addChildren = true;
            } else {
                if (type.isAssignableFrom(current.getClass()))
                    elements.add(current);
                if (!(current instanceof Perl6PsiScope))
                    addChildren = true;
            }
            if (addChildren)
                for (PsiElement e : current.getChildren())
                    if (e instanceof Perl6PsiElement)
                        visit.add((Perl6PsiElement) e);
        }
        return elements;
    }

    default List<Perl6PsiDeclaration> getDeclarations() {
        List<Object> tree = walkTree(Perl6PsiDeclaration.class);
        List<Perl6PsiDeclaration> result = new ArrayList<>();
        for (Object o : tree) { result.add((Perl6PsiDeclaration) o); }
        return result;
    }

    default List<Perl6ExternalElement> getImports() {
        List<Object> tree = walkTree(Perl6ExternalElement.class);
        List<Perl6ExternalElement> result = new ArrayList<>();
        for (Object o : tree) { result.add((Perl6ExternalElement) o); }
        return result;
    }

    default void contributeExtraSymbols(Perl6SymbolCollector collector) {
    }
}
