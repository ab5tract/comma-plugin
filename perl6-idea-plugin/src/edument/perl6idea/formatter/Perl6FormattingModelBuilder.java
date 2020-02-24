package edument.perl6idea.formatter;

import com.intellij.formatting.BraceStyle;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.DocumentBasedFormattingModel;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;

import static edument.perl6idea.parsing.Perl6TokenTypes.*;

public class Perl6FormattingModelBuilder implements FormattingModelBuilder {
    private static final TokenSet STATEMENTS = TokenSet.create(Perl6ElementTypes.STATEMENT, Perl6ElementTypes.COMMENT,
                                                               Perl6ElementTypes.HEREDOC);
    private static final TokenSet OPENERS = TokenSet.create(ARRAY_COMPOSER_OPEN, ARRAY_INDEX_BRACKET_OPEN,
                                                            HASH_INDEX_BRACKET_OPEN, PARENTHESES_OPEN, SIGNATURE_BRACKET_OPEN,
                                                            REGEX_GROUP_BRACKET_OPEN, REGEX_CAPTURE_PARENTHESES_OPEN);
    private static final TokenSet CLOSERS = TokenSet.create(ARRAY_COMPOSER_CLOSE, ARRAY_INDEX_BRACKET_CLOSE,
                                                            HASH_INDEX_BRACKET_CLOSE, PARENTHESES_CLOSE, SIGNATURE_BRACKET_CLOSE,
                                                            REGEX_GROUP_BRACKET_CLOSE, REGEX_CAPTURE_PARENTHESES_CLOSE);
    public static final Spacing CONSTANT_EMPTY_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
    public Spacing EMPTY_SPACING;
    public Spacing SINGLE_SPACE_SPACING;
    public Spacing SINGLE_LINE_BREAK;
    public Spacing DOUBLE_LINE_BREAK;

    @NotNull
    @Override
    public FormattingModel createModel(@NotNull PsiElement element, @NotNull CodeStyleSettings settings) {
        final PsiFile psiFile = element.getContainingFile();
        List<BiFunction<Perl6Block, Perl6Block, Spacing>> rules = new ArrayList<>();
        initRules(settings, rules);
        final Perl6Block block = new Perl6Block(psiFile.getNode(), null, null, settings, rules);
        return new DocumentBasedFormattingModel(block, element.getProject(), settings, psiFile.getFileType(), psiFile);
    }

    private void initRules(CodeStyleSettings settings, List<BiFunction<Perl6Block, Perl6Block, Spacing>> rules) {
        CommonCodeStyleSettings commonSettings = settings.getCommonSettings(Perl6Language.INSTANCE);
        Perl6CodeStyleSettings customSettings = settings.getCustomSettings(Perl6CodeStyleSettings.class);

        // Prepare fast constants for common cases
        EMPTY_SPACING = Spacing.createSpacing(0, 0, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_LINE_BREAKS ? 3 : 1);
        SINGLE_SPACE_SPACING = Spacing.createSpacing(1, 1, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_LINE_BREAKS ? 3 : 1);
        SINGLE_LINE_BREAK = Spacing.createSpacing(0, 0, 1, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_LINE_BREAKS ? 3 : 1);
        DOUBLE_LINE_BREAK = Spacing.createSpacing(0, 0, 2, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_LINE_BREAKS ? 3 : 1);

        // Init actual rule sets
        initLineBreakRules(commonSettings, customSettings, rules);
        initSpacingRules(commonSettings, customSettings, rules);
        // XXX No wrapping rules for now
    }

