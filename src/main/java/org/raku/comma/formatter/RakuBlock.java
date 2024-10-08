package org.raku.comma.formatter;

import com.intellij.formatting.*;
import com.intellij.formatting.templateLanguages.BlockWithParent;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.raku.comma.parsing.RakuElementTypes;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.*;
import org.raku.comma.utils.RakuPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.raku.comma.parsing.RakuElementTypes.*;
import static org.raku.comma.parsing.RakuOPPElementTypes.INFIX_APPLICATION;
import static org.raku.comma.parsing.RakuTokenTypes.*;

class RakuBlock extends AbstractBlock implements BlockWithParent {
    private final static boolean DEBUG_MODE = false;
    private static final TokenSet TRAIT_CARRIERS = TokenSet.create(
        PACKAGE_DECLARATION,
        ROUTINE_DECLARATION,
        VARIABLE_DECLARATION,
        PARAMETER,
        REGEX_DECLARATION,
        ENUM,
        SUBSET,
        CONSTANT
    );
    private final List<BiFunction<RakuBlock, RakuBlock, Spacing>> myRules;
    private final CommonCodeStyleSettings myCommonSettings;
    private final RakuCodeStyleSettings myCustomSettings;
    private BlockWithParent myParent;
    private final Boolean isStatementContinuation;

    private final TokenSet WHITESPACES = TokenSet.create(
        UNV_WHITE_SPACE,
        WHITE_SPACE,
        VERTICAL_WHITE_SPACE,
        UNSP_WHITE_SPACE
    );
    private ArrayList<Block> children;

    RakuBlock(ASTNode node,
              Wrap wrap,
              Alignment align,
              CommonCodeStyleSettings commonSettings,
              RakuCodeStyleSettings customSettings,
              List<BiFunction<RakuBlock, RakuBlock, Spacing>> rules)
    {
        super(node, wrap, align);
        myRules = rules;
        myCommonSettings = commonSettings;
        myCustomSettings = customSettings;
        this.isStatementContinuation = false;
    }

    private RakuBlock(ASTNode node,
                      Wrap wrap,
                      Alignment align,
                      Boolean isStatementContinuation,
                      CommonCodeStyleSettings commonSettings,
                      RakuCodeStyleSettings customSettings,
                      List<BiFunction<RakuBlock, RakuBlock, Spacing>> rules)
    {
        super(node, wrap, align);
        myRules = rules;
        myCommonSettings = commonSettings;
        myCustomSettings = customSettings;
        this.isStatementContinuation = isStatementContinuation;
    }

    @Override
    protected List<Block> buildChildren() {
        if (isLeaf()) return EMPTY;
        final ArrayList<Block> children = new ArrayList<>();

        Pair<Function<ASTNode, Boolean>, Alignment> alignFunction = calculateAlignment(myNode);

        for (ASTNode child = getNode().getFirstChildNode(); child != null; child = child.getTreeNext()) {
            IElementType elementType = child.getElementType();
            if (WHITESPACES.contains(elementType)) continue;

            RakuBlock childBlock;
            Boolean childIsStatementContinuation = null;
            if (isStatementContinuation != null && isStatementContinuation) {
                childIsStatementContinuation = false;
            } else if (nodeInStatementContinuation(child)) {
                childIsStatementContinuation = true;
            }
            Alignment align = alignFunction == null
                                  ? null
                                  : alignFunction.first.apply(child)
                                        ? alignFunction.second
                                        : null;
            childBlock = new RakuBlock(child, null, align, childIsStatementContinuation, myCommonSettings, myCustomSettings, myRules);
            childBlock.setParent(this);
            children.add(childBlock);
        }
        return this.children = children;
    }

