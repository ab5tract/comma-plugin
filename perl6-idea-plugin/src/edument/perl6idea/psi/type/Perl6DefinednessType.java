package edument.perl6idea.psi.type;

public class Perl6DefinednessType implements Perl6Type {
    private final Perl6Type baseType;
    private final boolean requiresDefined;

    public Perl6DefinednessType(Perl6Type baseType, boolean requiresDefined) {
        this.baseType = baseType;
        this.requiresDefined = requiresDefined;
    }

    public Perl6Type getBaseType() {
        return baseType;
    }

    public boolean requiresDefined() {
        return requiresDefined;
    }

    @Override
    public String getName() {
        return baseType.getName() + (requiresDefined ? ":D" : ":U");
    }

    @Override
    public Perl6Type nominalType() {
        return baseType.nominalType();
    }

    public boolean equals(Object other) {
        return other instanceof Perl6DefinednessType &&
                baseType.equals(((Perl6DefinednessType)other).baseType) &&
                requiresDefined == ((Perl6DefinednessType)other).requiresDefined;
    }
}
