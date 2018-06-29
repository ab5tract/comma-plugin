package edument.perl6idea.psi.symbols;

import com.intellij.psi.PsiElement;

/* Represents a symbol found by the symbol resolution process. It may be resolved
 * to a PSI element, but may also have none and so be external. */
public interface Perl6Symbol {
    Perl6SymbolKind getKind();
    String getName();
    PsiElement getPsi();
    boolean isExternal();               // From a module outside this project
    boolean isSetting();                // From CORE.setting
    boolean isImplicitlyDeclared();     // Like the default $_, $/, and $!; also $?FILE etc.
    default boolean isInstanceScoped() {
        return false;
    }
}
