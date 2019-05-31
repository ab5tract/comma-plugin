package edument.perl6idea.psi;

import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

public interface Perl6SignatureHolder {
    String getSignature();
    @Nullable
    Perl6Signature getSignatureNode();
    String getReturnsTrait();

    default String summarySignature() {
        Perl6Signature signature = getSignatureNode();
        String returnType = getReturnType();
        if (signature != null)
            return signature.summary(returnType);

        return returnType != null ? "(--> " + returnType + ")" : "()";
    }

    @Nullable
    default String getReturnType() {
        String retTrait = getReturnsTrait();
        if (retTrait != null) return retTrait;

        Perl6Signature signature = getSignatureNode();
        if (signature == null) return null;
        Perl6ReturnConstraint constraint = PsiTreeUtil.getChildOfType(signature, Perl6ReturnConstraint.class);
        if (constraint == null) return null;
        return constraint.getReturnType();
    }
}
