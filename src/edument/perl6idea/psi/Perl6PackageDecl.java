package edument.perl6idea.psi;

public interface Perl6PackageDecl extends Perl6PsiElement, Perl6PsiScope, Perl6PsiDeclaration {
    String getPackageKind();
    String getPackageName();
    Perl6RoutineDecl[] privateMethods();
}