    private void initSpacingRules(CommonCodeStyleSettings commonSettings,
                                  Perl6CodeStyleSettings customSettings,
                                  List<BiFunction<Perl6Block, Perl6Block, Spacing>> rules) {
        // Nothing between statement and its ;
        rules.add((left, right) -> right.getNode().getElementType() == Perl6TokenTypes.STATEMENT_TERMINATOR
                                   ? CONSTANT_EMPTY_SPACING : null);
        // Nothing between statement and its absence of ;
        rules.add((left, right) -> right.getNode().getElementType() == Perl6ElementTypes.UNTERMINATED_STATEMENT
                                   ? CONSTANT_EMPTY_SPACING
                                   : null);

        // Nothing inside of different types of braces, parens etc (block ones are handled in line break rules set
        rules.add((left, right) -> OPENERS.contains(left.getNode().getElementType()) ? EMPTY_SPACING : null);
        rules.add((left, right) -> CLOSERS.contains(right.getNode().getElementType()) ? EMPTY_SPACING : null);

        // Comma operator, a space after one if it's not a dangling one
        rules.add((left, right) -> left.getNode().getElementType() == Perl6ElementTypes.INFIX && left.getNode().getText().equals(",")
                                   ? isDanglingComma(left.getNode().getTreeNext()) ? EMPTY_SPACING : SINGLE_SPACE_SPACING : null);
        // Comma operator, nothing before one
        rules.add((left, right) -> right.getNode().getElementType() == Perl6ElementTypes.INFIX &&
                                   right.getNode().getText().equals(",") ? EMPTY_SPACING : null);

        // Fatarrow
        rules.add((left, right) -> left.getNode().getElementType() == Perl6ElementTypes.FATARROW ||
                                   right.getNode().getElementType() == Perl6ElementTypes.FATARROW ? SINGLE_SPACE_SPACING : null);

        // Prefix operators
        rules.add((left, right) -> left.getNode().getElementType() == Perl6ElementTypes.PREFIX ? EMPTY_SPACING : null);

        // Various infix operator related rules
        rules.add((left, right) -> {
            boolean isInfixOp =
                left.getNode().getElementType() == Perl6TokenTypes.INFIX ||
                right.getNode().getElementType() == Perl6TokenTypes.INFIX ||
                left.getNode().getElementType() == Perl6ElementTypes.INFIX ||
                right.getNode().getElementType() == Perl6ElementTypes.INFIX;
            if (!isInfixOp) return null;

            if (!(PsiTreeUtil.getParentOfType(left.getNode().getPsi(), Perl6QuoteRegex.class, Perl6StatementList.class) instanceof Perl6StatementList))
                return null;

            // Whatever-related expressions
            Perl6InfixApplication app = PsiTreeUtil.getParentOfType(left.getNode().getPsi(), Perl6InfixApplication.class);
            if (app != null && PsiTreeUtil.getChildOfType(app, Perl6Whatever.class) != null)
                return EMPTY_SPACING;

            // If it is a dot in a method call, no spacing
            boolean isDotOp = left.getNode().getText().equals(".") || right.getNode().getText().equals(".");
            return isDotOp ? EMPTY_SPACING : SINGLE_SPACE_SPACING;
        });

        // Lambda rules
        rules.add((left, right) -> left.getNode().getElementType() == Perl6TokenTypes.LAMBDA ? SINGLE_SPACE_SPACING : null);

        /** Regex related ones */
        // Regex infix
        rules.add((left, right) -> left.getNode().getElementType() == Perl6TokenTypes.REGEX_INFIX ||
                                   right.getNode().getElementType() == Perl6TokenTypes.REGEX_INFIX
                                   ? SINGLE_SPACE_SPACING : null);

        // Regex separator
        rules.add((left, right) -> {
            boolean isLeft = left.getNode().getElementType() == Perl6ElementTypes.REGEX_QUANTIFIER ||
                             left.getNode().getElementType() == Perl6TokenTypes.REGEX_QUANTIFIER;
            boolean isRight = right.getNode().getElementType() == Perl6ElementTypes.REGEX_QUANTIFIER ||
                              right.getNode().getElementType() == Perl6TokenTypes.REGEX_QUANTIFIER;
            if (!isLeft && !isRight) return null;
            String text = isLeft ? left.getNode().getText() : right.getNode().getText();
            return text.startsWith("**") || text.startsWith("%") || text.startsWith("%%") ? SINGLE_SPACE_SPACING : null;
        });

        // Regex quantifier
        rules.add((left, right) -> right.getNode().getElementType() == Perl6ElementTypes.REGEX_QUANTIFIER ? EMPTY_SPACING : null);
    }

    private static boolean isDanglingComma(ASTNode node) {
        return node != null && node.getElementType() == Perl6ElementTypes.NULL_TERM;
    }

