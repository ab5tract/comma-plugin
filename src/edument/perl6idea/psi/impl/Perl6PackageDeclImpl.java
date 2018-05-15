package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static edument.perl6idea.parsing.Perl6ElementTypes.*;

public class Perl6PackageDeclImpl extends StubBasedPsiElementBase<Perl6PackageDeclStub> implements Perl6PackageDecl {
    public Perl6PackageDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6PackageDeclImpl(final Perl6PackageDeclStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getPackageKind() {
        Perl6PackageDeclStub stub = getStub();
        if (stub != null)
            return stub.getPackageKind();

        PsiElement declarator = findChildByType(Perl6TokenTypes.PACKAGE_DECLARATOR);
        return declarator == null ? "package" : declarator.getText();
    }

    @Override
    public String getPackageName() {
        Perl6PackageDeclStub stub = getStub();
        if (stub != null)
            return stub.getPackageName();

        return getTypeLikeName();
    }

    @Override
    public Perl6RoutineDecl[] privateMethods() {
        List<Perl6PsiElement> result = new ArrayList<>();
        Perl6Blockoid blockoid = findChildByType(BLOCKOID);
        if (blockoid == null) return new Perl6RoutineDecl[0];
        ASTNode list = blockoid.getNode().findChildByType(STATEMENT_LIST);
        if (list == null) return new Perl6RoutineDecl[0];
        ASTNode[] statements = list.getChildren(TokenSet.create(STATEMENT));
        for (ASTNode node : statements) {
            PsiElement child = node.getFirstChildNode().getPsi();
            if (child instanceof Perl6RoutineDeclImpl) {
                Perl6RoutineDecl routine = (Perl6RoutineDecl)child;
                if (routine.isPrivateMethod())
                    result.add(routine);
            }
        }
        return result.toArray(new Perl6RoutineDecl[result.size()]);
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:PACKAGE_DECLARATION)";
    }

    // TODO REFACTOR
    // Everything below copied when making this a stub-based node

    public String getTypeLikeName() {
        PsiElement name = findChildByType(Perl6TokenTypes.NAME);
        PsiElement longName = findChildByType(Perl6ElementTypes.LONG_NAME);
        return name == null ? longName == null ? "<anon>" : longName.getText() : name.getText();
    }

    @Override
    public String getName() { return getTypeLikeName(); }

    @Nullable
    private String getOuterElementName() {
        StringBuilder name = new StringBuilder();
        PsiElement outer = getParent();
        while (outer != null && !(outer instanceof Perl6File)) {
            if (outer instanceof Perl6PackageDecl)
                name.insert(0, ((Perl6PackageDecl) outer).getPackageName());
            outer = outer.getParent();
        }
        return name.toString().equals("") ? null : name.toString();
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getTypeLikeName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                PsiFile baseFile = getContainingFile();
                PsiDirectory dir = baseFile.getParent();
                String path = getContainingFile().getName().replace(".pm6", "");
                if (getOuterElementName() != null) path = String.format("%s::%s", path, getOuterElementName());
                while (dir != null && !dir.getName().equals("lib")) {
                    path = String.format("%s::%s", dir.getName(), path);
                    dir = dir.getParent();
                }
                return String.format("(in %s)", path);
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return Perl6Icons.CAMELIA;
            }
        };
    }
}
