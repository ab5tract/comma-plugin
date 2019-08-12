package edument.perl6idea.formatter;

import com.intellij.formatting.*;
import com.intellij.formatting.templateLanguages.BlockWithParent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.parsing.Perl6OPPElementTypes;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static edument.perl6idea.parsing.Perl6ElementTypes.*;
import static edument.perl6idea.parsing.Perl6ElementTypes.HEREDOC;
import static edument.perl6idea.parsing.Perl6ElementTypes.INFIX;
import static edument.perl6idea.parsing.Perl6TokenTypes.*;

class Perl6Block extends AbstractBlock implements BlockWithParent {
    private BlockWithParent myParent;
    private CodeStyleSettings mySettings;
    private Boolean isStatementContinuation;
    private List<Block> children;

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
            IElementType elementType = child.getElementType();
            if (WHITESPACES.contains(elementType))
                continue;
            Perl6Block childBlock;
            if (elementType == BLOCKOID
                && LARGE_BLOCK.contains(child.getTreeParent().getElementType())) {
                childBlock = new Perl6LargeBlockoidBlock(child, null, null, mySettings);
            }
            else if (elementType == STATEMENT) {
                childBlock = new Perl6StatementBlock(child, null, null, mySettings);
            }
            else if (elementType == STATEMENT_TERMINATOR && child.getText().equals(";")) {
                childBlock = new Perl6StatementTerminatorBlock(child, null, null, mySettings);
            }
            else if (elementType == STATEMENT_LIST) {
                childBlock = new Perl6StatementListBlock(child, null, null, mySettings);
            }
            else {
                Boolean childIsStatementContinuation = null;
                if (isStatementContinuation != null && isStatementContinuation)
                    childIsStatementContinuation = false;
                else if (nodeInStatementContinuation(child))
                    childIsStatementContinuation = true;
                childBlock = new Perl6Block(child, null, null, mySettings,
                                           childIsStatementContinuation);
            }
            childBlock.setParent(this);
            children.add(childBlock);
        }
        this.children = children;
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
        if (myNode.getElementType() == ARRAY_COMPOSER_CLOSE)
            return Indent.getNoneIndent();
        if (isStatementContinuation != null && isStatementContinuation)
            return Indent.getContinuationWithoutFirstIndent();
        return Indent.getNoneIndent();
    }

    public List<Block> getChildren() {
        return children;
    }

    private static boolean nodeInStatementContinuation(ASTNode startNode) {
        ASTNode curNode = startNode;

        /* Check if we're in a hash or array literal, which we won't treat as a
        * continuation if at statement level. */
        ASTNode infixApplication = null;
        IElementType startType = startNode.getElementType();
        if (startType != INFIX && startNode.getTreeParent().getElementType() == Perl6OPPElementTypes.INFIX_APPLICATION)
            infixApplication = startNode.getTreeParent();
        if (infixApplication != null && infixApplication.getPsi().getChildren().length >= 2) {
            PsiElement infixApplicationPsi = infixApplication.getPsi();
            PsiElement infix = infixApplication.getPsi().getChildren()[1];
            if (infix.getText().equals(",")) {
                PsiElement hopefullyStatement = infixApplicationPsi.getParent();
                if (hopefullyStatement instanceof Perl6Statement) {
                    PsiElement statementHolder = hopefullyStatement.getParent();
                    if (statementHolder instanceof Perl6StatementList) {
                        // Might be in a hash initializer. Check if the first child of the infix
                        // application is pair-like.
                        PsiElement hopefullyPairish = infixApplicationPsi.getChildren()[0];
                        if (hopefullyPairish instanceof Perl6FatArrow || hopefullyPairish instanceof Perl6ColonPair) {
                            // It's fine, now just check we're in the appropriate kind of block.
                            PsiElement hopefullyBlockoid = statementHolder.getParent();
                            if (hopefullyBlockoid instanceof Perl6Blockoid &&
                                    hopefullyBlockoid.getParent() instanceof Perl6BlockOrHash)
                                 return false;
                        }
                    }
                    else if (statementHolder instanceof Perl6SemiList) {
                        // Might be an array literal.
                        if (statementHolder.getParent() instanceof Perl6ArrayComposer)
                            return false;
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
                return statementPrefix.contains("\n") && !statementPrefix.endsWith("}\n") &&
                    !statementPrefix.endsWith("]\n");
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
        if (elementType == REGEX_GROUP || elementType == ARRAY_COMPOSER) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }
        else if (elementType == BLOCKOID) {
            if (newIndex == children.size() - 1) {
                // Maybe adding at the end of the blockoid; see if the last statement is
                // complete.
                Block maybeStatementList = children.get(1);
                if (maybeStatementList instanceof Perl6StatementListBlock) {
                    List<Block> stmts = ((Perl6StatementListBlock)maybeStatementList).getChildren();
                    if (stmts.size() > 0) {
                        Block lastStatement = stmts.get(stmts.size() - 1);
                        if (lastStatement instanceof AbstractBlock) {
                            // This is incredibly cheaty, but: if the thing doesn't contain a
                            // ; or a } then it wasn't terminated. If it does, well, who knows,
                            // but it's better than getting it wrong all of the time.
                            String lastStmtText = ((AbstractBlock)lastStatement).getNode().getText();
                            if (shouldBeContinued(lastStmtText)) {
                                int indent = mySettings.getIndentSize(Perl6ModuleFileType.INSTANCE) +
                                             mySettings.getContinuationIndentSize(Perl6ModuleFileType.INSTANCE);
                                return new ChildAttributes(Indent.getSpaceIndent(indent), null);
                            }
                        }
                    }
                }
            }
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }
        else if (isStatementContinuation != null && isStatementContinuation) {
            return new ChildAttributes(Indent.getNoneIndent(), null);
        }
        else if (NOT_CONTINUATIONY.contains(elementType) || isInHashOrArrayLiteral(myNode)) {
            return new ChildAttributes(Indent.getNoneIndent(), null);
        }
        else {
            return new ChildAttributes(Indent.getContinuationWithoutFirstIndent(), null);
        }
    }

    private static boolean shouldBeContinued(String lastStmtText) {
        // Get last line entered.
        String[] lines = lastStmtText.trim().split("\n");
        if (lines.length == 0)
            return false;
        String lastLine = lines[lines.length - 1].trim();

        // Comments don't cause continuations.
        if (lastLine.startsWith("#"))
            return false;

        // Otherwise, strip any comment/whitespace and see if it ends with ; or }.
        String checkEnd = lastLine.replaceAll("\\s*#.*", "");
        return !checkEnd.endsWith(";") && !checkEnd.endsWith("}");
    }

    private static boolean isInHashOrArrayLiteral(ASTNode node) {
        PsiElement psi = node.getPsi();
        // Should be infix:<,>.
        if (psi instanceof Perl6InfixApplication && psi.getChildren().length >= 2 &&
                psi.getChildren()[1].getText().equals(",")) {
            // Check what kind of parentage we have.
            PsiElement hopefullyStatement = psi.getParent();
            if (hopefullyStatement instanceof Perl6Statement) {
                PsiElement statementHolder = hopefullyStatement.getParent();
                if (statementHolder instanceof Perl6StatementList) {
                    PsiElement hopefullyBlockoid = statementHolder.getParent();
                    if (hopefullyBlockoid instanceof Perl6Blockoid) {
                        if (hopefullyBlockoid.getParent() instanceof Perl6BlockOrHash) {
                            // Could be a hash, just check first child.
                            PsiElement hopefullyPairish = psi.getChildren()[0];
                            return hopefullyPairish instanceof Perl6FatArrow || hopefullyPairish instanceof Perl6ColonPair;
                        }
                    }
                }
                else if (statementHolder instanceof Perl6SemiList) {
                    return statementHolder.getParent() instanceof Perl6ArrayComposer;
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
