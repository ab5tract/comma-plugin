package edument.perl6idea.psi;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.meta.PsiMetaOwner;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Perl6File extends Perl6PsiElement, Perl6PsiScope, PsiNameIdentifierOwner, PsiFile, PsiMetaOwner {
    void contributeGlobals(Perl6SymbolCollector collector, Set<String> seen);
    Map<Integer, List<Integer>> getStatementLineMap();
    boolean isReal();
}
