package org.raku.comma.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUntyped;

public interface RakuTopicalizer extends RakuPsiElement {
    /** This construct may set $_, but does it really? */
    default boolean isTopicalizing() { return true; }

    /** Get the type of the topic that is set. */
    default RakuType inferTopicType() { return RakuUntyped.INSTANCE; }

    /** Tries to work out the topic type. It defaults to whatever inferTopicType
     * is implemented to return, however if there is a when block matching on a
     * type between the current location and the found topicalizer, that type
     * is taken instead. */
    default RakuType calculateTopicType(RakuPsiElement lookup) {
        RakuWhenStatement when = PsiTreeUtil.getParentOfType(lookup, RakuWhenStatement.class);
        if (when != null && PsiTreeUtil.isAncestor(this, when, false)) {
            PsiElement whatIsTheWhen = when.getTopic();
            if (whatIsTheWhen instanceof RakuTypeName)
                return ((RakuTypeName)whatIsTheWhen).inferType();
        }
        return inferTopicType();
    }
}
