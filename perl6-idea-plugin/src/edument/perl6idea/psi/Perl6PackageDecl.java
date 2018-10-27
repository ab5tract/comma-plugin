package edument.perl6idea.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;

public interface Perl6PackageDecl extends Perl6PsiScope, Perl6PsiDeclaration,
                                          StubBasedPsiElement<Perl6PackageDeclStub>,
                                          Perl6IndexableType, PsiNamedElement, P6Extractable {
    String getPackageKind();
    String getPackageName();
    void contributeNestedPackagesWithPrefix(Perl6SymbolCollector collector, String prefix);
}
