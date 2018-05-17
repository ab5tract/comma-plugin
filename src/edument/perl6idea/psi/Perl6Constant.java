package edument.perl6idea.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6ConstantStub;

public interface Perl6Constant extends Perl6PsiElement, Perl6PsiDeclaration,
                                       PsiNameIdentifierOwner, StubBasedPsiElement<Perl6ConstantStub> {
    String getConstantName();
}
