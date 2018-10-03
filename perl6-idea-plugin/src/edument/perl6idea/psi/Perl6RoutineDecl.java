package edument.perl6idea.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;

public interface Perl6RoutineDecl extends Perl6PsiScope, Perl6PsiDeclaration,
                                          StubBasedPsiElement<Perl6RoutineDeclStub>,
                                          Perl6SignatureHolder, PsiNamedElement {
    String getRoutineKind();
    String getRoutineName();
    boolean isPrivate();
}
