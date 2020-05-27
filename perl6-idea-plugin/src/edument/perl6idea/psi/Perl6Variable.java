package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.Nullable;

public interface Perl6Variable extends Perl6PsiElement, PsiNameIdentifierOwner, P6Extractable {

    PsiElement getVariableToken();

    @Nullable
    String getVariableName();

    /* This method is used to factor out code of deciding
     * what most generic type variable can have based purely
     * on its sigil and its declaration element (if any).
     * E.g. `@foo, null` -> `Array`, `%hash, Perl6ParameterVariable` -> `Map`
     */
    @Nullable
    String getTypeBySigil(String text, @Nullable  PsiElement declaration);

    static char getSigil(String text) {
        if (text.length() < 1)
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
}
