package org.raku.comma.psi.symbols;

import com.intellij.psi.PsiElement;

/* Represents a symbol found by the symbol resolution process. It may be resolved
 * to a PSI element, but may also have none and so be external. */
public interface RakuSymbol {
    RakuSymbolKind getKind();
    String getName();
    PsiElement getPsi();
    boolean isExternal();               // From a module outside this project
    boolean isSetting();                // From CORE.setting
    boolean isImplicitlyDeclared();     // Like the default $_, $/, and $!; also $?FILE etc.
    void setPriority(double priority);
    double getPriority();
    boolean wasDeferred();
}
