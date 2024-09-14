package org.raku.comma.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.raku.comma.psi.symbols.RakuLexicalSymbolContributor;
import org.raku.comma.psi.type.RakuType;
import org.jetbrains.annotations.Nullable;

public interface RakuVariable extends RakuPsiElement, PsiNameIdentifierOwner, RakuExtractable, RakuLexicalSymbolContributor {

    PsiElement getVariableToken();

    @Nullable
    String getVariableName();

    default char getSigil() {
        return getSigil(getVariableName());
    }

    /* This method is used to factor out code of deciding
     * what most generic type variable can have based purely
     * on its sigil and its declaration element (if any).
     * E.g. `@foo, null` -> `Array`, `%hash, RakuParameterVariable` -> `Map`
     */
    @Nullable RakuType getTypeBySigil(String text, @Nullable  PsiElement declaration);

    static char getSigil(@Nullable String text) {
        if (text == null || text.length() < 1)
            return ' ';
        switch (text.charAt(0)) {
            case '$': return '$';
            case '@': return '@';
            case '%': return '%';
            case '&': return '&';
            default:  return ' ';
        }
    }

    static char getTwigil(@Nullable String text) {
        if (text == null || text.length() < 2) return ' ';
        switch (text.charAt(1)) {
            case '*': return '*';
            case '?': return '?';
            case '!': return '!';
            case '^': return '^';
            case ':': return ':';
            case '=': return '=';
            case '~': return '~';
            case '.': return '.';
            default:  return ' ';
        }
    }

    boolean isCaptureVariable();
}
