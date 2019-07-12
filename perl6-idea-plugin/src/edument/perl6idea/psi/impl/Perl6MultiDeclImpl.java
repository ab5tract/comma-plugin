package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6MultiDecl;
import org.jetbrains.annotations.NotNull;

public class Perl6MultiDeclImpl extends ASTWrapperPsiElement implements Perl6MultiDecl {
    public Perl6MultiDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getMultiness() {
        PsiElement multiness = findChildByType(Perl6TokenTypes.MULTI_DECLARATOR);
        return multiness == null ? "only" : multiness.getText();
    }
}
