package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

public class Perl6ScopedDeclImpl extends Perl6SymbolLike implements Perl6ScopedDecl {
    public Perl6ScopedDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getScope() {
        String text = getText();
        if (text.startsWith("my")) return "my";
        if (text.startsWith("has")) return "has";
        if (text.startsWith("our")) return "our";
        if (text.startsWith("augment")) return "augment";
        return "";
    }

    public String getTypeLikeName() {
        PsiElement declarator = findChildByType(Perl6ElementTypes.PACKAGE_DECLARATION);
        if (declarator != null) return ((Perl6PackageDecl)declarator).getPackageName();
        PsiElement enumElement = findChildByType(Perl6ElementTypes.ENUM);
        if (enumElement != null) return ((Perl6EnumImpl)enumElement).getTypeLikeName();
        PsiElement subset = findChildByType(Perl6ElementTypes.SUBSET);
        if (subset != null) return ((Perl6SubsetImpl)subset).getTypeLikeName();
        PsiElement variable = findChildByType(Perl6ElementTypes.VARIABLE_DECLARATION);
        if (variable != null) return ((Perl6VariableDeclImpl)variable).getName();
        return "<anon>";
    }
}
