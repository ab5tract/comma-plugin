package edument.perl6idea.formatter;

import com.intellij.formatting.*;
import com.intellij.formatting.templateLanguages.BlockWithParent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static edument.perl6idea.parsing.Perl6ElementTypes.*;
import static edument.perl6idea.parsing.Perl6ElementTypes.HEREDOC;
import static edument.perl6idea.parsing.Perl6TokenTypes.*;

class Perl6Block extends AbstractBlock implements BlockWithParent {
    private BlockWithParent myParent;
    private CodeStyleSettings mySettings;
    private Boolean isStatementContinuation;

    private TokenSet WHITESPACES = TokenSet.create(
            UNV_WHITE_SPACE, WHITE_SPACE,
            VERTICAL_WHITE_SPACE, UNSP_WHITE_SPACE
    );

    Perl6Block(ASTNode node, Wrap wrap, Alignment align, CodeStyleSettings settings) {
        super(node, wrap, align);
        mySettings = settings;
        this.isStatementContinuation = false;
    }

    Perl6Block(ASTNode node, Wrap wrap, Alignment align, CodeStyleSettings settings,
               Boolean isStatementContinuation) {
        super(node, wrap, align);
        mySettings = settings;
        this.isStatementContinuation = isStatementContinuation;
    }

    @Override
    protected List<Block> buildChildren() {
        if (isLeaf())
            return EMPTY;
        final ArrayList<Block> children = new ArrayList<>();
        for (ASTNode child = getNode().getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (WHITESPACES.contains(child.getElementType()))
                continue;
            Boolean childIsStatementContinuation = null;
            if (child.getElementType() != STATEMENT_LIST) {
                if (isStatementContinuation != null && isStatementContinuation)
                    childIsStatementContinuation = false;
                else if (nodeInStatementContinuation(child))
                    childIsStatementContinuation = true;
            }
            final Perl6Block childBlock = new Perl6Block(child, null, null, mySettings,
                    childIsStatementContinuation);
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
        if ((myNode.getElementType() == STATEMENT_LIST || myNode.getElementType() == REGEX)
            && myNode.getTreeParent().getElementType() == BLOCKOID)
            return myNode.getTextLength() == 0 ? Indent.getNoneIndent() : Indent.getNormalIndent();
        if (isStatementContinuation != null && isStatementContinuation)
            return Indent.getContinuationWithoutFirstIndent();
        return Indent.getNoneIndent();
    }

    private static boolean nodeInStatementContinuation(ASTNode startNode) {
        ASTNode curNode = startNode;
        while (curNode != null && curNode.getElementType() != BLOCKOID) {
            IElementType elementType = curNode.getElementType();
            if (elementType == IF_STATEMENT || elementType == HEREDOC)
                return false;
            if (elementType == STATEMENT) {
                int nodeOffset = startNode.getStartOffset() - curNode.getStartOffset();
                String statementPrefix = curNode.getText().substring(0, nodeOffset);
                return statementPrefix.contains("\n") && !statementPrefix.endsWith("}\n");
            }
            curNode = curNode.getTreeParent();
        }
        return false;
    }

    private static final Set<IElementType> NOT_CONTINUATIONY = new HashSet<>(Arrays.asList(
        FILE, STATEMENT_LIST, IF_STATEMENT, PACKAGE_DECLARATION
    ));

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(final int newIndex) {
        IElementType elementType = myNode.getElementType();
        if (elementType == BLOCKOID || elementType == REGEX_GROUP) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }
        else if (isStatementContinuation != null && isStatementContinuation) {
            return new ChildAttributes(Indent.getNoneIndent(), null);
        }
        else if (NOT_CONTINUATIONY.contains(elementType)) {
            return new ChildAttributes(Indent.getNoneIndent(), null);
        }
        else {
            return new ChildAttributes(Indent.getContinuationWithoutFirstIndent(), null);
        }
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
