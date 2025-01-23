package org.raku.comma.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.DocumentBasedFormattingModel;
import com.intellij.psi.formatter.PsiBasedFormattingModel;
import com.intellij.psi.impl.source.codeStyle.PsiBasedFormatterModelWithShiftIndentInside;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.RakuLanguage;
import org.raku.comma.parsing.RakuElementTypes;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;

import static org.raku.comma.parsing.RakuElementTypes.SEMI_LIST;
import static org.raku.comma.parsing.RakuTokenTypes.*;

@InternalIgnoreDependencyViolation
public class RakuFormattingModelBuilder implements FormattingModelBuilder {
    private static final TokenSet STATEMENTS = TokenSet.create(RakuElementTypes.STATEMENT, RakuElementTypes.COMMENT,
                                                               RakuElementTypes.HEREDOC);
    private static final TokenSet OPENERS = TokenSet.create(ARRAY_COMPOSER_OPEN, ARRAY_INDEX_BRACKET_OPEN,
                                                            HASH_INDEX_BRACKET_OPEN, SIGNATURE_BRACKET_OPEN);
    private static final TokenSet CLOSERS = TokenSet.create(ARRAY_COMPOSER_CLOSE, ARRAY_INDEX_BRACKET_CLOSE,
                                                            HASH_INDEX_BRACKET_CLOSE, SIGNATURE_BRACKET_CLOSE);
    public Spacing CONSTANT_EMPTY_SPACING;
    public Spacing EMPTY_SPACING;
    public Spacing SINGLE_SPACE_SPACING;
    public Spacing SINGLE_LINE_BREAK;
    public Spacing DOUBLE_LINE_BREAK;

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        final PsiFile psiFile = formattingContext.getContainingFile();
        List<BiFunction<RakuBlock, RakuBlock, Spacing>> rules = new ArrayList<>();
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();
        CommonCodeStyleSettings commonSettings = settings.getCommonSettings(RakuLanguage.INSTANCE);
        RakuCodeStyleSettings customSettings = settings.getCustomSettings(RakuCodeStyleSettings.class);
        initRules(rules, commonSettings, customSettings);
        final RakuBlock block = new RakuBlock(psiFile.getNode(), null, null, commonSettings, customSettings, rules);
//        return new DocumentBasedFormattingModel(block, formattingContext.getProject(), settings, psiFile.getFileType(), psiFile);
        return FormattingModelProvider.createFormattingModelForPsiFile(psiFile, block, settings);
    }

    private void initRules(List<BiFunction<RakuBlock, RakuBlock, Spacing>> rules,
                           CommonCodeStyleSettings commonSettings, RakuCodeStyleSettings customSettings) {

        // Prepare fast constants for common cases
        EMPTY_SPACING = Spacing.createSpacing(0, 0, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_LINE_BREAKS ? 3 : 1);
        SINGLE_SPACE_SPACING = Spacing.createSpacing(1, 1, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_LINE_BREAKS ? 3 : 1);
        SINGLE_LINE_BREAK = Spacing.createSpacing(0, 0, 1, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_LINE_BREAKS ? 3 : 1);
        DOUBLE_LINE_BREAK = Spacing.createSpacing(0, 0, 2, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_LINE_BREAKS ? 3 : 1);
        CONSTANT_EMPTY_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);

        // Init spacing rule sets
        initLineBreakRules(commonSettings, customSettings, rules);
        initSpacingRules(commonSettings, customSettings, rules);
    }

    private void initSpacingRules(CommonCodeStyleSettings commonSettings,
                                  RakuCodeStyleSettings customSettings,
                                  List<BiFunction<RakuBlock, RakuBlock, Spacing>> rules) {
        // Nothing between statement and its ;
        rules.add((left, right) -> right.getNode().getElementType() == STATEMENT_TERMINATOR
                                   ? CONSTANT_EMPTY_SPACING : null);
        // Nothing between statement and its absence of ;
        rules.add((left, right) -> right.getNode().getElementType() == RakuElementTypes.UNTERMINATED_STATEMENT
                                   ? CONSTANT_EMPTY_SPACING
                                   : null);

        // In-Parentheses
        rules.add((left, right) -> {
            if (left.getNode().getElementType() == PARENTHESES_OPEN || right.getNode().getElementType() == PARENTHESES_CLOSE) {
                PsiElement parent = left.getNode().getTreeParent().getPsi();
                if (parent instanceof RakuSubCall || parent instanceof RakuMethodCall)
                    return customSettings.CALL_PARENS_SPACING ? SINGLE_SPACE_SPACING : EMPTY_SPACING;
                return customSettings.GROUPING_PARENS_SPACING ? SINGLE_SPACE_SPACING : EMPTY_SPACING;
            }
            return null;
        });

        // In array braces
        rules.add((left, right) -> {
            if (left.getNode().getElementType() == ARRAY_COMPOSER_OPEN || right.getNode().getElementType() == ARRAY_COMPOSER_CLOSE)
                return customSettings.ARRAY_LITERAL_PARENS_SPACING ? SINGLE_SPACE_SPACING : EMPTY_SPACING;
            return null;
        });

        // Regex group
        rules.add((left, right) -> {
            if (left.getNode().getElementType() == REGEX_GROUP_BRACKET_OPEN || right.getNode().getElementType() == REGEX_GROUP_BRACKET_CLOSE)
                return customSettings.REGEX_GROUP_PARENS_SPACING ? SINGLE_SPACE_SPACING : EMPTY_SPACING;
            return null;
        });
        // Regex capture
        rules.add((left, right) -> {
            if (left.getNode().getElementType() == REGEX_CAPTURE_PARENTHESES_OPEN || right.getNode().getElementType() == REGEX_CAPTURE_PARENTHESES_CLOSE)
                return customSettings.REGEX_POSITIONAL_PARENS_SPACING ? SINGLE_SPACE_SPACING : EMPTY_SPACING;
            return null;
        });

        // Nothing inside different types of braces, parens etc. (block ones are handled in line break rules set
        rules.add((left, right) -> OPENERS.contains(left.getNode().getElementType()) ? EMPTY_SPACING : null);
        rules.add((left, right) -> CLOSERS.contains(right.getNode().getElementType()) ? EMPTY_SPACING : null);

        // Comma operator, a space after one if it's not a dangling one
        rules.add((left, right) -> left.getNode().getElementType() == RakuElementTypes.INFIX && left.getNode().getText().equals(",") &&
                                   right.getNode().getElementType() != METAOP
                                   ? ((isDanglingComma(left.getNode().getTreeNext())
                                       ? EMPTY_SPACING
                                       : customSettings.AFTER_COMMA ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING)) : null);
        // Comma operator, nothing before one
        rules.add((left, right) -> right.getNode().getElementType() == RakuElementTypes.INFIX &&
                                   right.getNode().getText().equals(",")
                                   ? (customSettings.BEFORE_COMMA ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING) : null);

        // Fatarrow
        rules.add((left, right) -> {
            boolean after = left.getNode().getElementType() == INFIX && left.getNode().getText().equals("=>");
            boolean before = right.getNode().getElementType() == INFIX && right.getNode().getText().equals("=>");
            return (after && customSettings.AFTER_FATARROW) ||
                   (before && customSettings.BEFORE_FATARROW)
                   ? SINGLE_SPACE_SPACING
                   : (before || after ? CONSTANT_EMPTY_SPACING : null);
        });

        // No excessive whitespace around trait
        rules.add((left, right) -> right.getNode().getElementType() == RakuElementTypes.TRAIT ||
                                   left.getNode().getElementType() == RakuElementTypes.TRAIT
                                   ? SINGLE_SPACE_SPACING : null);

        // Prefix operators
        rules.add((left, right) -> left.getNode().getElementType() == RakuElementTypes.PREFIX && !left.getNode().getText().trim().matches("^[a-zA-Z]*$")
                                   ? (customSettings.AFTER_PREFIX_OPS ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING) : null);

        // Various infix operator related rules
        rules.add((left, right) -> {
            boolean isLeftInfix =
                left.getNode().getElementType() == INFIX ||
                left.getNode().getElementType() == RakuElementTypes.INFIX;
            boolean isRightInfix =
                right.getNode().getElementType() == INFIX ||
                right.getNode().getElementType() == RakuElementTypes.INFIX;
            if (!isLeftInfix && !isRightInfix) return null;

            // Don't play with regexes, they have separate rules
            if (!(PsiTreeUtil.getParentOfType(left.getNode().getPsi(), RakuQuoteRegex.class, RakuRegexDecl.class,
                                              RakuStatementList.class) instanceof RakuStatementList)) {
                return null;
            }

            // Keep metaop like `+=` close
            if (left.getNode().getElementType() == RakuElementTypes.INFIX && right.getNode().getElementType() == METAOP ||
                right.getNode().getElementType() == RakuElementTypes.INFIX && left.getNode().getElementType() == METAOP) {
                return CONSTANT_EMPTY_SPACING;
            }

            // Binding ops are here
            String leftText = left.getNode().getText();
            String rightText = right.getNode().getText();
            if (leftText.equals("=") || leftText.equals(":=")) {
                return customSettings.AFTER_ASSIGNMENT ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING;
            }
            else if (rightText.equals("=") || rightText.equals(":=")) {
                return customSettings.BEFORE_ASSIGNMENT ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING;
            }

            // Whatever-related expressions
            if (left.getNode().getElementType() == RakuElementTypes.WHATEVER)
                return customSettings.AFTER_WHATEVER_STAR ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING;
            if (right.getNode().getElementType() == RakuElementTypes.WHATEVER)
                return customSettings.BEFORE_WHATEVER_STAR ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING;

            // If it is a dot in a method call, no spacing
            if (leftText.equals(".") || rightText.equals(".")) {
                return EMPTY_SPACING;
            }
            if (isLeftInfix) {
                return customSettings.AFTER_INFIX ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING;
            }
            else {
                return customSettings.BEFORE_INFIX ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING;
            }
        });

        // Lambda rules
        rules.add((left, right) -> left.getNode().getElementType() == LAMBDA
                                   ? (customSettings.AFTER_LAMBDA ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING) : null);

        /** Regex related ones */
        // Regex infix
        rules.add((left, right) -> {
            boolean leftInfix = left.getNode().getElementType() == REGEX_INFIX;
            boolean rightInfix = right.getNode().getElementType() == REGEX_INFIX;
            if (!leftInfix && !rightInfix) return null;

            // FIXME we handle only || here, because e.g. `&& a` turned into `&&a` becomes a call
            if (leftInfix && left.getNode().getText().equals("||")) {
                return customSettings.AFTER_REGEX_INFIX_SPACING ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING;
            }
            else if (rightInfix && right.getNode().getText().equals("||")) {
                return customSettings.BEFORE_REGEX_INFIX_SPACING ? SINGLE_SPACE_SPACING : CONSTANT_EMPTY_SPACING;
            }
            return SINGLE_SPACE_SPACING;
        });

        // Regex separator
        rules.add((left, right) -> {
            boolean isLeft = left.getNode().getElementType() == RakuElementTypes.REGEX_QUANTIFIER ||
                             left.getNode().getElementType() == REGEX_QUANTIFIER;
            boolean isRight = right.getNode().getElementType() == RakuElementTypes.REGEX_QUANTIFIER ||
                              right.getNode().getElementType() == REGEX_QUANTIFIER;
            if (!isLeft && !isRight) return null;
            String text = isLeft ? left.getNode().getText() : right.getNode().getText();
            if (!(text.startsWith("**") || text.startsWith("%") || text.startsWith("%%"))) return null;
            if (isLeft) {
                return customSettings.AFTER_REGEX_SEPARATOR_SPACING
                            ? SINGLE_SPACE_SPACING
                            : CONSTANT_EMPTY_SPACING;
            }
            else {
                return customSettings.BEFORE_REGEX_SEPARATOR_SPACING
                            ? SINGLE_SPACE_SPACING
                            : CONSTANT_EMPTY_SPACING;
            }
        });

        // Regex quantifier
        rules.add((left, right) -> {
            if (left.getNode().getElementType() == RakuElementTypes.REGEX_QUANTIFIER) {
                return customSettings.AFTER_REGEX_QUANTIFIER_SPACING
                            ? SINGLE_SPACE_SPACING
                            : CONSTANT_EMPTY_SPACING;
            }
            if (right.getNode().getElementType() == RakuElementTypes.REGEX_QUANTIFIER) {
                return customSettings.BEFORE_REGEX_QUANTIFIER_SPACING
                            ? SINGLE_SPACE_SPACING
                            : CONSTANT_EMPTY_SPACING;
            }
            return null;
        });
    }

    private static boolean isDanglingComma(ASTNode node) {
        return node != null && node.getElementType() == RakuElementTypes.NULL_TERM;
    }

    //TODO: Migrate every rule to a generator sub
    private void initLineBreakRules(CommonCodeStyleSettings commonSettings,
                                    RakuCodeStyleSettings customSettings,
                                    List<BiFunction<RakuBlock, RakuBlock, Spacing>> rules) {
        /** Brace style related rules */
        // Brace style for package
        rules.add((left, right) -> right.getNode().getElementType() == RakuElementTypes.BLOCKOID &&
                                   right.getNode().getTreeParent().getElementType() == RakuElementTypes.PACKAGE_DECLARATION
                                   ? (customSettings.PACKAGE_DECL_BRACE_STYLE == 1
                                        ? SINGLE_SPACE_SPACING
                                        : SINGLE_LINE_BREAK)
                                   : null);

        // Brace style for routine
        rules.add((left, right) -> right.getNode().getElementType() == RakuElementTypes.BLOCKOID &&
                                   right.getNode().getTreeParent().getElementType() == RakuElementTypes.ROUTINE_DECLARATION
                                   ? (customSettings.ROUTINE_DECL_BRACE_STYLE == 1
                                        ? SINGLE_SPACE_SPACING
                                        : SINGLE_LINE_BREAK)
                                   : null);

        // Brace style for regex
        rules.add((left, right) -> right.getNode().getElementType() == RakuElementTypes.BLOCKOID &&
                                   right.getNode().getTreeParent().getElementType() == RakuElementTypes.REGEX_DECLARATION
                                   ? (customSettings.REGEX_DECL_BRACE_STYLE == 1
                                        ? SINGLE_SPACE_SPACING
                                        : SINGLE_LINE_BREAK)
                                   : null);

        // Brace style for phasers and everything else
        rules.add((left, right) -> {
            if (!(PsiTreeUtil.getParentOfType(right.getNode().getPsi(), RakuQuoteRegex.class, RakuRegexDecl.class,
                                              RakuStatement.class, RakuStrLiteral.class, RakuHeredoc.class) instanceof RakuStatement))
                return null;
            return right.getNode().getElementType() == RakuElementTypes.BLOCK
                   ? (right.getNode().getTreeParent().getElementType() == RakuElementTypes.PHASER
                      ? (customSettings.PHASER_BRACE_STYLE == 1
                            ? SINGLE_SPACE_SPACING
                            : SINGLE_LINE_BREAK)
                      : (customSettings.OTHER_BRACE_STYLE == 1
                            ? SINGLE_SPACE_SPACING
                            : SINGLE_LINE_BREAK))
                   : null;
        });

        rules.add((left, right) -> {
            boolean isOpener = left.getNode().getElementType() == BLOCK_CURLY_BRACKET_OPEN;
            boolean isCloser = right.getNode().getElementType() == BLOCK_CURLY_BRACKET_CLOSE;
            if (!isOpener && !isCloser) return null;

            // Do not play with {} in regexes
            if (left.getNode().getElementType() == REGEX_LOOKAROUND)
                return null;

            if (left.getNode().getElementType() == ONLY_STAR || right.getNode().getElementType() == ONLY_STAR)
                return Spacing.createSpacing(0, 0, 0, false, 0);

            ASTNode blockoid = left.getNode().getTreeParent();
            if (blockoid.getElementType() == RakuElementTypes.BLOCKOID) {
                PsiElement source = PsiTreeUtil.getParentOfType(blockoid.getPsi(), RakuPackageDecl.class,
                                                                RakuRoutineDecl.class, RakuRegexDecl.class,
                                                                RakuPointyBlock.class, RakuStatement.class,
                                                                RakuBlockOrHash.class, org.raku.comma.psi.RakuBlock.class);
                PsiElement inner = PsiTreeUtil.getChildOfAnyType(blockoid.getPsi(), RakuStatementList.class, RakuRegex.class);
                PsiElement[] children = inner == null ? PsiElement.EMPTY_ARRAY : inner.getChildren();
                int statementCount = children.length;
                if (statementCount == 0) {
                    //TODO: Break this down to conceptually absorbable pieces
                    if (source instanceof RakuPackageDecl && customSettings.PACKAGE_DECLARATION_IN_ONE_LINE ||
                        source instanceof RakuRoutineDecl && customSettings.ROUTINES_DECLARATION_IN_ONE_LINE ||
                        source instanceof RakuRegexDecl && customSettings.REGEX_DECLARATION_IN_ONE_LINE ||
                        source instanceof RakuPointyBlock && customSettings.POINTY_BLOCK_IN_ONE_LINE ||
                        (source instanceof RakuStatement || source instanceof RakuBlockOrHash || source instanceof org.raku.comma.psi.RakuBlock)
                        && commonSettings.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE)
                    {
                        return Spacing.createSpacing(0, 0, 0, true, 1);
                    }
                }
                else if (statementCount == 1) {
                    if ((source instanceof RakuStatement || source instanceof RakuBlockOrHash || source instanceof org.raku.comma.psi.RakuBlock)
                        && commonSettings.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE ||
                        source instanceof RakuPointyBlock && customSettings.POINTY_BLOCK_IN_ONE_LINE)
                    {
                        return Spacing.createSpacing(1, 1, 0, true, 1);
                    }
                    if (source instanceof RakuRegexDecl && customSettings.REGEX_DECLARATION_IN_ONE_LINE) {
                        int spaces = 1;
                        if (left.getNode().getElementType() == RakuElementTypes.REGEX && left.getNode().getText().endsWith("  "))
                            spaces = 0;
                        return Spacing.createSpacing(spaces, spaces, 0, true, 1);
                    }
                    if (source instanceof RakuRoutineDecl && PsiTreeUtil.findChildOfType(source, RakuStubCode.class) != null) {
                        return Spacing.createSpacing(0, 0, 0, false, 0);
                    }
                }
                if (statementCount < 2) {
                    return Spacing.createSpacing(0, 0, 1, false, 0);
                }
            }
            int lineFeeds = 1;
            boolean lhsElementIsRegex = left.getNode().getElementType() == RakuElementTypes.REGEX;
            boolean lhsElementHasNewline = left.getNode().getText().replaceAll(" ", "").endsWith("\n");
            if (lhsElementIsRegex && lhsElementHasNewline) {
                lineFeeds = 0;
            }
            return Spacing.createSpacing(0, 0, lineFeeds, true, 1);
        });

        rules.add(semiListRule());
    }

    private BiFunction<RakuBlock, RakuBlock, Spacing> semiListRule() {
        return (left, right) ->
                (STATEMENTS.contains(left.getNode().getElementType()) || STATEMENTS.contains(right.getNode().getElementType()))
                && !(left.getNode().getTreeParent().getElementType() == SEMI_LIST
                || right.getNode().getTreeParent().getElementType() == SEMI_LIST)
                    ? SINGLE_LINE_BREAK
                    : null;
    }
}
