package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static edument.perl6idea.parsing.Perl6ElementTypes.*;

public class Perl6PackageDeclImpl extends ASTWrapperPsiElement implements Perl6PackageDecl {
    public Perl6PackageDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getPackageKind() {
        PsiElement declarator = findChildByType(Perl6TokenTypes.PACKAGE_DECLARATOR);
        return declarator == null ? "package" : declarator.getText();
    }

    @Override
    public String getPackageName() {
        PsiElement name = findChildByType(Perl6TokenTypes.NAME);
        return name == null ? "<anon>" : name.getText();
    }

    @Override
    public Perl6PsiElement[] privateMethods() {
        List<Perl6PsiElement> result = new ArrayList<>();
        Perl6Blockoid blockoid = findChildByType(BLOCKOID);
        if (blockoid == null) return new Perl6PsiElement[]{};
        ASTNode list = blockoid.getNode().findChildByType(STATEMENT_LIST);
        if (list == null) return new Perl6PsiElement[]{};
        ASTNode[] statements = list.getChildren(TokenSet.create(STATEMENT));
        for (ASTNode node : statements) {
            PsiElement child = node.getFirstChildNode().getPsi();;
            if (child instanceof Perl6RoutineDeclImpl) {
                Perl6RoutineDecl routine = (Perl6RoutineDecl)child;
                if (routine.isPrivateMethod())
                    result.add(routine);
            }
        }
        return result.toArray(new Perl6PsiElement[result.size()]);
    }
}