    @Override
    public Wrap getWrap() {
        // Basic sanity check: we don't wrap anything when interpolated in a str literal
        if (! (PsiTreeUtil.getParentOfType(myNode.getPsi(), RakuFile.class, RakuStrLiteral.class, RakuHeredoc.class) instanceof RakuFile)) {
            return null;
        }

        if (myNode.getElementType() == PARAMETER && myCustomSettings.PARAMETER_WRAP) {
            return Wrap.createWrap(WrapType.NORMAL, true);
        }

        if (myNode.getElementType() == RakuElementTypes.TRAIT && myCustomSettings.TRAIT_WRAP) {
            return Wrap.createWrap(WrapType.NORMAL, true);
        }

        if (myNode.getPsi() instanceof RakuMethodCall
            && myNode.getText().startsWith(".")
            && myCustomSettings.METHOD_CALL_WRAP)
        {
            return Wrap.createWrap(WrapType.NORMAL, false);
        }

        if (myNode.getTreeParent() != null
            && myNode.getTreeParent().getPsi() instanceof RakuInfixApplication application
            && myNode.getElementType() != RakuTokenTypes.NULL_TERM
            && myNode.getElementType() != RakuElementTypes.INFIX)
        {
            PsiElement parent = PsiTreeUtil.getParentOfType(application, RakuSubCall.class, RakuMethodCall.class,
                                                            RakuArrayComposer.class, RakuVariableDecl.class);
            if (application.getOperator().equals(",")
                && (parent instanceof RakuSubCall || parent instanceof RakuMethodCall))
            {
                return myCustomSettings.CALL_ARGUMENTS_WRAP
                       ? Wrap.createWrap(WrapType.NORMAL, false)
                       : null;
            }

            if (application.getOperator().equals(",")
                && (parent instanceof RakuSubCall || parent instanceof RakuMethodCall)
                && parent instanceof RakuArrayComposer || parent instanceof RakuVariableDecl)
            {
                return myCustomSettings.ARRAY_ELEMENTS_WRAP
                       ? Wrap.createWrap(WrapType.NORMAL, false)
                       : null;
            }

            if (!application.getOperator().equals(".") && myCustomSettings.INFIX_APPLICATION_WRAP) {
                return Wrap.createWrap(WrapType.NORMAL, false);
            }
        }

        return null;
    }

    @Nullable
    private Pair<Function<ASTNode, Boolean>, Alignment> calculateAlignment(ASTNode node) {
        IElementType type = node.getElementType();
        if (type == SIGNATURE && myCustomSettings.PARAMETER_ALIGNMENT) {
            return Pair.create((child) -> child.getElementType() == PARAMETER, Alignment.createAlignment());
        } else if (type == ARRAY_COMPOSER && myCustomSettings.ARRAY_ELEMENTS_ALIGNMENT) {
            return Pair.create((child) -> child.getElementType() == ARRAY_COMPOSER_OPEN
                                            && child.getElementType() == ARRAY_COMPOSER_CLOSE,
                               Alignment.createAlignment());
        } else if (type == INFIX_APPLICATION && !(node.getPsi().getLastChild() instanceof RakuMethodCall)) {
            if (! (node.getPsi() instanceof RakuInfixApplication infixApp)) return null;

            // TODO: Set up a rule for ternaries
            if (infixApp.getOperator().equals("??")) return null; // Do not align ?? !!, we'll just indent it

            if (infixApp.getOperator().equals(",")) {
                // Do not touch heredoc for real
                if (ContainerUtil.exists(infixApp.getOperands(), s -> s instanceof RakuHeredoc)) {
                    return null;
                }
                // If we have a comma separated list, there can be two cases: non-literal array init or signature
                // check those cases, otherwise just do as infix guides us
                PsiElement origin = PsiTreeUtil.getParentOfType(infixApp,
                                                                RakuVariableDecl.class,
                                                                RakuCodeBlockCall.class,
                                                                RakuArrayComposer.class,
                                                                RakuStatement.class);
                // This case is handled by another option
                if (origin instanceof RakuArrayComposer) return null;
                if (origin instanceof RakuCodeBlockCall) {
                    return myCustomSettings.CALL_ARGUMENTS_ALIGNMENT
                           ? Pair.create((child) -> child.getElementType() != RakuTokenTypes.INFIX
                                                        && child.getElementType() != RakuTokenTypes.NULL_TERM,
                                         Alignment.createAlignment())
                           : null;
                } else {
                    return myCustomSettings.ARRAY_ELEMENTS_ALIGNMENT
                           ? Pair.create((child) -> child.getElementType() != RakuTokenTypes.INFIX
                                                    && child.getElementType() != RakuTokenTypes.NULL_TERM,
                                         Alignment.createAlignment())
                           : null;
                }
            }

            if (myCustomSettings.INFIX_APPLICATION_ALIGNMENT) {
                return Pair.create((child) -> child.getElementType() != RakuTokenTypes.INFIX
                                                && child.getElementType() != RakuTokenTypes.NULL_TERM,
                                   Alignment.createAlignment());
            }
        } else if (TRAIT_CARRIERS.contains(type) && myCustomSettings.TRAIT_ALIGNMENT) {
            return Pair.create((child) -> child.getElementType() == RakuElementTypes.TRAIT, Alignment.createAlignment());
        }

        return null;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        if (!(child1 instanceof RakuBlock left) || !(child2 instanceof RakuBlock right)) {
            return null;
        }

        if (right.getNode().getElementType() == RakuElementTypes.REGEX_SIGSPACE) {
            return null;
        }

        int i = 0;
        for (BiFunction<RakuBlock, RakuBlock, Spacing> rule : myRules) {
            i++;
            Spacing result = rule.apply(left, right);
            if (result != null) {
                if (DEBUG_MODE) {
                    System.out.printf("Left: %s [%s]%n", left.getNode().getElementType(), left.getNode().getText());
                    System.out.printf("Right: %s [%s]%n", right.getNode().getElementType(), right.getNode().getText());
                    System.out.println("Applied rule: " + i);
                }
                return result;
            }
        }
        if (DEBUG_MODE) {
            System.out.printf("Left: %s [%s]%n", left.getNode().getElementType(), left.getNode().getText());
            System.out.printf("Right: %s [%s]%n", right.getNode().getElementType(), right.getNode().getText());
            System.out.println("==> No rule was applied.");
        }
        return null;
    }

