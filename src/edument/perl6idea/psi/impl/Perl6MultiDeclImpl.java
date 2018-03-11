package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6LongName;
import edument.perl6idea.psi.Perl6MultiDecl;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6RoutineDecl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Perl6MultiDeclImpl extends ASTWrapperPsiElement implements Perl6MultiDecl {
    public Perl6MultiDeclImpl(@NotNull ASTNode node) {
        super(node);
    }
}
