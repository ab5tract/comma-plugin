package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6QuoteRegex;
import edument.perl6idea.psi.Perl6RegexAtom;
import edument.perl6idea.psi.Perl6RegexCapturePositional;
import edument.perl6idea.psi.Perl6RegexVariable;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class Perl6QuoteRegexImpl extends ASTWrapperPsiElement implements Perl6QuoteRegex {
    public Perl6QuoteRegexImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferType() {
        return "Regex";
    }

    @Override
    public void contributeRegexVariables(Perl6SymbolCollector collector) {
        Collection<Perl6RegexAtom> atoms = PsiTreeUtil.findChildrenOfType(this, Perl6RegexAtom.class);
        for (Perl6RegexAtom atom : atoms) {
            PsiElement firstChild = atom.getFirstChild();
            if (firstChild instanceof Perl6RegexCapturePositional) {
                collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, (Perl6RegexCapturePositional)firstChild));
            } else if (firstChild instanceof Perl6RegexVariable) {
                collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, (Perl6RegexVariable)firstChild));
            }
        }
    }
}
