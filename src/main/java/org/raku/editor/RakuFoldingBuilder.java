package org.raku.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RakuFoldingBuilder extends FoldingBuilderEx implements DumbAware {
    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        int recursionLevel = 0;
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        getLevelFolding(root, recursionLevel, descriptors);
        RakuStatementList list = PsiTreeUtil.getChildOfType(root, RakuStatementList.class);

        // For individual delimited blocks, fold each one.
        List<RakuPsiElement> singleBlocks = PsiTreeUtil.getChildrenOfTypeAsList(list, PodBlockDelimited.class);
        for (RakuPsiElement block : singleBlocks) {
            String firstLine = block.getText().lines().findFirst().orElse("=pod");
            int endOffset = block.getTextOffset() + block.getText().stripTrailing().length();
            descriptors.add(new FoldingDescriptor(block.getNode(),
                                                  new TextRange(block.getTextOffset(), endOffset),
                                                  FoldingGroup.newGroup("pod-delimited"),
                                                  firstLine));
        }

        // For paragraph and delimited floating blocks, find sequences of them.
        List<RakuPsiElement> podSeparated = PsiTreeUtil.getChildrenOfAnyType(list, PodBlockAbbreviated.class, PodBlockParagraph.class);
        List<List<RakuPsiElement>> podGroups = new ArrayList<>(podSeparated.size());
        RakuPsiElement lastPod = null;
        for (RakuPsiElement pod : podSeparated) {
            // See if it should go in a new group, which is the case if it's not immediately
            // after the last element we saw.
            boolean newGroup = pod.getPrevSibling() != lastPod;
            lastPod = pod;

            // Add to group or create new group.
            if (newGroup)
                podGroups.add(new ArrayList<>());
            podGroups.get(podGroups.size() - 1).add(pod);
        }
        for (List<RakuPsiElement> group : podGroups) {
            RakuPsiElement first = group.get(0);
            RakuPsiElement last = group.get(group.size() - 1);
            String firstLine = first.getText().lines().findFirst().orElse("=pod");
            int endOffset = last.getTextOffset() + last.getText().stripTrailing().length();
            descriptors.add(new FoldingDescriptor(first.getNode(),
                                                  new TextRange(first.getTextOffset(), endOffset),
                                                  FoldingGroup.newGroup("pod-abbreviated"),
                                                  firstLine));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    private static void getLevelFolding(@NotNull PsiElement root, int recursionLevel, List<FoldingDescriptor> descriptors) {
        Collection<RakuBlockoid> blocks = PsiTreeUtil.findChildrenOfType(root, RakuBlockoid.class);
        for (final RakuBlockoid block : blocks) {
            descriptors.add(new FoldingDescriptor(block.getNode(),
                                                  block.getTextRange(), FoldingGroup.newGroup("raku-" + recursionLevel)));
            getLevelFolding(block, recursionLevel + 1, descriptors);
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return "{...}";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
