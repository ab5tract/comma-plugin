package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Perl6VariableReference extends PsiReferenceBase<Perl6PsiElement> {
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
                if (decl instanceof PsiNameIdentifierOwner) {
                    PsiElement ident = ((PsiNameIdentifierOwner)decl).getNameIdentifier();
                    if (ident != null) {
                        if (ident.getText().equals(var.getText()))
                            return ident;
                        Perl6ScopedDecl scopeDecl = PsiTreeUtil.getParentOfType(ident, Perl6ScopedDecl.class);
                        if (scopeDecl != null && scopeDecl.getText().startsWith("has"))
                            if (ident.getText().replace(".", "!").equals(var.getText()))
                                return ident;
                    }
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Perl6Variable var = (Perl6Variable)getElement();
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(var, Perl6PsiScope.class);
        Set<String> seen = new HashSet<>();
        List<PsiElement> results = new ArrayList<>();
        while (scope != null) {
            List<Perl6PsiElement> decls = scope.getDeclarations();
            for (Perl6PsiElement decl : decls) {
                if (decl instanceof Perl6VariableDecl || decl instanceof Perl6ParameterVariable || decl instanceof Perl6Constant) {
                    PsiElement ident = ((PsiNameIdentifierOwner)decl).getNameIdentifier();
                    if (ident != null) {
                        String name = ident.getText();
                        if (!seen.contains(name)) {
                            results.add(decl);
                            seen.add(name);
                        }
                    }
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }
        return results.toArray();
    }
}