    private void initLineBreakRules(CommonCodeStyleSettings commonSettings,
                                    Perl6CodeStyleSettings customSettings,
                                    List<BiFunction<Perl6Block, Perl6Block, Spacing>> rules) {
        /** Brace style related rules */
        // Brace style for package
        rules.add((left, right) -> right.getNode().getElementType() == Perl6ElementTypes.BLOCKOID &&
                                   right.getNode().getTreeParent().getElementType() == Perl6ElementTypes.PACKAGE_DECLARATION
                                   ? (BraceStyle.fromInt(customSettings.PACKAGE_DECL_BRACE_STYLE) == BraceStyle.EndOfLine
                                      ? SINGLE_SPACE_SPACING : SINGLE_LINE_BREAK)
                                   : null);

        // Brace style for routine
        rules.add((left, right) -> right.getNode().getElementType() == Perl6ElementTypes.BLOCKOID &&
                                   right.getNode().getTreeParent().getElementType() == Perl6ElementTypes.ROUTINE_DECLARATION
                                   ? (BraceStyle.fromInt(customSettings.ROUTINE_DECL_BRACE_STYLE) == BraceStyle.EndOfLine
                                      ? SINGLE_SPACE_SPACING : SINGLE_LINE_BREAK)
                                   : null);

        // Brace style for regex
        rules.add((left, right) -> right.getNode().getElementType() == Perl6ElementTypes.BLOCKOID &&
                                   right.getNode().getTreeParent().getElementType() == Perl6ElementTypes.REGEX_DECLARATION
                                   ? (BraceStyle.fromInt(customSettings.REGEX_DECL_BRACE_STYLE) == BraceStyle.EndOfLine
                                      ? SINGLE_SPACE_SPACING : SINGLE_LINE_BREAK)
                                   : null);

        // Brace style for phasers and everything else
        rules.add((left, right) -> right.getNode().getElementType() == Perl6ElementTypes.BLOCK
               ? (right.getNode().getTreeParent().getElementType() == Perl6ElementTypes.PHASER
                  ? (BraceStyle.fromInt(customSettings.PHASER_BRACE_STYLE) == BraceStyle.EndOfLine
                     ? SINGLE_SPACE_SPACING : SINGLE_LINE_BREAK)
                  : (BraceStyle.fromInt(customSettings.OTHER_BRACE_STYLE) == BraceStyle.EndOfLine
                     ? SINGLE_SPACE_SPACING : SINGLE_LINE_BREAK))
               : null);

        rules.add((left, right) -> {
            boolean isOpener = left.getNode().getElementType() == BLOCK_CURLY_BRACKET_OPEN;
            boolean isCloser = right.getNode().getElementType() == Perl6TokenTypes.BLOCK_CURLY_BRACKET_CLOSE;
            if (!isOpener && !isCloser) return null;

            ASTNode blockoid = left.getNode().getTreeParent();
            if (blockoid.getElementType() == Perl6ElementTypes.BLOCKOID) {
                PsiElement source = PsiTreeUtil.getParentOfType(blockoid.getPsi(), Perl6PackageDecl.class,
                                                                Perl6RoutineDecl.class, Perl6RegexDecl.class,
                                                                Perl6PointyBlock.class, Perl6Statement.class,
                                                                Perl6BlockOrHash.class, edument.perl6idea.psi.Perl6Block.class);
                Collection<PsiElement> inner = PsiTreeUtil.findChildrenOfAnyType(blockoid.getPsi(), Perl6Statement.class, Perl6Regex.class);
                int statementCount;
                if (inner.size() == 1 && inner.iterator().next() instanceof Perl6Regex) {
                    statementCount = inner.iterator().next().getChildren().length;
                } else {
                    statementCount = inner.size();
                }
                if (statementCount == 0) {
                    if (source instanceof Perl6PackageDecl && customSettings.PACKAGE_DECLARATION_IN_ONE_LINE ||
                        source instanceof Perl6RoutineDecl && customSettings.ROUTINES_DECLARATION_IN_ONE_LINE ||
                        source instanceof Perl6RegexDecl && customSettings.REGEX_DECLARATION_IN_ONE_LINE ||
                        source instanceof Perl6PointyBlock && customSettings.POINTY_BLOCK_IN_ONE_LINE ||
                        (source instanceof Perl6Statement || source instanceof Perl6BlockOrHash || source instanceof edument.perl6idea.psi.Perl6Block) && commonSettings.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE) {
                        return Spacing.createSpacing(0, 0, 0, true, 1);
                    }
                } else if (statementCount == 1) {
                    if ((source instanceof Perl6Statement || source instanceof Perl6BlockOrHash || source instanceof edument.perl6idea.psi.Perl6Block) && commonSettings.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE ||
                        source instanceof Perl6PointyBlock && customSettings.POINTY_BLOCK_IN_ONE_LINE)
                        return Spacing.createSpacing(1, 1, 0, true, 1);
                }
                if (statementCount < 2)
                    return Spacing.createSpacing(0, 0, 1, false, 0);
            }
            return Spacing.createSpacing(0, 0, 1, true, 1);
        });

        rules.add((left, right) -> STATEMENTS.contains(left.getNode().getElementType())
                                   || STATEMENTS.contains(right.getNode().getElementType())
                                   ? SINGLE_LINE_BREAK : null);
    }
}
