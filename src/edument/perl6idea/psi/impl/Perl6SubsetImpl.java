package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6PresentableStub;
import edument.perl6idea.psi.Perl6Subset;
import edument.perl6idea.psi.stub.Perl6SubsetStub;
import edument.perl6idea.psi.stub.Perl6SubsetStubElementType;
import org.jetbrains.annotations.NotNull;

public class Perl6SubsetImpl extends Perl6PresentableStub<Perl6SubsetStub> implements Perl6Subset {
    public Perl6SubsetImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6SubsetImpl(Perl6SubsetStub stub, Perl6SubsetStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getSubsetName() {
        Perl6SubsetStub stub = getStub();
        if (stub != null)
            return stub.getSubsetName();
        PsiElement name = findChildByType(Perl6TokenTypes.NAME);
        PsiElement longName = findChildByType(Perl6ElementTypes.LONG_NAME);
        return name == null ? longName == null ? "<anon>" : longName.getText() : name.getText();
    }
}
