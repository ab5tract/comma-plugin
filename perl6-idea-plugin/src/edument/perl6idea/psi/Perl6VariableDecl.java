package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6VariableDeclStub;
import org.jetbrains.annotations.Nullable;

public interface Perl6VariableDecl extends Perl6PsiElement, Perl6PsiDeclaration,
                                           PsiNameIdentifierOwner, StubBasedPsiElement<Perl6VariableDeclStub> {
    String getVariableName();
    boolean hasInitializer();
    @Nullable
    PsiElement getInitializer(PsiElement variable);
    @Nullable
    PsiElement getInitializer();
}
