package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6SubsetStub;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;

public interface Perl6Subset extends Perl6PsiDeclaration, StubBasedPsiElement<Perl6SubsetStub>,
                                     Perl6IndexableType {
    String getSubsetName();
}
