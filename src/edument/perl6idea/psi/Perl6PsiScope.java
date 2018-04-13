package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

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

    default List<Perl6PsiElement> getDeclarations() {
        List<Object> tree = walkTree(Perl6PsiDeclaration.class);
        List<Perl6PsiElement> result = new ArrayList<>();
        for (Object o : tree) { result.add((Perl6PsiElement) o); }
        return result;
    }

    default List<Perl6ExternalElement> getImports() {
        List<Object> tree = walkTree(Perl6ExternalElement.class);
        List<Perl6ExternalElement> result = new ArrayList<>();
        for (Object o : tree) { result.add((Perl6ExternalElement) o); }
        return result;
    }

    default Map<String, Perl6TypeLike> getTypeLike(String prefix) {
        return gatherTypes(prefix, new HashMap<>(), this);
    }

    default Map<String, Perl6TypeLike> gatherTypes(String prefix, Map<String, Perl6TypeLike> elements, PsiElement scope) {
        for (PsiElement element : scope.getChildren()) {
            if (element instanceof Perl6Subset || element instanceof Perl6Enum || element instanceof Perl6PackageDecl) {
                String name = ((Perl6TypeLike)element).getTypeLikeName();
                if (name.toUpperCase().equals(name)) continue;
                elements.put(prefix + "::" + name, (Perl6TypeLike)element);
                if (element instanceof Perl6PackageDecl)
                    gatherTypes(prefix + "::" + name, elements, element);
            } else if (element instanceof Perl6ScopedDecl) {
                if (!(element.getLastChild() instanceof Perl6TypeLike)) continue;
                String name = ((Perl6TypeLike)element).getTypeLikeName();
                if (name.toUpperCase().equals(name)) continue;
                String ident = "";
                switch (((Perl6ScopedDecl) element).getScope()) {
                    case "our":
                        ident = prefix + "::" + name; break;
                    case "my":
                        ident = String.format("%s (lexical in %s)", name, prefix); break;
                    case "augment":
                    case "state":
                    case "anon":
                    case "has":
                    case "temp":
                    case "let":
                        continue;
                }
                elements.put(ident, (Perl6TypeLike)element);
                Perl6PackageDecl decl = PsiTreeUtil.findChildOfType(element, Perl6PackageDecl.class);
                if (decl != null) gatherTypes(ident, elements, decl);
            } else {
                gatherTypes(prefix, elements, element);
            }
        }
        return elements;
    }
}
