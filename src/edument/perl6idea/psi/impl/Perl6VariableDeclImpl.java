package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6VariableDeclImpl extends ASTWrapperPsiElement implements Perl6VariableDecl {
    public Perl6VariableDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Perl6Variable varNode = findChildByClass(Perl6Variable.class);
        return varNode != null ? varNode.getVariableToken() : null;
    }

    @Override
    public String getName() {
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : null;
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        // TODO See https://github.com/JetBrains/intellij-community/blob/db9200fcdb58eccfeb065524bd211b3aa6d6b83c/java/java-psi-impl/src/com/intellij/psi/impl/PsiImplUtil.java
        return null;
    }
}
