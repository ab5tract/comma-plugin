package edument.perl6idea.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6VariableDeclStub;

public interface Perl6VariableDecl extends Perl6PsiElement, Perl6PsiDeclaration,
                                           PsiNameIdentifierOwner, StubBasedPsiElement<Perl6VariableDeclStub> {
    String getVariableName();
    boolean hasInitializer();
}
