package edument.perl6idea.formatter;

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
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6OPPElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static edument.perl6idea.parsing.Perl6ElementTypes.*;
import static edument.perl6idea.parsing.Perl6OPPElementTypes.INFIX_APPLICATION;
import static edument.perl6idea.parsing.Perl6TokenTypes.*;

class Perl6Block extends AbstractBlock implements BlockWithParent {
    private final static boolean DEBUG_MODE = false;
    private static final TokenSet TRAIT_CARRIERS = TokenSet.create(
        PACKAGE_DECLARATION, ROUTINE_DECLARATION, VARIABLE_DECLARATION,
        PARAMETER, REGEX_DECLARATION, ENUM, SUBSET, CONSTANT
    );
    private final List<BiFunction<Perl6Block, Perl6Block, Spacing>> myRules;
    private final CommonCodeStyleSettings myCommonSettings;
    private final Perl6CodeStyleSettings myCustomSettings;
    private BlockWithParent myParent;
    private Boolean isStatementContinuation;

    private TokenSet WHITESPACES = TokenSet.create(
            UNV_WHITE_SPACE, WHITE_SPACE,
            VERTICAL_WHITE_SPACE, UNSP_WHITE_SPACE
    );
    private ArrayList<Block> children;

    Perl6Block(ASTNode node,
               Wrap wrap,
               Alignment align,
               CommonCodeStyleSettings commonSettings, Perl6CodeStyleSettings customSettings,
               List<BiFunction<Perl6Block, Perl6Block, Spacing>> rules) {
        super(node, wrap, align);
        myRules = rules;
        myCommonSettings = commonSettings;
        myCustomSettings = customSettings;
        this.isStatementContinuation = false;
    }

    private Perl6Block(ASTNode node, Wrap wrap, Alignment align, Boolean isStatementContinuation,
                       CommonCodeStyleSettings commonSettings, Perl6CodeStyleSettings customSettings,
                       List<BiFunction<Perl6Block, Perl6Block, Spacing>> rules) {
        super(node, wrap, align);
        myRules = rules;
        myCommonSettings = commonSettings;
        myCustomSettings = customSettings;
        this.isStatementContinuation = isStatementContinuation;
    }

    @Override
    protected List<Block> buildChildren() {
        if (isLeaf())
            return EMPTY;
        final ArrayList<Block> children = new ArrayList<>();

        Pair<Function<ASTNode, Boolean>, Alignment> alignFunction = calculateAlignment(myNode);

        for (ASTNode child = getNode().getFirstChildNode(); child != null; child = child.getTreeNext()) {
            IElementType elementType = child.getElementType();
            if (WHITESPACES.contains(elementType))
                continue;
            Perl6Block childBlock;
            Boolean childIsStatementContinuation = null;
            if (isStatementContinuation != null && isStatementContinuation)
                childIsStatementContinuation = false;
            else if (nodeInStatementContinuation(child))
                childIsStatementContinuation = true;
            Alignment align = alignFunction == null ? null : alignFunction.first.apply(child) ? alignFunction.second : null;
            childBlock = new Perl6Block(child, null, align, childIsStatementContinuation, myCommonSettings, myCustomSettings, myRules);
            childBlock.setParent(this);
            children.add(childBlock);
        }
        return this.children = children;
    }

