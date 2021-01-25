package edument.perl6idea.psi.type;

import edument.perl6idea.psi.Perl6PackageDecl;

public class Perl6SelfType extends Perl6ResolvedType {
    public Perl6SelfType(Perl6PackageDecl packageDecl) {
        super(packageDecl.getPackageName(), packageDecl);
    }
}
