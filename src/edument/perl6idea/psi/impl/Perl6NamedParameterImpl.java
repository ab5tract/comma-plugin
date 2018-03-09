package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6NamedParameter;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.NAMED_PARAMETER_NAME_ALIAS;

public class Perl6NamedParameterImpl extends ASTWrapperPsiElement implements Perl6NamedParameter {
    public Perl6NamedParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary() {
        PsiElement alias = findChildByType(NAMED_PARAMETER_NAME_ALIAS);
        if (alias != null)
            return ":$" + alias.getText();
        return ":$";
    }
}
