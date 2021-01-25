package edument.perl6idea.psi.type;

import edument.perl6idea.psi.Perl6PsiElement;

public class Perl6ResolvedType implements Perl6Type {
    private final String typename;
    private final Perl6PsiElement resolution;

    public Perl6ResolvedType(String typename, Perl6PsiElement resolution) {
        this.typename = typename;
        this.resolution = resolution;
    }

    @Override
    public String getName() {
        return typename;
    }

    public Perl6PsiElement getResolution() {
        return resolution;
    }

    public boolean equals(Object other) {
        if (other instanceof Perl6ResolvedType)
            return ((Perl6ResolvedType)other).resolution == resolution;
        if (other instanceof Perl6Type)
            return ((Perl6Type)other).getName().equals(getName());
        return false;
    }
}
