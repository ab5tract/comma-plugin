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

    default Map<String, PsiElement> getTypeLike(String prefix) {
        return gatherTypes(prefix, new HashMap<>(), this);
    }

    default Map<String, PsiElement> gatherTypes(String prefix, Map<String, PsiElement> elements, PsiElement scope) {
        for (PsiElement element : scope.getChildren()) {
            if (element instanceof Perl6Subset || element instanceof Perl6Enum || element instanceof Perl6PackageDecl) {
                String name = ((Perl6SymbolLike)element).getTypeLikeName();
                if (name.toUpperCase().equals(name)) continue;
                elements.put(prefix + "::" + name, element);
                if (element instanceof Perl6PackageDecl)
                    gatherTypes(prefix + "::" + name, elements, element);
            } else if (element instanceof Perl6ScopedDecl) {
                if (!(element.getLastChild() instanceof Perl6SymbolLike)) continue;
                String name = ((Perl6SymbolLike)element).getTypeLikeName();
                if (name.toUpperCase().equals(name)) continue;
                String ident = "";
                switch (((Perl6ScopedDecl) element).getScope()) {
                    case "our":
                        ident = prefix + "::" + name; break;
                    case "my":
                        ident = String.format("%s (lexical in %s)", name, prefix); break;
                    default: continue;
                }
                elements.put(ident, element);
                Perl6PackageDecl decl = PsiTreeUtil.findChildOfType(element, Perl6PackageDecl.class);
                if (decl != null) gatherTypes(ident, elements, decl);
            } else {
                gatherTypes(prefix, elements, element);
            }
        }
        return elements;
    }

    default Map<String, PsiElement> getSymbolLike(String prefix) {
        Map<String, PsiElement> result = gatherTypes(prefix, new HashMap<>(), this);
        result.putAll(gatherSymbols(prefix, result, this));
        return result;
    }

    // XXX Basically a copy-paste, not sure if can be prettier without high-order functions
    default Map<String,PsiElement> gatherSymbols(String prefix, Map<String, PsiElement> elements, PsiElement scope) {
        for (PsiElement element : scope.getChildren()) {
            if (element instanceof Perl6RoutineDecl || element instanceof Perl6RegexDecl || element instanceof Perl6Constant) {
                String name = ((Perl6SymbolLike)element).getTypeLikeName();
                if (name.toUpperCase().equals(name)) continue;
                elements.put(prefix + "::" + name, element);
                if (element instanceof Perl6RoutineDecl)
                    gatherSymbols(prefix + "::" + name, elements, element);
            } else if (element instanceof Perl6ScopedDecl) {
                if (!(element.getLastChild() instanceof Perl6SymbolLike)) continue;
                String name = ((Perl6SymbolLike)element).getTypeLikeName();
                if (name.toUpperCase().equals(name)) continue;
                String ident = "";
                switch (((Perl6ScopedDecl) element).getScope()) {
                    case "has": {
                        ident = prefix + "::" + name; break;
                    }
                    default: continue;
                }
                elements.put(ident, element);
                Perl6PackageDecl decl = PsiTreeUtil.findChildOfType(element, Perl6PackageDecl.class);
                if (decl != null) gatherSymbols(ident, elements, decl);
            } else {
                gatherSymbols(prefix, elements, element);
            }
        }
        return elements;
    }
}
