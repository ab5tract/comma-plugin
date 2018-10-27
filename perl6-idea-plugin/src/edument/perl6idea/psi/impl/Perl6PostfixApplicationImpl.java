package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6PostfixApplication;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6TypeName;
import org.jetbrains.annotations.NotNull;

public class Perl6PostfixApplicationImpl extends ASTWrapperPsiElement implements Perl6PostfixApplication {
    public Perl6PostfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferType() {
        PsiElement first = getFirstChild();
        PsiElement last = getLastChild();

        if (first instanceof Perl6TypeName && last instanceof Perl6MethodCall) {
            Perl6MethodCall call = (Perl6MethodCall)last;
            Perl6TypeName typeName = (Perl6TypeName)first;
            return call.getCallName().equals(".new") ? typeName.getTypeName() : tryToCalculateMethodReturnType(call);
        } else if (last instanceof Perl6MethodCall) {
            return tryToCalculateMethodReturnType((Perl6MethodCall)last);
        }
        return "Mu";
    }

    private static String tryToCalculateMethodReturnType(Perl6MethodCall last) {
        PsiReference ref = last.getReference();
        if (ref == null) return "Mu";
        PsiElement resolved = ref.resolve();
        if (resolved == null) return "Mu";
        Perl6RoutineDecl decl = (Perl6RoutineDecl)resolved;
        return decl.getReturnType();
    }
}
