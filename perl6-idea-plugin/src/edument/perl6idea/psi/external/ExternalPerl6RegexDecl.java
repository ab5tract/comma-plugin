package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.psi.Perl6RegexDecl;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.stub.Perl6RegexDeclStub;
import edument.perl6idea.psi.symbols.MOPSymbolsAllowed;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import org.jetbrains.annotations.Nullable;

public class ExternalPerl6RegexDecl extends Perl6ExternalPsiElement implements Perl6RegexDecl {
    private final Perl6Symbol mySymbol;

    public ExternalPerl6RegexDecl(Project project, ExternalPerl6File file, Perl6Symbol symbol) {
        myProject = project;
        myParent = file;
        mySymbol = symbol;
    }

    @Override
    public String getRegexKind() {
        return null;
    }

    @Override
    public String getRegexName() {
        return null;
    }

    @Override
    public IStubElementType getElementType() {
        return null;
    }

    @Override
    public Perl6RegexDeclStub getStub() {
        return null;
    }

    @Override
    public String getScope() {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {

    }

    @Override
    public String getSignature() {
        return null;
    }

    @Nullable
    @Override
    public Perl6Signature getSignatureNode() {
        return null;
    }

    @Override
    public String getReturnsTrait() {
        return null;
    }

    @Override
    public void contributeMOPSymbols(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {

    }
}
