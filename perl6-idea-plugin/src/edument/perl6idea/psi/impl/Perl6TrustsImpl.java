package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Trusts;
import edument.perl6idea.psi.Perl6TypeName;

public class Perl6TrustsImpl extends ASTWrapperPsiElement implements Perl6Trusts {
    public Perl6TrustsImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String getTypeName() {
        Perl6TypeName name = PsiTreeUtil.findChildOfType(this, Perl6TypeName.class);
        return name == null ? "" : name.getTypeName();
    }
}
