package edument.perl6idea.psi.type;

public record Perl6DefinednessType(Perl6Type baseType, boolean requiresDefined) implements Perl6Type {

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
