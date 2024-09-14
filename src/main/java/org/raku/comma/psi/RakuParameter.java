package org.raku.comma.psi;

import com.intellij.psi.PsiElement;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;
import org.jetbrains.annotations.Nullable;

public interface RakuParameter extends RakuPsiElement, RakuPsiDeclaration, RakuLexicalSymbolContributor, RakuVariableSource {
    String summary(boolean includeName);
    String getVariableName();
    @Nullable
    PsiElement getInitializer();
    boolean isPositional();
    boolean isNamed();
    @Nullable
    RakuWhereConstraint getWhereConstraint();
    @Nullable
    RakuPsiElement getValueConstraint();
    boolean isSlurpy();
    boolean isRequired();
    boolean isOptional();
    boolean isExplicitlyOptional();
    boolean isCopy();
    boolean isRW();
    boolean equalsParameter(RakuParameter other);
}
