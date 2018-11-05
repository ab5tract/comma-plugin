package edument.perl6idea.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6LargeBlockoidBlock extends Perl6Block {
    Perl6LargeBlockoidBlock(ASTNode node,
                            Wrap wrap,
                            Alignment align,
                            CodeStyleSettings settings) {
        super(node, wrap, align, settings);
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return Spacing.createSpacing(0, 0, 1, true, 0);
    }
}
