package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.PodConfiguration;
import org.jetbrains.annotations.NotNull;

public class PodConfigurationImpl extends ASTWrapperPsiElement implements PodConfiguration {
    public PodConfigurationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
