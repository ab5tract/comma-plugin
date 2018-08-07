package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6PostfixApplication;
import edument.perl6idea.psi.Perl6TypeName;
import org.jetbrains.annotations.NotNull;

public class Perl6PostfixApplicationImpl extends ASTWrapperPsiElement implements Perl6PostfixApplication {
    public Perl6PostfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferType() {
        PsiElement first = getFirstChild();
        if (!(first instanceof Perl6TypeName)) return "Any";
        PsiElement last = getLastChild();
        if (!(last instanceof Perl6MethodCall)) return "Any";
        return ((Perl6MethodCall)last).getCallName().equals(".new") ? ((Perl6TypeName)first).getTypeName() : "Any";
    }
}