    @Override
    public Indent getIndent() {
        if (myNode.getTreeParent() == null) return null;

        if (myNode.getTreeParent().getPsi() instanceof RakuHeredoc) {
            return Indent.getAbsoluteNoneIndent();
        }

        if (myNode.getTreeParent().getPsi() instanceof RakuBlockoid
            && myNode.getTreeNext() != null && myNode.getTreePrev() != null)
        {
            return myNode.getTextLength() == 0
                   ? Indent.getNoneIndent()
                   : Indent.getNormalIndent();
        }

        if (myNode.getElementType() == SEMI_LIST
            && (myNode.getTreeParent().getElementType() == ARRAY_COMPOSER
                || myNode.getTreeParent().getElementType() == RakuElementTypes.CONTEXTUALIZER))
        {
            return myNode.getTextLength() == 0
                   ? Indent.getNoneIndent()
                   : Indent.getNormalIndent();
        }

        if (myNode.getElementType() == STATEMENT_TERMINATOR) {
            return Indent.getContinuationIndent();
        }

        if (isStatementContinuation != null && isStatementContinuation) {
            if (myNode.getElementType() == PARENTHESES_CLOSE
                && myNode.getTreeParent().getPsi() instanceof RakuSignature)
            {
                return Indent.getSpaceIndent(1, true);
            }
            else if (myNode.getElementType() == PARENTHESES_CLOSE
                && myNode.getTreeParent().getPsi() instanceof RakuSubCall subCall)
            {
                if (subCall.getCallArguments().length != 0) {
                    PsiElement infix = subCall.getCallArguments()[0].getParent();
                    return Indent.getSpaceIndent(infix instanceof RakuInfixApplication
                                                 ? infix.getStartOffsetInParent()
                                                 : 0, true);
                }
            }
            else if (myNode.getElementType() == ARRAY_COMPOSER_CLOSE) {
                return Indent.getNoneIndent();
            }
            return Indent.getContinuationIndent();
        }

        return Indent.getNoneIndent();
    }

    private static boolean nodeInStatementContinuation(ASTNode startNode) {
        PsiElement startPsi = startNode.getPsi();
        PsiFile file = startPsi.getContainingFile();
        if (file == null || !file.isPhysical()) return false;

        Document doc = file.getViewProvider().getDocument();
        if (doc == null) return false;

        // Check if the node spans over multiple lines, making us want a continuation
        if (doc.getLineNumber(startPsi.getTextOffset()) == doc.getLineNumber(startPsi.getParent().getTextOffset())) {
            return false;
        }

        // Comments can be swept under other statement nodes, but they are in no way a continuation
        if (startPsi instanceof PodPreComment || startPsi instanceof PodPostComment) {
            return false;
        }

        // Traits have continuation indent
        if (startPsi instanceof RakuTrait) return true;

        // While infix is handled below, a special case of `my $foo = <continuation here> ...` is handled here
        if (startPsi.getParent() instanceof RakuVariableDecl) {
            if (RakuPsiUtil.skipSpaces(startPsi.getPrevSibling(), false) instanceof RakuInfix) {
                return true;
            }
        }

        // Now checking parents
        if (startPsi.getParent() instanceof RakuInfixApplication) {
            return !checkIfNonContinuatedInitializer(startPsi);
        } else if (startPsi.getParent() instanceof RakuSubCall ||
                startPsi.getParent() instanceof RakuSignature ||
                startPsi.getParent() instanceof RakuMethodCall ||
                startPsi.getParent() instanceof RakuArrayComposer)
        {
            return true;
        }
        return false;
    }

