package edument.perl6idea.formatter;

import com.intellij.formatting.*;
import com.intellij.formatting.templateLanguages.BlockWithParent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6OPPElementTypes;
import edument.perl6idea.psi.*;
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
    private TokenSet LARGE_BLOCK = TokenSet.create(PACKAGE_DECLARATION, ROUTINE_DECLARATION);

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
            Perl6Block childBlock;
            if (child.getElementType() == BLOCKOID
                    && LARGE_BLOCK.contains(child.getTreeParent().getElementType())) {
                childBlock = new Perl6LargeBlockoidBlock(child, null, null, mySettings);
            }
            else if (child.getElementType() == STATEMENT) {
                childBlock = new Perl6StatementBlock(child, null, null, mySettings);
            }
            else if (child.getElementType() == STATEMENT_TERMINATOR && child.getText().equals(";")) {
                childBlock = new Perl6StatementTerminatorBlock(child, null, null, mySettings);
            }
            else if (child.getElementType() != STATEMENT_LIST) {
                Boolean childIsStatementContinuation = null;
                if (isStatementContinuation != null && isStatementContinuation)
                    childIsStatementContinuation = false;
                else if (nodeInStatementContinuation(child))
                    childIsStatementContinuation = true;
                childBlock = new Perl6Block(child, null, null, mySettings,
                                           childIsStatementContinuation);
            }
            else {
                childBlock = new Perl6StatementListBlock(child, null, null, mySettings);
            }
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
        if (myNode.getElementType() == SEMI_LIST && myNode.getTreeParent().getElementType() == ARRAY_COMPOSER)
            return myNode.getTextLength() == 0 ? Indent.getNoneIndent() : Indent.getNormalIndent();
        if (isStatementContinuation != null && isStatementContinuation)
            return Indent.getContinuationWithoutFirstIndent();
        return Indent.getNoneIndent();
    }

    private static boolean nodeInStatementContinuation(ASTNode startNode) {
        ASTNode curNode = startNode;

        /* Check if we're in a hash literal, which we won't treat as a
        * continuation if at statement level. */
        if (startNode.getElementType() == Perl6OPPElementTypes.INFIX_APPLICATION ||
                startNode.getElementType() == FATARROW ||
                startNode.getElementType() == Perl6ElementTypes.COLON_PAIR) {
            Perl6Blockoid maybeBlockoid = PsiTreeUtil.getParentOfType(startNode.getPsi(), Perl6Blockoid.class);
            if (maybeBlockoid != null) {
                if (maybeBlockoid.getParent() instanceof Perl6BlockOrHash
                    && maybeBlockoid.getChildren().length > 0) {
                    PsiElement hopefullyStatementList = maybeBlockoid.getChildren()[0];
                    if (hopefullyStatementList instanceof Perl6StatementList
                        && hopefullyStatementList.getChildren().length > 0) {
                        PsiElement hopefullyStatement = hopefullyStatementList.getChildren()[0];
                        if (hopefullyStatement instanceof Perl6Statement
                            && hopefullyStatementList.getChildren().length > 0) {
                            PsiElement hopefullyInfix = hopefullyStatement.getChildren()[0];
                            if (hopefullyInfix instanceof Perl6InfixApplication &&
                                hopefullyInfix.getChildren().length >= 2 &&
                                hopefullyInfix.getChildren()[1].getText().equals(",")) {
                                PsiElement hopefullyPairish = hopefullyInfix.getChildren()[0];
                                if (hopefullyPairish instanceof Perl6FatArrow || hopefullyPairish instanceof Perl6ColonPair)
                                    return false;
                            }
                        }
                    }
                }
            }
        }

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
        else if (NOT_CONTINUATIONY.contains(elementType) || isInHashLiteral(myNode)) {
            return new ChildAttributes(Indent.getNoneIndent(), null);
        }
        else {
            return new ChildAttributes(Indent.getContinuationWithoutFirstIndent(), null);
        }
    }

    private boolean isInHashLiteral(ASTNode node) {
        PsiElement psi = node.getPsi();
        // Should be infix:<,> and first element should be hash-like.
        if (psi instanceof Perl6InfixApplication && psi.getChildren().length >= 2 &&
                psi.getChildren()[1].getText().equals(",")) {
            PsiElement hopefullyPairish = psi.getChildren()[0];
            if (hopefullyPairish instanceof Perl6FatArrow || hopefullyPairish instanceof Perl6ColonPair) {
                // Matches here, but check the parents are as expected.
                PsiElement hopefullyStatement = psi.getParent();
                if (hopefullyStatement instanceof Perl6Statement) {
                    PsiElement hopefullyStatementList = hopefullyStatement.getParent();
                    if (hopefullyStatementList instanceof Perl6StatementList) {
                        PsiElement hopefullyBlockoid = hopefullyStatementList.getParent();
                        if (hopefullyBlockoid instanceof Perl6Blockoid)
                            if (hopefullyBlockoid.getParent() instanceof Perl6BlockOrHash)
                                return true;
                    }
                }
            }
        }
        return false;
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
