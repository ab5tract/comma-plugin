package edument.perl6idea.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Perl6FoldingBuilder extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        int recursionLevel = 0;
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        getLevelFolding(root, recursionLevel, descriptors);
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private void getLevelFolding(@NotNull PsiElement root, int recursionLevel, List<FoldingDescriptor> descriptors) {
        Collection<Perl6Block> blocks = PsiTreeUtil.findChildrenOfType(root, Perl6Block.class);
        for (final Perl6Block block : blocks) {
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
