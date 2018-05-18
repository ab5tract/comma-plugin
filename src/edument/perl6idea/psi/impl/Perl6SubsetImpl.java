package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6TypeStubBasedPsi;
import edument.perl6idea.psi.Perl6Subset;
import edument.perl6idea.psi.stub.Perl6SubsetStub;
import edument.perl6idea.psi.stub.Perl6SubsetStubElementType;
import org.jetbrains.annotations.NotNull;

public class Perl6SubsetImpl extends Perl6TypeStubBasedPsi<Perl6SubsetStub> implements Perl6Subset {
    public Perl6SubsetImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6SubsetImpl(Perl6SubsetStub stub, Perl6SubsetStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getSubsetName() {
        return getName();
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:SUBSET)";
    }
}
