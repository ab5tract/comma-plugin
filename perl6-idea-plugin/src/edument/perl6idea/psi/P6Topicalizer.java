package edument.perl6idea.psi;

public interface P6Topicalizer extends Perl6PsiElement {
    /** This construct may set $_, but does it really? */
    default boolean isTopicalizing() { return true; }

    /** Get the type of the topic. */
    default String inferTopicType() { return "Any"; }
}
