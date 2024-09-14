package org.raku.cro.template.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.RakuIcons;
import org.raku.cro.template.psi.CroTemplateFile;
import org.raku.cro.template.psi.CroTemplateMacro;
import org.raku.cro.template.psi.CroTemplateSub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class CroTemplateStructureViewElement implements StructureViewTreeElement {
    private final NavigatablePsiElement element;

    public CroTemplateStructureViewElement(NavigatablePsiElement element) {
        this.element = element;
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        if (element instanceof CroTemplateFile)
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
                    return RakuIcons.CRO;
                }
            };
        if (element instanceof CroTemplateMacro || element instanceof CroTemplateSub)
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return element.getName();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Override
                public Icon getIcon(boolean b) {
                    return RakuIcons.CRO;
                }
            };
        throw new IllegalArgumentException();
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<StructureViewTreeElement> structureElements = new ArrayList<>();
        Queue<NavigatablePsiElement> visit = new ArrayDeque<>();
        visit.add(element);
        while (!visit.isEmpty()) {
            NavigatablePsiElement current = visit.remove();
            if (appearsInStructureView(current)) {
                structureElements.add(new CroTemplateStructureViewElement(current));
            }
            else {
                NavigatablePsiElement[] children = PsiTreeUtil.getChildrenOfType(current, NavigatablePsiElement.class);
                if (children != null)
                    visit.addAll(Arrays.asList(children));
            }
        }
        return structureElements.toArray(StructureViewTreeElement.EMPTY_ARRAY);
    }

    private static boolean appearsInStructureView(NavigatablePsiElement current) {
        return current instanceof CroTemplateSub || current instanceof CroTemplateMacro;
    }


    @Override
    public NavigatablePsiElement getValue() {
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
