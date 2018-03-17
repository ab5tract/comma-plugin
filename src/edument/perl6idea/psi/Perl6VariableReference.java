package edument.perl6idea.psi;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
        HashMap<String, Perl6PsiElement> outer = new HashMap<>();
        HashSet<String> seen = new HashSet<>();
        List<Object> results = new ArrayList<>();
        while (scope != null) {
            List<Perl6PsiElement> decls = scope.getDeclarations();
            for (Perl6PsiElement decl : decls) {
                if (decl instanceof Perl6VariableDecl || decl instanceof Perl6ParameterVariable || decl instanceof Perl6Constant) {
                    PsiElement ident = ((PsiNameIdentifierOwner)decl).getNameIdentifier();
                    if (ident != null) {
                        String name = ident.getText();
                        if (ident.getNode().getStartOffset() < var.getNode().getStartOffset())
                            outer.put(name, decl);
                        else
                            seen.add(name);
                    }
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }
        for (Map.Entry<String, Perl6PsiElement> entry : outer.entrySet()) {
            if (!seen.contains(entry.getKey())) {
                results.add(entry.getValue());
            } else {
                LookupElement element = (LookupElement) LookupElementBuilder.create(entry.getValue()).strikeout().getObject();
                results.add(element);
            }
        }
        return results.toArray();
    }
}
