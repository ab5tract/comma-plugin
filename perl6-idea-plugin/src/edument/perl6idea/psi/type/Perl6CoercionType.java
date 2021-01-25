package edument.perl6idea.psi.type;

public class Perl6CoercionType implements Perl6Type {
    private final Perl6Type targetType;
    private final Perl6Type sourceType;

    public Perl6CoercionType(Perl6Type targetType, Perl6Type sourceType) {
        this.targetType = targetType;
        this.sourceType = sourceType;
    }

    public Perl6Type getTargetType() {
        return targetType;
    }

    public Perl6Type getSourceType() {
        return sourceType;
    }

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
