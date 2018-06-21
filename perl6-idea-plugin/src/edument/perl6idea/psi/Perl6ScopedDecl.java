package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;

public interface Perl6ScopedDecl extends Perl6PsiElement, StubBasedPsiElement<Perl6ScopedDeclStub> {
    String getScope();
}
