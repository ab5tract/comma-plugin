package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public interface Perl6PsiScope extends Perl6PsiElement {
    default List<Perl6PsiElement> getDeclarations() {
        List<Perl6PsiElement> declarations = new ArrayList<>();
        Queue<Perl6PsiElement> visit = new LinkedList<>();
        visit.add(this);
        while (!visit.isEmpty()) {
            Perl6PsiElement current = visit.remove();
            boolean addChildren = false;
            if (current == this) {
                addChildren = true;
            } else {
                if (current instanceof Perl6PsiDeclaration) {
                    declarations.add(current);
                }
                if (!(current instanceof Perl6PsiScope))
                    addChildren = true;
            }
            if (addChildren)
                for (PsiElement e : current.getChildren())
                    if (e instanceof Perl6PsiElement)
                        visit.add((Perl6PsiElement) e);
        }
        return declarations;
    }
}
