package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6TokenTypes.*;

public interface P6CodeBlockCall extends PsiNamedElement {
    @NotNull
    String getCallName();

    // For calls that have mandatory prefix parts, for example, method calls have a caller,
    // method returns a whole element to work with when we want to change the whole expression,
    // not only call's part.
    PsiElement getWholeCallNode();

    default PsiElement[] getCallArguments() {
        PsiElement node = getFirstChild();

        boolean hasSeenName = false;

        while (node != null) {
            if (node instanceof PsiWhiteSpace ||
                node.getNode().getElementType() == UNV_WHITE_SPACE ||
                node.getNode().getElementType() == INVOCANT_MARKER ||
                node.getNode().getElementType() == PARENTHESES_OPEN ||
                node.getNode().getElementType() == PARENTHESES_CLOSE ||
                node.getNode().getElementType() == METHOD_CALL_OPERATOR ||
                node.getNode().getElementType() == NULL_TERM) {
                node = node.getNextSibling();
                continue;
            } else if (node instanceof Perl6LongName ||
                       node instanceof Perl6SubCallName) {
                if (hasSeenName)
                    return new PsiElement[]{node};
                hasSeenName = true;
                node = node.getNextSibling();
                continue;
            } else if (node instanceof Perl6InfixApplication) {
                if (((Perl6InfixApplication)node).getOperator().equals(","))
                    return ((Perl6InfixApplication)node).getOperands();
            }
            return new PsiElement[]{node};
        }
        return PsiElement.EMPTY_ARRAY;
    }
}
