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
        List<Perl6PsiElement> podBlocksSeparated = PsiTreeUtil.getChildrenOfTypeAsList(list, PodBlockAbbreviated.class);
        List<Perl6PsiElement> singleBlocks = PsiTreeUtil.getChildrenOfTypeAsList(list, PodBlockDelimited.class);
        for (Perl6PsiElement block : singleBlocks) {
            int endOffset = block.getTextOffset() + block.getText().stripTrailing().length();
            descriptors.add(new FoldingDescriptor(block.getNode(),
                                                  new TextRange(block.getTextOffset(),
                                                                endOffset),
                                                  FoldingGroup.newGroup("pod-single")));
        }
        for (int i = 0; i < podBlocksSeparated.size(); i += 2) {
            if (podBlocksSeparated.size() % 2 != 0 && i + 1 == podBlocksSeparated.size()) {
                break;
            }
            Perl6PsiElement startBlock = podBlocksSeparated.get(i);
            Perl6PsiElement endBlock = podBlocksSeparated.get(i + 1);
            int size = endBlock.getText().stripTrailing().length();
            descriptors.add(new FoldingDescriptor(startBlock.getNode(),
                                                  new TextRange(startBlock.getTextOffset(),
                                                                endBlock.getTextOffset() + size),
                                                  FoldingGroup.newGroup("pod-joined")));
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
