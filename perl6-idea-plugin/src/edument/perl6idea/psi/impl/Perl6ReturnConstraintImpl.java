package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6ReturnConstraint;
import edument.perl6idea.psi.Perl6TypeName;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6Untyped;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6ElementTypes.TYPE_NAME;

public class Perl6ReturnConstraintImpl extends ASTWrapperPsiElement implements Perl6ReturnConstraint {
    public Perl6ReturnConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public Perl6Type getReturnType() {
        PsiElement typeName = findChildByType(TYPE_NAME);
        return typeName instanceof Perl6TypeName
               ? ((Perl6TypeName)typeName).inferType()
               : Perl6Untyped.INSTANCE;
    }
}
