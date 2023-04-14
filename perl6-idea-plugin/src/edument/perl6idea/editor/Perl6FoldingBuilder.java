package edument.perl6idea.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Perl6FoldingBuilder extends FoldingBuilderEx implements DumbAware {
    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        int recursionLevel = 0;
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        getLevelFolding(root, recursionLevel, descriptors);
        Perl6StatementList list = PsiTreeUtil.getChildOfType(root, Perl6StatementList.class);

        // For individual delimited blocks, fold each one.
        List<Perl6PsiElement> singleBlocks = PsiTreeUtil.getChildrenOfTypeAsList(list, PodBlockDelimited.class);
        for (Perl6PsiElement block : singleBlocks) {
            String firstLine = block.getText().lines().findFirst().orElse("=pod");
            int endOffset = block.getTextOffset() + block.getText().stripTrailing().length();
            descriptors.add(new FoldingDescriptor(block.getNode(),
                                                  new TextRange(block.getTextOffset(), endOffset),
                                                  FoldingGroup.newGroup("pod-delimited"),
                                                  firstLine));
        }

        // For paragraph and delimited floating blocks, find sequences of them.
        List<Perl6PsiElement> podSeparated = PsiTreeUtil.getChildrenOfAnyType(list, PodBlockAbbreviated.class, PodBlockParagraph.class);
        List<List<Perl6PsiElement>> podGroups = new ArrayList<>(podSeparated.size());
        Perl6PsiElement lastPod = null;
        for (Perl6PsiElement pod : podSeparated) {
            // See if it should go in a new group, which is the case if it's not immediately
            // after the last element we saw.
            boolean newGroup = pod.getPrevSibling() != lastPod;
            lastPod = pod;

            // Add to group or create new group.
            if (newGroup)
                podGroups.add(new ArrayList<>());
            podGroups.get(podGroups.size() - 1).add(pod);
        }
        for (List<Perl6PsiElement> group : podGroups) {
            Perl6PsiElement first = group.get(0);
            Perl6PsiElement last = group.get(group.size() - 1);
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
        Collection<Perl6Blockoid> blocks = PsiTreeUtil.findChildrenOfType(root, Perl6Blockoid.class);
        for (final Perl6Blockoid block : blocks) {
            descriptors.add(new FoldingDescriptor(block.getNode(),
                                                  block.getTextRange(), FoldingGroup.newGroup("perl6-" + recursionLevel)));
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