    private static boolean checkIfNonContinuatedInitializer(PsiElement startPsi) {
        if (((RakuInfixApplication) startPsi.getParent()).getOperator().equals(",")) {
            RakuContextualizer contextualizer = PsiTreeUtil.getParentOfType(startPsi, RakuContextualizer.class);
            if (contextualizer != null && contextualizer.getText().startsWith("%")) {
                return true;
            }

            PsiElement hopefullyStatement = startPsi.getParent().getParent();
            if (hopefullyStatement instanceof RakuStatement) {
                PsiElement statementHolder = hopefullyStatement.getParent();
                if (statementHolder instanceof RakuStatementList) {
                    // Might be in a hash initializer. Check if the first child of the infix
                    // application is pair-like.
                    PsiElement hopefullyPairish = startPsi.getParent().getChildren()[0];
                    if (hopefullyPairish instanceof RakuFatArrow || hopefullyPairish instanceof RakuColonPair) {
                        // It's fine, now just check we're in the appropriate kind of block.
                        PsiElement hopefullyBlockoid = statementHolder.getParent();
                        return hopefullyBlockoid instanceof RakuBlockoid && hopefullyBlockoid.getParent() instanceof RakuBlockOrHash;
                    }
                } else if (statementHolder instanceof RakuSemiList) {
                    // Might be an array literal.
                    if (statementHolder.getParent() instanceof RakuArrayComposer) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newIndex) {
        IElementType elementType = myNode.getElementType();
        if (elementType == REGEX_GROUP || elementType == ARRAY_COMPOSER) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        } else if (elementType == BLOCKOID) {
            List<Block> subblocks;
            Block block = getSubBlocks().get(newIndex - 1);
            while (block != null) {
                subblocks = block.getSubBlocks();
                if (subblocks.size() != 0) {
                    block = subblocks.getLast();
                } else {
                    if (block instanceof RakuBlock
                        && ((RakuBlock) block).getNode().getElementType() == UNTERMINATED_STATEMENT)
                    {
                        return new ChildAttributes(Indent.getContinuationIndent(), obtainAlign((RakuBlock) block));
                    }
                    else {
                        return new ChildAttributes(Indent.getNormalIndent(), null);
                    }
                }
            }
            return new ChildAttributes(Indent.getNormalIndent(), null);
        } else if (elementType == SIGNATURE || elementType == INFIX_APPLICATION) {
            return new ChildAttributes(Indent.getContinuationIndent(), obtainAlign(this));
        } else if (elementType == METHOD_CALL) {
            return new ChildAttributes(Indent.getContinuationIndent(), null);
        } else if (myNode.getPsi() instanceof RakuPsiDeclaration) {
            List<Block> blocks = getSubBlocks();
            Block block = blocks.get(newIndex - 1);
            if (block instanceof RakuBlock) {
                if (((RakuBlock) block).getNode().getPsi() instanceof RakuTrait) {
                    return new ChildAttributes(Indent.getContinuationIndent(), block.getAlignment());
                }
            }
        }
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    private static Alignment obtainAlign(RakuBlock block) {
        IElementType elementType = block.getNode().getElementType();
        if (elementType == SIGNATURE) {
            return block.children.stream()
                                 .map(Block::getAlignment)
                                 .filter(Objects::nonNull)
                                 .findFirst()
                                 .orElse(null);
        } else if (elementType == INFIX_APPLICATION) {
            RakuInfixApplication application = (RakuInfixApplication) block.getNode().getPsi();
            if (application.getOperator().equals("=") &&
                    application.getOperands().length == 2 &&
                    application.getOperands()[1] instanceof RakuInfix)
            {
                return null;
            } else {
                return block.children.stream()
                                     .map(Block::getAlignment)
                                     .filter(Objects::nonNull)
                                     .findFirst()
                                     .orElse(null);
            }
        } else if (elementType == UNTERMINATED_STATEMENT) {
            RakuBlock base = (RakuBlock) block.getParent();
            List<Block> blocks = base.getSubBlocks();
            for (int i = 0; i < blocks.size(); i++) {
                Block temp = blocks.get(i);
                if (temp == block) {
                    return obtainAlign((RakuBlock) blocks.get(i - 1));
                }
            }
        }
        return null;
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
