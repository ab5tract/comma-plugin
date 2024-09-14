package org.raku.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.RakuIcons;
import org.raku.extensions.RakuFrameworkCall;
import org.raku.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class RakuStructureViewElement implements StructureViewTreeElement {
    private final RakuPsiElement element;
    private final ItemPresentation calculatedPresentation;

    public RakuStructureViewElement(RakuPsiElement element) {
        this.element = element;
        this.calculatedPresentation = getPresentation();
    }

    public RakuStructureViewElement(RakuPsiElement element, ItemPresentation calculatedPresentation) {
        this.element = element;
        this.calculatedPresentation = calculatedPresentation;
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<StructureViewTreeElement> structureElements = new ArrayList<>();
        if (element instanceof RakuPsiScope) {
            // Add declarations.
            List<RakuPsiDeclaration> declarations = ((RakuPsiScope)element).getDeclarations();
            Set<RakuPsiDeclaration> declSet = new HashSet<>();
            for (RakuPsiDeclaration decl : declarations) {
                if (applicable(decl)) {
                    structureElements.add(new RakuStructureViewElement(decl));
                    declSet.add(decl);
                }
            }

            // Look through statements to see if there's any DSL-like framework
            // calls.
            RakuFrameworkCall[] extensions = RakuFrameworkCall.EP_NAME.getExtensions();
            collectFrameworkCalls(structureElements, extensions, declSet, element);
        }
        return structureElements.toArray(StructureViewTreeElement.EMPTY_ARRAY);
    }

    private static void collectFrameworkCalls(List<StructureViewTreeElement> structureElements,
                                              RakuFrameworkCall[] extensions,
                                              Set<RakuPsiDeclaration> stoppers,
                                              RakuPsiElement search) {
        // If we hit a stopper, don't enter it; it's another declaration in the tree.
        if (search instanceof RakuPsiDeclaration && stoppers.contains(search))
            return;

        // See if this element is a DSL call.
        if (search instanceof RakuSubCall call) {
            for (RakuFrameworkCall ext : extensions) {
                if (ext.isApplicable(call)) {
                    ItemPresentation presentation = ext.getStructureViewPresentation(call, ext.getFrameworkData(call));
                    structureElements.add(new RakuStructureViewElement(call, presentation));
                    return;
                }
            }
        }

        // Otherwise, walk children.
        RakuPsiElement[] children = PsiTreeUtil.getChildrenOfType(search, RakuPsiElement.class);
        if (children != null)
            for (RakuPsiElement child : children)
                collectFrameworkCalls(structureElements, extensions, stoppers, child);
    }

    private static boolean applicable(RakuPsiElement child) {
        return child instanceof RakuFile ||
               child instanceof RakuPackageDecl && child.getName() != null ||
               child instanceof RakuRoutineDecl && child.getName() != null ||
               child instanceof RakuRegexDecl && child.getName() != null ||
               child instanceof RakuConstant && child.getName() != null ||
               (child instanceof RakuVariableDecl && ((RakuVariableDecl)child).getScope().equals("has")
                && child.getName() != null) ||
               child instanceof RakuSubset && child.getName() != null ||
               child instanceof RakuEnum && child.getName() != null;
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        if (calculatedPresentation != null)
            return calculatedPresentation;
        if (element instanceof RakuFile) {
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
                    return RakuIcons.CAMELIA;
                }
            };
        }
        if (element instanceof RakuPackageDecl)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    RakuPackageDecl pkg = (RakuPackageDecl)element;
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
                    return RakuIcons.iconForPackageDeclarator(((RakuPackageDecl)element).getPackageKind());
                }
            };
        if (element instanceof RakuRegexDecl)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return ((RakuRegexDecl)element).getSignature();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Override
                public Icon getIcon(boolean b) {
                    return RakuIcons.REGEX;
                }
            };
        if (element instanceof RakuRoutineDecl)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return ((RakuRoutineDecl)element).getSignature();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Override
                public Icon getIcon(boolean b) {
                    if (((RakuRoutineDecl)element).getRoutineKind().equals("method"))
                        return RakuIcons.METHOD;
                    return RakuIcons.SUB;
                }
            };
        if (element instanceof RakuConstant)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return ((RakuConstant)element).getConstantName();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Override
                public Icon getIcon(boolean b) {
                    return RakuIcons.CONSTANT;
                }
            };
        if (element instanceof RakuVariableDecl)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return String.join(", ", ((RakuVariableDecl)element).getVariableNames());
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Override
                public Icon getIcon(boolean b) {
                    return RakuIcons.ATTRIBUTE;
                }
            };
        if (element instanceof RakuSubset)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return ((RakuSubset)element).getSubsetName();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Override
                public Icon getIcon(boolean b) {
                    return RakuIcons.SUBSET;
                }
            };
        if (element instanceof RakuEnum)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return ((RakuEnum)element).getEnumName();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Override
                public Icon getIcon(boolean b) {
                    return RakuIcons.ENUM;
                }
            };
        throw new IllegalArgumentException();
    }

    @Override
    public RakuPsiElement getValue() {
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
