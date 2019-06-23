package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6SubCallStub;

public interface Perl6SubCall extends Perl6PsiElement, P6CodeBlockCall, P6Extractable,
                                      StubBasedPsiElement<Perl6SubCallStub> {
}
