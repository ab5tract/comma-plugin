package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;
import edument.perl6idea.psi.symbols.Perl6LexicalSymbolContributor;
import edument.perl6idea.psi.symbols.Perl6MOPSymbolContributor;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import org.jetbrains.annotations.Nullable;

public interface Perl6PackageDecl extends Perl6PsiScope, Perl6PsiDeclaration,
                                          StubBasedPsiElement<Perl6PackageDeclStub>,
                                          Perl6IndexableType, PsiNamedElement, P6Extractable,
                                          Perl6LexicalSymbolContributor, Perl6MOPSymbolContributor {
    String getPackageKind();
    String getPackageName();
    @Nullable
    PsiElement getPackageKeywordNode();
    void contributeNestedPackagesWithPrefix(Perl6SymbolCollector collector, String prefix);
}
