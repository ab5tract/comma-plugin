package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6TypeNameStub;

public interface Perl6TypeName extends Perl6PsiElement, StubBasedPsiElement<Perl6TypeNameStub> {
    String getTypeName();
}
