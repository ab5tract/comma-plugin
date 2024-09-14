package org.raku.comma.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.meta.PsiMetaOwner;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;

public interface RakuParameterVariable extends RakuPsiDeclaration, PsiNamedElement, PsiMetaOwner,
                                               RakuLexicalSymbolContributor {
    String summary(boolean includeName);
}
