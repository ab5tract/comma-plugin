package edument.perl6idea.psi;

import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6UnresolvedType;
import edument.perl6idea.psi.type.Perl6Untyped;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Perl6SignatureHolder extends Perl6MultiHolder {
    String getSignature();
    @Nullable
    Perl6Signature getSignatureNode();
    @Nullable
    String getReturnsTrait();

    default String summarySignature() {
        Perl6Signature signature = getSignatureNode();
        Perl6Type returnType = getReturnType();
        if (signature != null)
            return signature.summary(returnType);

        return returnType instanceof Perl6Untyped ? "()" : "(--> " + returnType.getName() + ")";
    }

    default @NotNull Perl6Type getReturnType() {
        String retTrait = getReturnsTrait();
        if (retTrait != null)
            return new Perl6UnresolvedType(retTrait);

        Perl6Signature signature = getSignatureNode();
        if (signature == null)
            return Perl6Untyped.INSTANCE;
        Perl6ReturnConstraint constraint = PsiTreeUtil.getChildOfType(signature, Perl6ReturnConstraint.class);
        if (constraint == null)
            return Perl6Untyped.INSTANCE;
        return constraint.getReturnType();
    }
}
