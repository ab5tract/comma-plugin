package edument.perl6idea.psi;

import com.intellij.psi.util.PsiTreeUtil;

public interface Perl6BlockOrHash extends Perl6PsiElement, Perl6PsiScope, P6Extractable, P6Holder, P6Control {
    @Override
    default Perl6Statement[] getElements() {
        Perl6StatementList list = PsiTreeUtil.findChildOfType(this, Perl6StatementList.class);
        Perl6Statement[] statements = PsiTreeUtil.getChildrenOfType(list, Perl6Statement.class);
        return statements == null ? new Perl6Statement[0] : statements;
    }
}
