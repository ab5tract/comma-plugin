package edument.perl6idea.formatter;

import com.intellij.formatting.*;
import com.intellij.formatting.templateLanguages.BlockWithParent;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.parsing.Perl6OPPElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

import static edument.perl6idea.parsing.Perl6ElementTypes.*;
import static edument.perl6idea.parsing.Perl6ElementTypes.HEREDOC;
import static edument.perl6idea.parsing.Perl6ElementTypes.INFIX;
import static edument.perl6idea.parsing.Perl6TokenTypes.*;

class Perl6Block extends AbstractBlock implements BlockWithParent {
    private final static boolean DEBUG_MODE = false;
    private final List<BiFunction<Perl6Block, Perl6Block, Spacing>> myRules;
    private final CodeStyleSettings mySettings;
    private BlockWithParent myParent;
    private Boolean isStatementContinuation;

    private TokenSet WHITESPACES = TokenSet.create(
            UNV_WHITE_SPACE, WHITE_SPACE,
            VERTICAL_WHITE_SPACE, UNSP_WHITE_SPACE
    );

    Perl6Block(ASTNode node,
               Wrap wrap,
               Alignment align,
               CodeStyleSettings settings,
               List<BiFunction<Perl6Block, Perl6Block, Spacing>> rules) {
        super(node, wrap, align);
        myRules = rules;
        mySettings = settings;
        this.isStatementContinuation = false;
    }

    private Perl6Block(ASTNode node, Wrap wrap, Alignment align, Boolean isStatementContinuation,
                       CodeStyleSettings settings,
                       List<BiFunction<Perl6Block, Perl6Block, Spacing>> rules) {
        super(node, wrap, align);
        myRules = rules;
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
            Boolean childIsStatementContinuation = null;
            if (isStatementContinuation != null && isStatementContinuation)
                childIsStatementContinuation = false;
            else if (nodeInStatementContinuation(child))
                childIsStatementContinuation = true;
            childBlock = new Perl6Block(child, null, null, childIsStatementContinuation, mySettings, myRules);
            childBlock.setParent(this);
            children.add(childBlock);
        }
        return children;
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
        if (myNode.getTreeParent().getPsi() instanceof Perl6Blockoid && myNode.getTreeNext() != null && myNode.getTreePrev() != null)
            if (myNode.getFirstChildNode() != null && myNode.getFirstChildNode().getElementType() == LABEL)
                return mySettings.getLanguageIndentOptions(Perl6Language.INSTANCE).LABEL_INDENT_ABSOLUTE
                       ? Indent.getAbsoluteLabelIndent()
                       : Indent.getSpaceIndent(mySettings.getLanguageIndentOptions(Perl6Language.INSTANCE).LABEL_INDENT_SIZE, true);
            else
                return myNode.getTextLength() == 0 ? Indent.getNoneIndent() : Indent.getNormalIndent();
        if (myNode.getElementType() == SEMI_LIST && myNode.getTreeParent().getElementType() == ARRAY_COMPOSER)
            return myNode.getTextLength() == 0 ? Indent.getNoneIndent() : Indent.getNormalIndent();
        if (myNode.getElementType() == ARRAY_COMPOSER_CLOSE)
            return Indent.getNoneIndent();
        if (isStatementContinuation != null && isStatementContinuation)
            return Indent.getContinuationWithoutFirstIndent();
        return Indent.getNoneIndent();
    }

    private static boolean nodeInStatementContinuation(ASTNode startNode) {
        PsiElement startPsi = startNode.getPsi();
        PsiFile file = startPsi.getContainingFile();
        if (file == null)
            return false;
        Document doc = file.getViewProvider().getDocument();
        if (doc == null)
            return false;
        if (startPsi.getParent() instanceof Perl6InfixApplication
            && doc.getLineNumber(startPsi.getTextOffset()) != doc.getLineNumber(startPsi.getParent().getTextOffset())) {
            if (checkIfNonContinuatedInitializer(startPsi)) return false;
            return true;
        }
        return (startPsi.getParent() instanceof Perl6Signature || startPsi.getParent() instanceof Perl6MethodCall)
               && doc.getLineNumber(startPsi.getTextOffset()) != doc.getLineNumber(startPsi.getParent().getTextOffset());
    }

    private static boolean checkIfNonContinuatedInitializer(PsiElement startPsi) {
        if (((Perl6InfixApplication)startPsi.getParent()).getOperator().equals(",")) {
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
                        return true;
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