    @Override
    public Wrap getWrap() {
        // Basic sanity check: we don't wrap anything when interpolated in a str literal
        if (!(PsiTreeUtil.getParentOfType(myNode.getPsi(), Perl6File.class, Perl6StrLiteral.class, Perl6Heredoc.class) instanceof Perl6File))
            return null;

        if (myNode.getElementType() == PARAMETER && myCustomSettings.PARAMETER_WRAP) {
            return Wrap.createWrap(WrapType.NORMAL, true);
        }
        if (myNode.getElementType() == Perl6ElementTypes.TRAIT && myCustomSettings.TRAIT_WRAP) {
            return Wrap.createWrap(WrapType.NORMAL, true);
        }
        if (myNode.getPsi() instanceof Perl6MethodCall && myNode.getText().startsWith(".") && myCustomSettings.METHOD_CALL_WRAP)
            return Wrap.createWrap(WrapType.NORMAL, false);
        if (myNode.getTreeParent() != null && myNode.getTreeParent().getPsi() instanceof Perl6InfixApplication &&
            myNode.getElementType() != Perl6TokenTypes.NULL_TERM && myNode.getElementType() != Perl6ElementTypes.INFIX) {
            Perl6InfixApplication application = (Perl6InfixApplication)myNode.getTreeParent().getPsi();
            PsiElement parent = PsiTreeUtil.getParentOfType(application, Perl6SubCall.class, Perl6MethodCall.class,
                                                            Perl6ArrayComposer.class, Perl6VariableDecl.class);
            if (application.getOperator().equals(",") && (parent instanceof Perl6SubCall || parent instanceof Perl6MethodCall))
                return myCustomSettings.CALL_ARGUMENTS_WRAP ? Wrap.createWrap(WrapType.NORMAL, false) : null;
            if (application.getOperator().equals(",") && (parent instanceof Perl6SubCall || parent instanceof Perl6MethodCall) &&
                parent instanceof Perl6ArrayComposer || parent instanceof Perl6VariableDecl)
                return myCustomSettings.ARRAY_ELEMENTS_WRAP ? Wrap.createWrap(WrapType.NORMAL, false) : null;
            if (!application.getOperator().equals(".") && myCustomSettings.INFIX_APPLICATION_WRAP)
                return Wrap.createWrap(WrapType.NORMAL, false);
        }
        return null;
    }

