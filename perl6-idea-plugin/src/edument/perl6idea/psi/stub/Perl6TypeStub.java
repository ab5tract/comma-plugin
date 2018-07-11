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
        Stub current = getParentStub();
        while (current != null) {
            if (current instanceof Perl6ScopedDeclStub)
                if (((Perl6ScopedDeclStub)current).getScope().equals("my"))
                    return null;
            if (current instanceof Perl6PackageDeclStub)
                globalName = ((Perl6PackageDeclStub)current).getTypeName() + "::" + globalName;
            current = current.getParentStub();
        }
        return globalName;
    }

    default String getLexicalName() {
        String lexicalName = getTypeName();
        if (lexicalName == null)
            return null;
        Stub current = getParentStub();
        while (current != null) {
            if (current instanceof Perl6ScopedDeclStub) {
                if (((Perl6ScopedDeclStub)current).getScope().equals("my"))
                    return lexicalName;
                return null;
            }
            if (current instanceof Perl6PackageDeclStub)
                lexicalName = ((Perl6PackageDeclStub)current).getTypeName() + "::" + lexicalName;
            current = current.getParentStub();
        }
        return null;
    }
}
