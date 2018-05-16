package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;

public interface Perl6PackageDecl extends Perl6PsiElement, Perl6PsiScope,
                                          Perl6PsiDeclaration, StubBasedPsiElement<Perl6PackageDeclStub> {
    String getPackageKind();
    String getPackageName();
    Perl6RoutineDecl[] privateMethods();
}
