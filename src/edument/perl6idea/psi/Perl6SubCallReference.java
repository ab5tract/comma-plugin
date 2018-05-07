package edument.perl6idea.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.impl.Perl6SubCallImpl;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Perl6SubCallReference extends PsiReferenceBase<Perl6PsiElement> {
    public Perl6SubCallReference(Perl6SubCallImpl call) {
        super(call, new TextRange(0, call.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6SubCall call = (Perl6SubCall)getElement();
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(call, Perl6PsiScope.class);
        while (scope != null) {
            for (Perl6ExternalElement ex : scope.getImports()) {
                List<Perl6PsiElement> localDecls = ex.getLocalDeclarations(getElement().getProject());
                for (Perl6PsiElement decl : localDecls) {
                    if (!(decl instanceof Perl6RoutineDecl)) continue;
                    if (((Perl6RoutineDecl) decl).getRoutineName().equals(call.getCallName()) &&
                            ((Perl6RoutineDecl) decl).getTraits().contains("is export"))
                        return ((Perl6RoutineDecl) decl).getNameIdentifier();
                }
            }
            for (Perl6PsiElement decl : scope.getDeclarations()) {
                if (decl instanceof Perl6RoutineDecl) {
                    PsiElement ident = ((PsiNameIdentifierOwner)decl).getNameIdentifier();
                    if (ident != null && ident.getText().equals(call.getCallName()))
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
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(getElement(), Perl6PsiScope.class);
        List<Object> results = new ArrayList<>(Perl6SdkType.getInstance().getSymbols());
        while (scope != null) {
            List<Perl6ExternalElement> extern = scope.getImports();
            for (Perl6PsiElement decl : scope.getDeclarations()) {
                if (decl instanceof Perl6RoutineDecl)
                    results.add(decl.getName());
            }
            for (Perl6ExternalElement ex : extern) {
                results.addAll(ex.getExternallyDeclaredNames(getElement().getProject()));
            }
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }
        return results.toArray();
    }
}
