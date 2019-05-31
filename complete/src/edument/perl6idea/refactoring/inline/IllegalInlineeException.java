package edument.perl6idea.refactoring.inline;

import com.intellij.psi.PsiElement;

public class IllegalInlineeException extends Exception {
    private PsiElement myElement;
    private String myMessage;

    public IllegalInlineeException(PsiElement element, String message) {
        myElement = element;
        myMessage = message;
    }

    @Override
    public String toString() {
        return myMessage;
    }

    public PsiElement getElement() {
        return myElement;
    }
}
