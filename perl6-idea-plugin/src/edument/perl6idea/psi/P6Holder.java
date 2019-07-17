package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

public interface P6Holder {
    default PsiElement[] getElements() {
        if (!(this instanceof PsiElement))
            return PsiElement.EMPTY_ARRAY;

        Perl6SemiList semiList = PsiTreeUtil.getChildOfType((PsiElement)this, Perl6SemiList.class);
        if (semiList == null)
            return PsiElement.EMPTY_ARRAY;
        Perl6Statement[] statements = PsiTreeUtil.getChildrenOfType(semiList, Perl6Statement.class);
        return statements == null ? PsiElement.EMPTY_ARRAY : statements;
    }
}
