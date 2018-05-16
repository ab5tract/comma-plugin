package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6SubsetStub;

public interface Perl6Subset extends Perl6PsiElement, Perl6PsiDeclaration,
                                     StubBasedPsiElement<Perl6SubsetStub> {
    String getSubsetName();
}
