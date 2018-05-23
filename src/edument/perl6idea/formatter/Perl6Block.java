package edument.perl6idea.formatter;

import com.intellij.formatting.*;
import com.intellij.formatting.templateLanguages.BlockWithParent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static edument.perl6idea.parsing.Perl6ElementTypes.*;
import static edument.perl6idea.parsing.Perl6TokenTypes.*;

class Perl6Block extends AbstractBlock implements BlockWithParent {
    private BlockWithParent myParent;
    private CodeStyleSettings mySettings;

    private TokenSet WHITESPACES = TokenSet.create(
            UNV_WHITE_SPACE, WHITE_SPACE,
            VERTICAL_WHITE_SPACE, UNSP_WHITE_SPACE
    );

    Perl6Block(ASTNode node, Wrap wrap, Alignment align, CodeStyleSettings settings) {
        super(node, wrap, align);
        mySettings = settings;
    }

    @Override
    protected List<Block> buildChildren() {
        if (isLeaf())
            return EMPTY;
        final ArrayList<Block> children = new ArrayList<>();
        for (ASTNode child = getNode().getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (WHITESPACES.contains(child.getElementType()))
                continue;
            final Perl6Block childBlock = new Perl6Block(child, null, null, mySettings);
            childBlock.setParent(this);
            children.add(childBlock);
        }
        return children;
    }

    @Override
    public boolean isIncomplete() {
        return super.isIncomplete();
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return null;
    }

    @Override
    public Indent getIndent() {
        if (myNode.getElementType() == STATEMENT_LIST && myNode.getTreeParent().getElementType() == BLOCKOID)
            return Indent.getNormalIndent();
        if (inStatementContinuation())
            return Indent.getContinuationWithoutFirstIndent();
        return Indent.getNoneIndent();
    }

    private boolean inStatementContinuation() {
        ASTNode curNode = myNode;
        while (curNode != null && curNode.getElementType() != BLOCKOID) {
            if (curNode.getElementType() == STATEMENT) {
                int nodeOffset = myNode.getStartOffset() - curNode.getStartOffset();
                String statementPrefix = curNode.getText().substring(0, nodeOffset);
                return statementPrefix.contains("\n") && !statementPrefix.endsWith("}\n");
            }
            curNode = curNode.getTreeParent();
        }
        return false;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(final int newIndex) {
        return new ChildAttributes(
                myNode.getElementType() == BLOCKOID
                    ? Indent.getNormalIndent()
                    : myNode.getElementType() == FILE || myNode.getElementType() == STATEMENT_LIST
                        ? Indent.getNoneIndent()
                        : Indent.getContinuationWithoutFirstIndent(),
                null);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    @Override
    public BlockWithParent getParent() {
        return myParent;
    }

    @Override
    public void setParent(BlockWithParent newParent) {
        myParent = newParent;
    }
}
