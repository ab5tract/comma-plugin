package edument.perl6idea.psi.type;

public record Perl6CoercionType(Perl6Type targetType, Perl6Type sourceType) implements Perl6Type {

    @Override
    public String getName() {
        return targetType.getName() + "(" + sourceType.getName() + ")";
    }

    @Override
    public Perl6Type nominalType() {
        return targetType.nominalType();
    }

    public boolean equals(Object other) {
        return other instanceof Perl6CoercionType &&
               targetType.equals(((Perl6CoercionType)other).targetType) &&
               sourceType.equals(((Perl6CoercionType)other).sourceType);
    }
}