    @Nullable
    private Pair<Function<ASTNode, Boolean>, Alignment> calculateAlignment(ASTNode node) {
        IElementType type = node.getElementType();
        if (type == SIGNATURE && myCustomSettings.PARAMETER_ALIGNMENT) {
            return Pair.create((child) -> child.getElementType() == PARAMETER, Alignment.createAlignment());
        } else if (type == ARRAY_COMPOSER && myCustomSettings.ARRAY_ELEMENTS_ALIGNMENT) {
            return Pair.create((child) -> child.getElementType() == ARRAY_COMPOSER_OPEN && child.getElementType() == ARRAY_COMPOSER_CLOSE, Alignment.createAlignment());
        } else if (type == Perl6OPPElementTypes.INFIX_APPLICATION && !(node.getPsi().getLastChild() instanceof Perl6MethodCall)) {
            if (!(node.getPsi() instanceof Perl6InfixApplication))
                return null;

            Perl6InfixApplication infixApp = (Perl6InfixApplication)node.getPsi();

            if (infixApp.getOperator().equals("??"))
                return null; // Do not align ?? !!, we'll just indent it

            if (infixApp.getOperator().equals(",")) {
                // If we have a comma separated list, there can be two cases: non-literal array init or signature
                // check those cases, otherwise just do as infix guides us
                PsiElement origin = PsiTreeUtil.getParentOfType(infixApp, Perl6VariableDecl.class, P6CodeBlockCall.class, Perl6ArrayComposer.class, Perl6Statement.class);
                // This case is handled by another option
                if (origin instanceof Perl6ArrayComposer)
                    return null;
                if (origin instanceof P6CodeBlockCall)
                    return myCustomSettings.CALL_ARGUMENTS_ALIGNMENT ? Pair.create(
                    (child) -> child.getElementType() != Perl6TokenTypes.INFIX && child.getElementType() != Perl6TokenTypes.NULL_TERM,
                    Alignment.createAlignment()) : null;
                if (origin instanceof Perl6VariableDecl)
                    return myCustomSettings.ARRAY_ELEMENTS_ALIGNMENT ? Pair.create(
                    (child) -> child.getElementType() != Perl6TokenTypes.INFIX && child.getElementType() != Perl6TokenTypes.NULL_TERM,
                    Alignment.createAlignment()) : null;
            }

            if (myCustomSettings.INFIX_APPLICATION_ALIGNMENT)
                return Pair.create(
                    (child) -> child.getElementType() != Perl6TokenTypes.INFIX && child.getElementType() != Perl6TokenTypes.NULL_TERM,
                    Alignment.createAlignment());
        } else if (TRAIT_CARRIERS.contains(type) && myCustomSettings.TRAIT_ALIGNMENT) {
            return Pair.create((child) -> child.getElementType() == Perl6ElementTypes.TRAIT, Alignment.createAlignment());
        }
        return null;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        if (!(child1 instanceof Perl6Block) || !(child2 instanceof Perl6Block))
            return null;

        Perl6Block left = (Perl6Block) child1;
        Perl6Block right = (Perl6Block) child2;

        int i = 0;
        for (BiFunction<Perl6Block, Perl6Block, Spacing> rule : myRules) {
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
        if (myNode.getTreeParent().getPsi() instanceof Perl6Heredoc)
            return Indent.getAbsoluteNoneIndent();
        if (myNode.getTreeParent().getPsi() instanceof Perl6Blockoid && myNode.getTreeNext() != null && myNode.getTreePrev() != null)
            return myNode.getTextLength() == 0 ? Indent.getNoneIndent() : Indent.getNormalIndent();
        if (myNode.getElementType() == SEMI_LIST && (myNode.getTreeParent().getElementType() == ARRAY_COMPOSER ||
                                                     myNode.getTreeParent().getElementType() == Perl6ElementTypes.CONTEXTUALIZER))
            return myNode.getTextLength() == 0 ? Indent.getNoneIndent() : Indent.getNormalIndent();
        if (isStatementContinuation != null && isStatementContinuation) {
            if (myNode.getElementType() == PARENTHESES_CLOSE && myNode.getTreeParent().getPsi() instanceof Perl6Signature) {
                return Indent.getSpaceIndent(1, true);
            }
            else if (myNode.getElementType() == PARENTHESES_CLOSE && myNode.getTreeParent().getPsi() instanceof Perl6SubCall) {
                Perl6SubCall subCall = (Perl6SubCall)myNode.getTreeParent().getPsi();
                if (subCall.getCallArguments().length != 0) {
                    PsiElement infix = subCall.getCallArguments()[0].getParent();
                    return Indent.getSpaceIndent(infix instanceof Perl6InfixApplication ? infix.getStartOffsetInParent() : 0, true);
                }
            }
            else if (myNode.getElementType() == ARRAY_COMPOSER_CLOSE) {
                if (myNode.getTreeParent().getPsi() instanceof Perl6ArrayComposer) {
                    PsiElement maybeSemilist = Perl6PsiUtil.skipSpaces(myNode.getTreePrev().getPsi(), false);
                    if (maybeSemilist instanceof Perl6SemiList) {
                        if (maybeSemilist.getText().trim().endsWith(","))
                            return Indent.getSpaceIndent(1, true);
                        else
                            return Indent.getNoneIndent();
                    }
                }
            }
            return Indent.getContinuationIndent();
        }
        return Indent.getNoneIndent();
    }

    private static boolean nodeInStatementContinuation(ASTNode startNode) {
        PsiElement startPsi = startNode.getPsi();
        PsiFile file = startPsi.getContainingFile();
        if (file == null || !file.isPhysical())
            return false;
        Document doc = file.getViewProvider().getDocument();
        if (doc == null)
            return false;
        // Check if the node spans over multiple lines, making us want a continuation
        if (doc.getLineNumber(startPsi.getTextOffset()) == doc.getLineNumber(startPsi.getParent().getTextOffset()))
            return false;

        // Comments can be swept under other statement nodes, but they are in no way a continuation
        if (startPsi instanceof PodPreComment || startPsi instanceof PodPostComment)
            return false;

        // Traits have continuation indent
        if (startPsi instanceof Perl6Trait)
            return true;
        // While infix is handled below, a special case of `my $foo = <continuation here> ...` is handled here
        if (startPsi.getParent() instanceof Perl6VariableDecl) {
            if (Perl6PsiUtil.skipSpaces(startPsi.getPrevSibling(), false) instanceof Perl6Infix)
                return true;
        }

        // Now checking parents
        if (startPsi.getParent() instanceof Perl6InfixApplication) {
            return !checkIfNonContinuatedInitializer(startPsi);
        } else if (startPsi.getParent() instanceof Perl6SubCall ||
                   startPsi.getParent() instanceof Perl6Signature ||
                   startPsi.getParent() instanceof Perl6MethodCall ||
                   startPsi.getParent() instanceof Perl6ArrayComposer) {
            return true;
        }
        return false;
    }

    private static boolean checkIfNonContinuatedInitializer(PsiElement startPsi) {
        if (((Perl6InfixApplication)startPsi.getParent()).getOperator().equals(",")) {
            Perl6Contextualizer contextualizer = PsiTreeUtil.getParentOfType(startPsi, Perl6Contextualizer.class);
            if (contextualizer != null && contextualizer.getText().startsWith("%"))
                return true;

            PsiElement hopefullyStatement = startPsi.getParent().getParent();
            if (hopefullyStatement instanceof Perl6Statement) {
                PsiElement statementHolder = hopefullyStatement.getParent();
                if (statementHolder instanceof Perl6StatementList) {
                    // Might be in a hash initializer. Check if the first child of the infix
                    // application is pair-like.
                    PsiElement hopefullyPairish = startPsi.getParent().getChildren()[0];
                    if (hopefullyPairish instanceof Perl6FatArrow || hopefullyPairish instanceof Perl6ColonPair) {
                        // It's fine, now just check we're in the appropriate kind of block.
                        PsiElement hopefullyBlockoid = statementHolder.getParent();
                        if (hopefullyBlockoid instanceof Perl6Blockoid &&
                            hopefullyBlockoid.getParent() instanceof Perl6BlockOrHash)
                            return true;
                    }
                }
                else if (statementHolder instanceof Perl6SemiList) {
                    // Might be an array literal.
                    if (statementHolder.getParent() instanceof Perl6ArrayComposer)
                        return false;
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
        }
        else if (elementType == BLOCKOID) {
            List<Block> subblocks;
            Block block = getSubBlocks().get(newIndex - 1);
            while (block != null) {
                subblocks = block.getSubBlocks();
                if (subblocks.size() != 0) {
                    block = subblocks.get(subblocks.size() - 1);
                    subblocks = block.getSubBlocks();
                }
                else {
                    if (block instanceof Perl6Block && ((Perl6Block)block).getNode().getElementType() == UNTERMINATED_STATEMENT)
                        return new ChildAttributes(Indent.getContinuationIndent(), obtainAlign((Perl6Block)block));
                    else
                        return new ChildAttributes(Indent.getNormalIndent(), null);
                }
            }
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }
        else if (elementType == SIGNATURE || elementType == INFIX_APPLICATION) {
            return new ChildAttributes(Indent.getContinuationIndent(), obtainAlign(this));
        }
        else if (elementType == METHOD_CALL) {
            return new ChildAttributes(Indent.getContinuationIndent(), null);
        }
        else if (myNode.getPsi() instanceof Perl6PsiDeclaration) {
            List<Block> blocks = getSubBlocks();
            Block block = blocks.get(newIndex - 1);
            if (block instanceof Perl6Block) {
                if (((Perl6Block)block).getNode().getPsi() instanceof Perl6Trait) {
                    return new ChildAttributes(Indent.getContinuationIndent(), block.getAlignment());
                }
            }
        }
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    private static Alignment obtainAlign(Perl6Block block) {
        IElementType elementType = block.getNode().getElementType();
        if (elementType == SIGNATURE) {
            return block.children.stream().map(child -> child.getAlignment()).filter(child -> child != null).findFirst().orElse(null);
        } else if (elementType == INFIX_APPLICATION) {
            Perl6InfixApplication application = (Perl6InfixApplication)block.getNode().getPsi();
            if (application.getOperator().equals("=") &&
                application.getOperands().length == 2 &&
                application.getOperands()[1] instanceof Perl6Infix)
                return null;
            else
                return block.children.stream().map(child -> child.getAlignment()).filter(child -> child != null).findFirst()
                    .orElse(null);
        } else if (elementType == UNTERMINATED_STATEMENT) {
            Perl6Block base = (Perl6Block)block.getParent();
            List<Block> blocks = base.getSubBlocks();
            for (int i = 0; i < blocks.size(); i++) {
                Block temp = blocks.get(i);
                if (temp == block) {
                    return obtainAlign((Perl6Block)blocks.get(i - 1));
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
