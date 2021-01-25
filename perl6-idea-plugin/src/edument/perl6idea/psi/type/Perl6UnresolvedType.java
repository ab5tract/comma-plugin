package edument.perl6idea.psi.type;

public class Perl6UnresolvedType implements Perl6Type {
    private final String typename;

    public Perl6UnresolvedType(String typename) {
        this.typename = typename;
    }

    @Override
    public String getName() {
        return typename;
    }

    public boolean equals(Object other) {
        if (other instanceof Perl6Type)
            return ((Perl6Type)other).getName().equals(getName());
        return false;
    }
}
