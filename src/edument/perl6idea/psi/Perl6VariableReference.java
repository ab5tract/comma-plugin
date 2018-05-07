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

import static edument.perl6idea.parsing.Perl6TokenTypes.STATEMENT_CONTROL;

public class Perl6VariableReference extends PsiReferenceBase<Perl6PsiElement> {
    private static final String[] ALWAYS_PRESENT_VARS = new String[]{"$_", "$/", "$!", "$?FILE", "$?LINE", "$?LANG", "%?RESOURCES", "$?PACKAGE", "$=pod", "$=finish"};
    private static final Set<String> TOPICALIZERS = new HashSet<>(Arrays.asList("with", "without", "given", "for"));
    private static final Set<String> ALWAYS_DECLARED_VARS = new HashSet<>(Arrays.asList("$_", "$/", "$!"));

    public Perl6VariableReference(Perl6Variable var) {
        super(var, new TextRange(0, var.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Perl6Variable var = (Perl6Variable)getElement();
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(var, Perl6PsiScope.class);

        // Check declarations of innermost block
        if (scope != null) {
            for (Perl6PsiElement decl : scope.getDeclarations())
                if (decl instanceof Perl6VariableDecl || decl instanceof Perl6Constant)
                    if (checkDeclaration(var, decl)) return ((PsiNameIdentifierOwner)decl).getNameIdentifier();
        }

        // Without explicit definition, search for simple topicalizers for always declared variables
        if (scope != null && ALWAYS_DECLARED_VARS.contains(var.getVariableName()))
            for (PsiElement e = scope.getPrevSibling(); e != null; e = e.getPrevSibling())
                if (e.getNode().getElementType().equals(STATEMENT_CONTROL) && TOPICALIZERS.contains(e.getText()))
                    if (!(scope instanceof Perl6PointyBlock))
                        return e;
                    else
                        for (Perl6Parameter param : ((Perl6PointyBlock) scope).getParams())
                            if (param.getVariableName().equals(var.getVariableName()))
                                return param;

        // If topic variable is not resolved yet, return outer routine or one of its parameters
        if (ALWAYS_DECLARED_VARS.contains(var.getVariableName()) && scope != null) {
            for (Perl6PsiElement decl : scope.getDeclarations())
                if (decl instanceof Perl6ParameterVariable && checkDeclaration(var, decl))
                    return ((PsiNameIdentifierOwner)decl).getNameIdentifier();
            // If it is routine already, return it
            if (scope instanceof Perl6RoutineDecl) return scope;
            // If it is just a Block, find nearest routine parent
            return PsiTreeUtil.getParentOfType(scope, Perl6RoutineDecl.class);
        }

        // Check declarations of outer scopes for normal variables
        while (scope != null) {
            for (Perl6PsiElement decl : scope.getDeclarations())
                if (decl instanceof Perl6VariableDecl || decl instanceof Perl6Constant || decl instanceof Perl6ParameterVariable)
                    if (checkDeclaration(var, decl)) return ((PsiNameIdentifierOwner)decl).getNameIdentifier();
            scope = PsiTreeUtil.getParentOfType(scope, Perl6PsiScope.class);
        }

        // Check attributes
        Perl6PackageDeclImpl outerPackage = PsiTreeUtil.getParentOfType(var, Perl6PackageDeclImpl.class);
        if (outerPackage != null)
            for (Perl6PsiElement element : outerPackage.getDeclarations()) {
                if (!(element instanceof Perl6VariableDecl)) continue;
                if (element.getName().replace(".", "!").equals(var.getVariableName()))
                    return element;
            }

        return null;
    }

    private boolean checkDeclaration(Perl6Variable var, Perl6PsiElement decl) {
        PsiElement ident = ((PsiNameIdentifierOwner)decl).getNameIdentifier();
        return ident != null && ident.getText().equals(var.getText());
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
            List<Perl6ExternalElement> extern = scope.getImports();
            for (Perl6ExternalElement ex : extern)
                results.addAll(ex.getExternallyDeclaredNames(getElement().getProject()));
            for (Perl6PsiElement decl : scope.getDeclarations()) {
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
