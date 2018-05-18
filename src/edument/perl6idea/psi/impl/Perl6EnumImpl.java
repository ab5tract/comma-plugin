package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Enum;
import edument.perl6idea.psi.Perl6TypeStubBasedPsi;
import edument.perl6idea.psi.stub.Perl6EnumStub;
import edument.perl6idea.psi.stub.Perl6EnumStubElementType;
import org.jetbrains.annotations.NotNull;

public class Perl6EnumImpl extends Perl6TypeStubBasedPsi<Perl6EnumStub> implements Perl6Enum {
    public Perl6EnumImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6EnumImpl(Perl6EnumStub stub, Perl6EnumStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getEnumName() {
        return getName();
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:ENUM)";
    }
}
