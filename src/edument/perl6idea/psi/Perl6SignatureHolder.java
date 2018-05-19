package edument.perl6idea.psi;

import com.intellij.lang.ASTNode;

import static edument.perl6idea.parsing.Perl6ElementTypes.RETURN_CONSTRAINT;
import static edument.perl6idea.parsing.Perl6ElementTypes.TYPE_NAME;

public interface Perl6SignatureHolder {
    String getSignature();
    Perl6Signature getSignatureNode();
    String getReturnsTrait();

    default String summarySignature() {
        Perl6Signature signature = getSignatureNode();
        String retTrait = null;
        String retConstraint = null;

        if (signature == null) {
            retTrait = getReturnsTrait();
            if (retTrait != null) return "(--> " + retTrait + ")";
            return "()";
        }

        ASTNode constr = signature.getNode().findChildByType(RETURN_CONSTRAINT);
        if (constr != null) {
            ASTNode type = constr.getPsi().getNode().findChildByType(TYPE_NAME);
            if (type != null)
                retConstraint = type.getText();
        } else
            retTrait = getReturnsTrait();

        return retTrait != null ?
                signature.summary(retTrait) :
                retConstraint != null ?
                        signature.summary(retConstraint) :
                        signature.summary("");
    }
}
