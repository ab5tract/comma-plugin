package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

public interface P6Holder {
    default Perl6Statement[] getElements() {
        if (!(this instanceof PsiElement))
            return new Perl6Statement[0];

        Perl6SemiList semiList = PsiTreeUtil.getChildOfType((PsiElement)this, Perl6SemiList.class);
        if (semiList == null)
            return new Perl6Statement[0];
        Perl6Statement[] statements = PsiTreeUtil.getChildrenOfType(semiList, Perl6Statement.class);
        return statements == null ? new Perl6Statement[0] : statements;
    }
}
