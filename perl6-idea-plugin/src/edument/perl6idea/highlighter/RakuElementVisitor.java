package edument.perl6idea.highlighter;

import com.intellij.psi.PsiElementVisitor;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6RegexDecl;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6VariableDecl;

public class RakuElementVisitor extends PsiElementVisitor {
    public void visitPackage(Perl6PackageDecl packageDecl) {
        visitElement(packageDecl);
    }

    public void visitRoutine(Perl6RoutineDecl routineDecl) {
        visitElement(routineDecl);
    }

    public void visitRegex(Perl6RegexDecl regexDecl) {
        visitElement(regexDecl);
    }

    public void visitVariableDecl(Perl6VariableDecl variableDecl) {
        visitElement(variableDecl);
    }
}
