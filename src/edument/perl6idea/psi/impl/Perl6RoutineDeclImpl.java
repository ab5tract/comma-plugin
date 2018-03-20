package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6RoutineDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6ElementTypes.*;

public class Perl6RoutineDeclImpl extends ASTWrapperPsiElement implements Perl6RoutineDecl {
    public Perl6RoutineDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getRoutineKind() {
        PsiElement declarator = findChildByType(Perl6TokenTypes.ROUTINE_DECLARATOR);
        return declarator == null ? "sub" : declarator.getText();
    }

    @Override
    public String getRoutineName() {
        PsiElement name = findChildByType(Perl6ElementTypes.LONG_NAME);
        return name == null ? "<anon>" : name.getText();
    }

    @Override
    public String getSignature() {
        return getRoutineName() + summarySignature();
    }

    @Override
    public boolean isPrivateMethod() {
        return getRoutineName().startsWith("!");
    }

    private String summarySignature() {
        Perl6SignatureImpl signature = findChildByClass(Perl6SignatureImpl.class);
        String retTrait = null;
        String retConstraint = null;

        if (signature == null) {
            retTrait = getReturnsTrait(this.getNode());
            if (retTrait != null) return "(--> " + retTrait + ")";
            return "()";
        }

        ASTNode constr = signature.getNode().findChildByType(RETURN_CONSTRAINT);
        if (constr != null) {
            ASTNode type = constr.getPsi().getNode().findChildByType(TYPE_NAME);
            if (type != null)
                retConstraint = type.getText();
        } else
            retTrait = getReturnsTrait(this.getNode());

        return retTrait != null ?
                signature.summary(retTrait) :
                retConstraint != null ?
                        signature.summary(retConstraint) :
                        signature.summary("");
    }

    @Nullable
    private String getReturnsTrait(ASTNode child) {
        ASTNode trait = child.findChildByType(TRAIT);
        if (trait != null && trait.getFirstChildNode().getText().equals("returns")) {
            ASTNode type = trait.findChildByType(TYPE_NAME);
            if (type != null) return type.getText();
        }
        return null;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(Perl6ElementTypes.LONG_NAME);
    }

    @Override
    public String getName() {
        return getRoutineName();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }
}
