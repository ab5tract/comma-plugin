package org.raku.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.meta.PsiMetaOwner;
import org.raku.psi.symbols.RakuLexicalSymbolContributor;

public interface RakuParameterVariable extends RakuPsiDeclaration, PsiNamedElement, PsiMetaOwner,
                                               RakuLexicalSymbolContributor {
    String summary(boolean includeName);
}
