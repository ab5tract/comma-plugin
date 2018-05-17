package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;
import org.jetbrains.annotations.NotNull;

public class Perl6ScopedDeclImpl extends StubBasedPsiElementBase<Perl6ScopedDeclStub> implements Perl6ScopedDecl {
    public Perl6ScopedDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6ScopedDeclImpl(final Perl6ScopedDeclStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getScope() {
        Perl6ScopedDeclStub stub = this.getStub();
        if (stub != null)
            return stub.getScope();

        String text = getText();
        if (text.startsWith("my")) return "my";
        if (text.startsWith("has")) return "has";
        if (text.startsWith("our")) return "our";
        if (text.startsWith("augment")) return "augment";
        return "";
    }

    public String getTypeLikeName() {
        PsiElement subDeclarator = findChildByType(Perl6ElementTypes.ROUTINE_DECLARATION);
        if (subDeclarator != null) return ((Perl6RoutineDecl)subDeclarator).getRoutineName();
        PsiElement declarator = findChildByType(Perl6ElementTypes.PACKAGE_DECLARATION);
        if (declarator != null) return ((Perl6PackageDecl)declarator).getPackageName();
        PsiElement enumElement = findChildByType(Perl6ElementTypes.ENUM);
        if (enumElement != null) return ((Perl6EnumImpl)enumElement).getEnumName();
        PsiElement subset = findChildByType(Perl6ElementTypes.SUBSET);
        if (subset != null) return ((Perl6SubsetImpl)subset).getSubsetName();
        PsiElement variable = findChildByType(Perl6ElementTypes.VARIABLE_DECLARATION);
        if (variable != null) return ((Perl6VariableDeclImpl)variable).getName();
        return "<anon>";
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:SCOPED_DECLARATION)";
    }
}
