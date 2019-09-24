package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6VariableDeclStub;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;
import edument.perl6idea.psi.symbols.Perl6MOPSymbolContributor;
import org.jetbrains.annotations.Nullable;

public interface Perl6VariableDecl extends Perl6PsiElement, Perl6PsiDeclaration,
                                           PsiNameIdentifierOwner, StubBasedPsiElement<Perl6VariableDeclStub>,
                                           Perl6LexicalSymbolContributor, Perl6MOPSymbolContributor {
    String[] getVariableNames();
    Perl6Variable[] getVariables();
    boolean hasInitializer();
    @Nullable
    PsiElement getInitializer(Perl6Variable variable);
    @Nullable
    PsiElement getInitializer();
    void removeVariable(Perl6Variable variable);
}
