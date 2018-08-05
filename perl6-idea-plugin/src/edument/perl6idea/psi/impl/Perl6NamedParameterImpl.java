package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6NamedParameter;
import edument.perl6idea.psi.Perl6ParameterVariable;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6TokenTypes.NAMED_PARAMETER_NAME_ALIAS;

public class Perl6NamedParameterImpl extends ASTWrapperPsiElement implements Perl6NamedParameter {
    public Perl6NamedParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String summary() {
        PsiElement alias = findChildByType(NAMED_PARAMETER_NAME_ALIAS);
        Perl6ParameterVariable var = PsiTreeUtil.findChildOfType(this, Perl6ParameterVariableImpl.class);
        String base = ":";
        if (alias != null) {
            Perl6NamedParameter internal = PsiTreeUtil.findChildOfType(this, Perl6NamedParameterImpl.class);
            if (internal == null)
                return base + "$" + alias.getText();
            else
                return base + alias.getText() + "(" + internal.summary() + ")";
        }
        else if (var != null)
            return base + var.getText();
        else
            return base; // should not happen
    }
}
