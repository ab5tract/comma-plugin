package edument.perl6idea.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;

public class Perl6StatementTerminatorBlock extends Perl6Block {
    Perl6StatementTerminatorBlock(ASTNode node,
                                  Wrap wrap,
                                  Alignment align,
                                  CodeStyleSettings settings) {
        super(node, wrap, align, settings);
    }
}
