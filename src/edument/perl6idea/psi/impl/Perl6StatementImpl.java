package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Perl6StatementImpl extends ASTWrapperPsiElement implements Perl6Statement {
    public Perl6StatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
