package org.raku.comma.psi.stub;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.Stub;
import org.raku.comma.psi.RakuPsiDeclaration;

public interface RakuTypeStub<T extends PsiElement & RakuPsiDeclaration> extends RakuDeclStub<T> {
    String getTypeName();

    default String getGlobalName() {
        String globalName = getTypeName();
        if (globalName == null)
            return null;
        StringBuilder globalNameBuilder = new StringBuilder(globalName);
        Stub current = getParentStub();
        while (current != null) {
            if (current instanceof RakuScopedDeclStub)
                if (((RakuScopedDeclStub)current).getScope().equals("my"))
                    return null;
            if (current instanceof RakuPackageDeclStub)
                globalNameBuilder.insert(0, ((RakuPackageDeclStub)current).getTypeName() + "::");
            current = current.getParentStub();
        }
        return globalNameBuilder.toString();
    }

    default String getLexicalName() {
        String lexicalName = getTypeName();
        if (lexicalName == null)
            return null;
        StringBuilder lexicalNameBuilder = new StringBuilder(lexicalName);
        Stub current = getParentStub();
        while (current != null) {
            if (current instanceof RakuScopedDeclStub) {
                if (((RakuScopedDeclStub)current).getScope().equals("my"))
                    return lexicalNameBuilder.toString();
                return null;
            }
            if (current instanceof RakuPackageDeclStub)
                lexicalNameBuilder.insert(0, ((RakuPackageDeclStub)current).getTypeName() + "::");
            current = current.getParentStub();
        }
        return null;
    }
}
