package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
                    if (ident != null && ident.getText().equals(var.getText()))
                        return ident;
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
