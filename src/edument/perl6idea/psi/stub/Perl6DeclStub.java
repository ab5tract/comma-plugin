package edument.perl6idea.psi.stub;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6PsiDeclaration;

public interface Perl6DeclStub<T extends PsiElement & Perl6PsiDeclaration> extends StubElement<T> {
    String getScope();
    boolean isExported();
}
