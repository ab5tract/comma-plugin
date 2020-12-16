package edument.perl6idea.psi.type;

/* Done by all kinds of type that we may infer. */
public interface Perl6Type {
    /* Get a displayable name for the type. */
    String getName();

    /* Get the nominal type for this type. */
    default Perl6Type nominalType() {
        return this;
    }
}
