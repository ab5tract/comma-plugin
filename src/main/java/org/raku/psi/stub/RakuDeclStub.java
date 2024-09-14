package org.raku.psi.stub;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import org.raku.psi.RakuPsiDeclaration;

public interface RakuDeclStub<T extends PsiElement & RakuPsiDeclaration> extends StubElement<T> {
    String getScope();
    boolean isExported();
}
