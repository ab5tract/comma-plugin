package edument.perl6idea.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.extensions.Perl6FrameworkCall;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class Perl6StructureViewElement implements StructureViewTreeElement {
    private final Perl6PsiElement element;
    private ItemPresentation calculatedPresentation;

    public Perl6StructureViewElement(Perl6PsiElement element) {
        this.element = element;
    }

    public Perl6StructureViewElement(Perl6PsiElement element, ItemPresentation calculatedPresentation) {
        this.element = element;
        this.calculatedPresentation = calculatedPresentation;
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        List<StructureViewTreeElement> structureElements = new ArrayList<>();
        if (element instanceof Perl6PsiScope) {
            // Add declarations.
            List<Perl6PsiDeclaration> declarations = ((Perl6PsiScope)element).getDeclarations();
            Set<Perl6PsiDeclaration> declSet = new HashSet<>();
            for (Perl6PsiDeclaration decl : declarations) {
                if (applicable(decl)) {
                    structureElements.add(new Perl6StructureViewElement(decl));
                    declSet.add(decl);
                }
            }

            // Look through statements to see if there's any DSL-like framework
            // calls.
            Perl6FrameworkCall[] extensions = Perl6FrameworkCall.EP_NAME.getExtensions();
            collectFrameworkCalls(structureElements, extensions, declSet, element);
        }
        return structureElements.toArray(StructureViewTreeElement.EMPTY_ARRAY);
    }

    private static void collectFrameworkCalls(List<StructureViewTreeElement> structureElements,
                                              Perl6FrameworkCall[] extensions,
                                              Set<Perl6PsiDeclaration> stoppers,
                                              Perl6PsiElement search) {
        // If we hit a stopper, don't enter it; it's another declaration in the tree.
        if (stoppers.contains(search))
            return;

        // See if this element is a DSL call.
        if (search instanceof Perl6SubCall) {
            Perl6SubCall call = (Perl6SubCall)search;
            for (Perl6FrameworkCall ext : extensions) {
                if (ext.isApplicable(call)) {
                    ItemPresentation presentation = ext.getStructureViewPresentation(call, ext.getFrameworkData(call));
                    structureElements.add(new Perl6StructureViewElement(call, presentation));
                    return;
                }
            }
        }

        // Otherwise, walk children.
        Perl6PsiElement[] children = PsiTreeUtil.getChildrenOfType(search, Perl6PsiElement.class);
        if (children != null)
            for (Perl6PsiElement child : children)
                collectFrameworkCalls(structureElements, extensions, stoppers, child);
    }

    private static boolean applicable(Perl6PsiElement child) {
        return child instanceof Perl6File ||
               child instanceof Perl6PackageDecl && child.getName() != null ||
               child instanceof Perl6RoutineDecl && child.getName() != null ||
               child instanceof Perl6RegexDecl && child.getName() != null ||
               child instanceof Perl6Constant && child.getName() != null ||
               (child instanceof Perl6VariableDecl && ((Perl6VariableDecl)child).getScope().equals("has")
                    && child.getName() != null) ||
               child instanceof Perl6Subset && child.getName() != null ||
               child instanceof Perl6Enum && child.getName() != null;
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        if (calculatedPresentation != null)
            return calculatedPresentation;
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
                    return pkg.getPackageName();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean b) {
                    return Perl6Icons.iconForPackageDeclarator(((Perl6PackageDecl)element).getPackageKind());
                }
            };
        if (element instanceof Perl6RegexDecl)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return ((Perl6RegexDecl)element).getSignature();
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
                    return ((Perl6RoutineDecl)element).getSignature();
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
        if (element instanceof Perl6Constant)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return ((Perl6Constant)element).getConstantName();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean b) {
                    return Perl6Icons.CONSTANT;
                }
            };
        if (element instanceof Perl6VariableDecl)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return String.join(", ", ((Perl6VariableDecl)element).getVariableNames());
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean b) {
                    return Perl6Icons.ATTRIBUTE;
                }
            };
        if (element instanceof Perl6Subset)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return ((Perl6Subset)element).getSubsetName();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean b) {
                    return Perl6Icons.SUBSET;
                }
            };
        if (element instanceof Perl6Enum)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return ((Perl6Enum)element).getEnumName();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean b) {
                    return Perl6Icons.ENUM;
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
