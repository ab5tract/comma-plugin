package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

public interface P6Topicalizer extends Perl6PsiElement {
    /** This construct may set $_, but does it really? */
    default boolean isTopicalizing() { return true; }

    /** Get the type of the topic that is set. */
    default String inferTopicType() { return "Any"; }

    /** Tries to work out the topic type. It defaults to whatever inferTopicType
     * is implemented to return, however if there is a when block matching on a
     * type between the current location and the found topicalizer, that type
     * is taken instead. */
    default String calculateTopicType(Perl6PsiElement lookup) {
        Perl6WhenStatement when = PsiTreeUtil.getParentOfType(lookup, Perl6WhenStatement.class);
        if (when != null && PsiTreeUtil.isAncestor(this, when, false)) {
            PsiElement whatIsTheWhen = when.getTopic();
            if (whatIsTheWhen instanceof Perl6TypeName)
                return ((Perl6TypeName)whatIsTheWhen).getTypeName();
        }
        return inferTopicType();
    }
}
