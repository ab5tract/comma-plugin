package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6Enum;
import edument.perl6idea.psi.Perl6SymbolLike;
import edument.perl6idea.psi.stub.Perl6EnumStub;
import edument.perl6idea.psi.stub.Perl6EnumStubElementType;
import org.jetbrains.annotations.NotNull;

public class Perl6EnumImpl extends StubBasedPsiElementBase<Perl6EnumStub> implements Perl6Enum {
    public Perl6EnumImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6EnumImpl(Perl6EnumStub stub, Perl6EnumStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getEnumName() {
        Perl6EnumStub stub = getStub();
        if (stub != null)
            return stub.getEnumName();
        PsiElement name = findChildByType(Perl6TokenTypes.NAME);
        PsiElement longName = findChildByType(Perl6ElementTypes.LONG_NAME);
        return name == null ? longName == null ? "<anon>" : longName.getText() : name.getText();
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:ENUM)";
    }
}
