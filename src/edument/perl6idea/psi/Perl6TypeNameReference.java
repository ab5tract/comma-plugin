package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.Stack;
import edument.perl6idea.contribution.Perl6ClassNameContributor;
import edument.perl6idea.contribution.Perl6SymbolNameContributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class Perl6TypeNameReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6TypeNameReference(@NotNull Perl6PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(getElement(), Perl6PsiScope.class);
        if (scope == null) return null;
        // Try out single resolution, works out for Foo and Foo::Bar cases
        PsiElement result = singleResolution(scope);
        if (result != null) return result;
        // Try to resolve name where each part is another node
        return multipleResolution(getElement().getText().split("::"), scope);
    }

    private PsiElement multipleResolution(String[] parts, Perl6PsiScope scope) {
        Perl6PsiElement root = null;
        // local check if type is defined in current file
        // inner sets if we're resolving Foo->Bar (referring to inner class inside of another)
        // or Bar<-Foo (referring to outer class)
        boolean local = false, inner = false;
        for (Perl6PsiElement decl : scope.getDeclarations()) {
            if (!(decl instanceof Perl6PackageDecl)) continue;
            if (((Perl6PackageDecl) decl).getPackageName().equals(parts[0]) ||
                    ((Perl6PackageDecl) decl).getPackageName().equals(parts[parts.length - 1])) {
                root = decl;
                local = true;
                inner = ((Perl6PackageDecl) decl).getPackageName().equals(parts[0]);
            }
        }
        if (local) return localMultipleResolution(parts, root, inner);
        return externalResolution();
    }

    private PsiElement externalResolution() {
        PsiElement element = getElement();
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(element, Perl6PsiScope.class);
        Set<String> includes = new HashSet<>();
        while (scope != null) {
            for (Perl6ExternalElement i : scope.getImports())
                includes.add(i.getModuleName());
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }
        return null; // XXX Rewrite later
    }

    @Nullable
    private PsiElement localMultipleResolution(String[] parts, Perl6PsiElement root, boolean inner) {
        Stack<String> stack;
        if (inner) {
            List<String> partsList = new LinkedList<>(Arrays.asList(parts));
            Collections.reverse(partsList);
            stack = new Stack<>(partsList);
        } else stack = new Stack<>(parts);
        stack.pop();
        if (inner) return innerDirectionLocalLookup(root, stack);
        else return outerDirectionLocalLookup(root, stack);
    }

    @Nullable
    private PsiElement outerDirectionLocalLookup(Perl6PsiElement root, Stack<String> stack) {
        Perl6PsiElement originalRoot = root;
        while (!stack.isEmpty()) {
            root = PsiTreeUtil.getParentOfType(root, Perl6PackageDecl.class);
            if (root == null) return null;
            if (!((Perl6PackageDecl)root).getPackageName().equals(stack.pop())) {
                if (stack.isEmpty()) return null;
                else break;
            }
        }
        return stack.isEmpty() ? originalRoot : null;
    }

    @Nullable
    private PsiElement innerDirectionLocalLookup(Perl6PsiElement root, Stack<String> stack) {
        while (!stack.isEmpty()) {
            boolean updated = false;
            String piece = stack.pop();
            Collection<Perl6PackageDecl> decls = PsiTreeUtil.findChildrenOfType(root, Perl6PackageDecl.class);
            // For in while loop, but should be safe as outer loop has small N generally
            for (Perl6PsiElement decl : decls) {
                if (!(decl instanceof Perl6PackageDecl)) continue;
                if (((Perl6PackageDecl) decl).getPackageName().equals(piece)) {
                    if (stack.isEmpty()) return decl;
                    updated = true;
                    root = decl;
                    break;
                }
            }
            // Such piece part was not found in package
            if (!updated) break;
        }
        return null;
    }

    private PsiElement singleResolution(Perl6PsiScope scope) {
        while (scope != null) {
            for (Perl6PsiElement decl : scope.getDeclarations()) {
                if (!(decl instanceof Perl6PackageDecl)) continue;
                if (getElement().getText().equals(((Perl6PackageDecl) decl).getPackageName())) return decl;
            }
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }
        return externalResolution();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        // External names.
        Perl6ClassNameContributor contributor = new Perl6ClassNameContributor();
        String[] names = contributor.getNames(getElement().getProject(), false);
        Stream<String> exported = Stream.of(names);
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(getElement(), Perl6PsiScope.class);
        List<String> moduleNames = new ArrayList<>();
        while (scope != null) {
            for (Perl6ExternalElement importModule : scope.getImports())
                moduleNames.add(importModule.getModuleName());
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }
        Stream<String> nonLexical = exported.filter(s -> !s.contains("lexical"));
        nonLexical = nonLexical.filter(
                s -> {
                    boolean used = false;
                    for (String name : moduleNames) if (s.startsWith(name)) used = true;
                    return used;
                });
        return nonLexical.toArray();
    }
}
