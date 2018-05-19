package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6ConstantStub;

public interface Perl6Constant extends Perl6PsiDeclaration, StubBasedPsiElement<Perl6ConstantStub> {
    String getConstantName();
}
