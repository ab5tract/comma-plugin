package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public interface Perl6PsiScope extends Perl6PsiElement {
    default List<Perl6PsiElement> getDeclarations() {
        List<Perl6PsiElement> declarations = new ArrayList<>();
        System.err.println("Getting declarations of " + this.toString());
        try {
            Queue<Perl6PsiElement> visit = new LinkedList<>();
            visit.add(this);
            while (!visit.isEmpty()) {
                Perl6PsiElement current = visit.remove();
                //System.err.println("Took from queue " + current.toString());
                boolean addChildren = false;
                if (current == this) {
                    addChildren = true;
                } else {
                    //System.err.println("Considering child " + current.toString());
                    if (current instanceof Perl6PsiDeclaration) {
                        declarations.add(current);
                        System.err.println("Adding declaration " + current.toString());
                    }
                    if (!(current instanceof Perl6PsiScope))
                        addChildren = true;
                }
                if (addChildren)
                    for (PsiElement e : current.getChildren())
                        if (e instanceof Perl6PsiElement) {
                            //System.err.println("Adding to queue " + e.toString());
                            visit.add((Perl6PsiElement) e);
                        }
            }
        }
        catch (Throwable t) {
            System.err.println(t.toString());
        }
        return declarations;
    }
}
