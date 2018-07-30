package edument.perl6idea.psi.stub;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.Stub;
import edument.perl6idea.psi.Perl6PsiDeclaration;

public interface Perl6TypeStub<T extends PsiElement & Perl6PsiDeclaration> extends Perl6DeclStub<T> {
    String getTypeName();

    default String getGlobalName() {
        String globalName = getTypeName();
        if (globalName == null)
            return null;
        StringBuilder globalNameBuilder = new StringBuilder(globalName);
        Stub current = getParentStub();
        while (current != null) {
            if (current instanceof Perl6ScopedDeclStub)
                if (((Perl6ScopedDeclStub)current).getScope().equals("my"))
                    return null;
            if (current instanceof Perl6PackageDeclStub)
                globalNameBuilder.insert(0, ((Perl6PackageDeclStub)current).getTypeName() + "::");
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
            if (current instanceof Perl6ScopedDeclStub) {
                if (((Perl6ScopedDeclStub)current).getScope().equals("my"))
                    return lexicalNameBuilder.toString();
                return null;
            }
            if (current instanceof Perl6PackageDeclStub)
                lexicalNameBuilder.insert(0, ((Perl6PackageDeclStub)current).getTypeName() + "::");
            current = current.getParentStub();
        }
        return null;
    }
}
