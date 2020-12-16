package edument.perl6idea.psi.type;

public class Perl6Untyped implements Perl6Type {
    public static final Perl6Untyped INSTANCE = new Perl6Untyped();

    private Perl6Untyped() {}

    @Override
    public String getName() {
        return "Any";
    }

    public boolean equals(Object other) {
        return other instanceof Perl6Untyped;
    }
}
