package edument.perl6idea.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.util.PlatformIcons;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Perl6StructureViewElement implements StructureViewTreeElement {
    private final Perl6PsiElement element;

    public Perl6StructureViewElement(Perl6PsiElement element) {
        this.element = element;
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        List<StructureViewTreeElement> structureElements = new ArrayList<>();
        if (element instanceof Perl6PsiScope)
            for (Perl6PsiElement decl : ((Perl6PsiScope)element).getDeclarations())
                if (applicable(decl))
                    structureElements.add(new Perl6StructureViewElement(decl));
        return structureElements.toArray(StructureViewTreeElement.EMPTY_ARRAY);
    }

    private static boolean applicable(Perl6PsiElement child) {
        return child instanceof Perl6File ||
               child instanceof Perl6PackageDecl ||
               child instanceof Perl6RoutineDecl ||
               child instanceof Perl6RegexDecl;
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        if (element instanceof Perl6File) {
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return element.getContainingFile().getName();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Override
                public Icon getIcon(boolean b) {
                    return Perl6Icons.CAMELIA;
                }
            };
        }
        if (element instanceof Perl6PackageDecl)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    Perl6PackageDecl pkg = (Perl6PackageDecl)element;
                    return pkg.getPackageName() + " (" + pkg.getPackageKind() + ")";
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean b) {
                    String kind = (((Perl6PackageDecl)element).getPackageKind());
                    switch (kind) {
                        case "module":
                            return Perl6Icons.MODULE;
                        case "class":
                            return Perl6Icons.CLASS;
                        case "role":
                            return Perl6Icons.ROLE;
                        case "grammar":
                            return Perl6Icons.GRAMMAR;
                        default:
                            return Perl6Icons.PACKAGE;
                    }
                }
            };
        if (element instanceof Perl6RegexDecl)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    Perl6RegexDecl rx = (Perl6RegexDecl)element;
                    return rx.getRegexName() + " (" + rx.getRegexKind() + ")";
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean b) {
                    return Perl6Icons.REGEX;
                }
            };
        if (element instanceof Perl6RoutineDecl)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    Perl6RoutineDecl r = (Perl6RoutineDecl)element;
                    return r.getSignature() + " (" + r.getRoutineKind() + ")";
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean b) {
                    if (((Perl6RoutineDecl)element).getRoutineKind().equals("method"))
                        return Perl6Icons.METHOD;
                    return Perl6Icons.SUB;
                }
            };
        return null;
    }

    @Override
    public Perl6PsiElement getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        element.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return element.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element.canNavigateToSource();
    }
}
