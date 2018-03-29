package edument.perl6idea.psi;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.impl.Perl6PackageDeclImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Perl6VariableReference extends PsiReferenceBase<Perl6PsiElement> {
    private static final String[] ALWAYS_PRESENT_VARS = new String[]{"$?FILE", "$?LINE", "$?LANG", "%?RESOURCES", "$?PACKAGE", "$=pod", "$=finish"};

    public Perl6VariableReference(Perl6Variable var) {
        super(var, new TextRange(0, var.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6Variable var = (Perl6Variable)getElement();
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(var, Perl6PsiScope.class);
        while (scope != null) {
            List<Perl6PsiElement> decls = scope.getDeclarations();
            for (Perl6PsiElement decl : decls) {
                if (decl instanceof Perl6VariableDecl || decl instanceof Perl6ParameterVariable || decl instanceof Perl6Constant) {
                    PsiElement ident = ((PsiNameIdentifierOwner)decl).getNameIdentifier();
                    if (ident != null && ident.getText().equals(var.getText()))
                        return ident;
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }
        Perl6PackageDeclImpl outerPackage = PsiTreeUtil.getParentOfType(var, Perl6PackageDeclImpl.class);
        if (outerPackage != null)
            for (Perl6PsiElement element : outerPackage.getDeclarations()) {
                if (!(element instanceof Perl6VariableDecl)) continue;
                if (element.getName().replace(".", "!").equals(var.getVariableName()))
                    return element;
            }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Perl6Variable var = (Perl6Variable)getElement();

        HashMap<String, Perl6PsiElement> outer = new HashMap<>();
        HashSet<String> seen = new HashSet<>();
        List<Object> results = new ArrayList<>(Arrays.asList(ALWAYS_PRESENT_VARS));
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(var, Perl6PsiScope.class);
        while (scope != null) {
            List<Perl6PsiElement> decls = scope.getDeclarations();
            for (Perl6PsiElement decl : decls) {
                if (decl instanceof Perl6VariableDecl || decl instanceof Perl6ParameterVariable || decl instanceof Perl6Constant) {
                    PsiElement ident = ((PsiNameIdentifierOwner)decl).getNameIdentifier();
                    if (ident != null) {
                        String name = ident.getText();
                        Perl6ScopedDecl scopeDecl = PsiTreeUtil.getParentOfType(ident, Perl6ScopedDecl.class);
                        if (scopeDecl != null && scopeDecl.getScope().equals("has")) continue;
                        if (ident.getNode().getStartOffset() < var.getNode().getStartOffset())
                            outer.put(name, decl);
                        else
                            seen.add(name);
                    }
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }
        Perl6PackageDeclImpl outerPackage = PsiTreeUtil.getParentOfType(var, Perl6PackageDeclImpl.class);
        if (outerPackage != null)
            for (Perl6PsiElement element : outerPackage.getDeclarations()) {
                if (!(element instanceof Perl6VariableDecl)) continue;
                Perl6ScopedDecl scopeDecl = PsiTreeUtil.getParentOfType(element, Perl6ScopedDecl.class);
                if (scopeDecl != null && scopeDecl.getScope().equals("has")) {
                    // No null is possible here as implementation returns empty string in worst case
                    outer.put(element.getName().replace('.', '!'), element);
                    outer.put(element.getName(), element);
                }
            }
        for (Map.Entry<String, Perl6PsiElement> entry : outer.entrySet()) {
            if (!seen.contains(entry.getKey())) {
                    results.add(LookupElementBuilder.create(entry.getValue(), entry.getKey()));
            } else {
                results.add(LookupElementBuilder.create(entry.getValue(), entry.getKey()).strikeout());
            }
        }

        Perl6PackageDecl packageDecl = PsiTreeUtil.getParentOfType(var, Perl6PackageDecl.class);
        if (packageDecl != null)
            switch (packageDecl.getPackageKind()) {
                case "class": results.add("$?CLASS");
                case "role":  results.add("$?ROLE");
            }

        return results.toArray();
    }
}
