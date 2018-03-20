package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6ParameterVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6ParameterVariableImpl extends ASTWrapperPsiElement implements Perl6ParameterVariable {
    public Perl6ParameterVariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(Perl6TokenTypes.VARIABLE);
    }

    @NotNull
    @Override
    public String getName() {
        PsiElement nameIdent = getNameIdentifier();
        return nameIdent != null ? nameIdent.getText() : "";
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        // TODO See https://github.com/JetBrains/intellij-community/blob/db9200fcdb58eccfeb065524bd211b3aa6d6b83c/java/java-psi-impl/src/com/intellij/psi/impl/PsiImplUtil.java
        return null;
    }
}
