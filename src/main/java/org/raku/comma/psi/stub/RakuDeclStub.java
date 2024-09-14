package org.raku.comma.psi.stub;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import org.raku.comma.psi.RakuPsiDeclaration;

public interface RakuDeclStub<T extends PsiElement & RakuPsiDeclaration> extends StubElement<T> {
    String getScope();
    boolean isExported();
}
