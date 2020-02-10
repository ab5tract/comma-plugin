package edument.perl6idea.formatter;

import com.intellij.codeInsight.ExpectedTypeUtil;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import static edument.perl6idea.parsing.Perl6TokenTypes.*;

public class Perl6FormattingModelBuilder implements FormattingModelBuilder {
    private static final TokenSet STATEMENTS = TokenSet.create(Perl6ElementTypes.STATEMENT, Perl6ElementTypes.COMMENT,
                                                               Perl6ElementTypes.HEREDOC);
    private static final TokenSet OPENERS = TokenSet.create(ARRAY_COMPOSER_OPEN, ARRAY_INDEX_BRACKET_OPEN, BLOCK_CURLY_BRACKET_OPEN,
                                                            HASH_INDEX_BRACKET_OPEN, PARENTHESES_OPEN, SIGNATURE_BRACKET_OPEN);
    private static final TokenSet CLOSERS = TokenSet.create(ARRAY_COMPOSER_CLOSE, ARRAY_INDEX_BRACKET_CLOSE, BLOCK_CURLY_BRACKET_CLOSE,
                                                            HASH_INDEX_BRACKET_CLOSE, PARENTHESES_CLOSE, SIGNATURE_BRACKET_CLOSE);
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
        final Perl6Block block = new Perl6Block(psiFile.getNode(), null,null, settings, rules);
        return new DocumentBasedFormattingModel(block, element.getProject(), settings, psiFile.getFileType(), psiFile);
    }

    private void initRules(CodeStyleSettings settings,List<BiFunction<Perl6Block, Perl6Block, Spacing>> rules) {
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
        rules.add((left, right) -> right.getNode().getElementType() == Perl6TokenTypes.STATEMENT_TERMINATOR ? EMPTY_SPACING : null);
        rules.add((left, right) -> right.getNode().getElementType() == Perl6ElementTypes.UNTERMINATED_STATEMENT ? EMPTY_SPACING : null);
        rules.add((left, right) -> OPENERS.contains(left.getNode().getElementType()) ? EMPTY_SPACING : null);
        rules.add((left, right) -> CLOSERS.contains(right.getNode().getElementType()) ? EMPTY_SPACING : null);
        rules.add((left, right) -> left.getNode().getElementType() == Perl6ElementTypes.INFIX && left.getNode().getText().equals(",") ? SINGLE_SPACE_SPACING : null);
        rules.add((left, right) -> right.getNode().getElementType() == Perl6ElementTypes.INFIX && right.getNode().getText().equals(",") ? EMPTY_SPACING : null);
        rules.add((left, right) -> left.getNode().getElementType() == Perl6ElementTypes.INFIX || right.getNode().getElementType() == Perl6ElementTypes.INFIX ? SINGLE_SPACE_SPACING : null);
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
                PsiElement source = PsiTreeUtil.getParentOfType(blockoid.getPsi(), Perl6PackageDecl.class, Perl6RoutineDecl.class, Perl6RegexDecl.class, Perl6PointyBlock.class, Perl6Statement.class);
                Collection<PsiElement> inner = PsiTreeUtil.findChildrenOfAnyType(blockoid.getPsi(), Perl6Statement.class, Perl6Regex.class);
                int statementCount;
                if (inner.size() == 1 && inner.iterator().next() instanceof Perl6Regex) {
                    statementCount = inner.iterator().next().getChildren().length;
                } else {
                    statementCount = inner.size();
                }
                if (statementCount == 0) {
                    if (source instanceof Perl6PackageDecl && customSettings.PACKAGE_DECLARATION_IN_ONE_LINE)
                        return EMPTY_SPACING;
                    if (source instanceof Perl6RoutineDecl && customSettings.ROUTINES_DECLARATION_IN_ONE_LINE)
                        return EMPTY_SPACING;
                    if (source instanceof Perl6RegexDecl && customSettings.REGEX_DECLARATION_IN_ONE_LINE)
                        return EMPTY_SPACING;
                    if (source instanceof Perl6PointyBlock && customSettings.POINTY_BLOCK_IN_ONE_LINE)
                        return EMPTY_SPACING;
                    if (source instanceof Perl6Statement && commonSettings.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE)
                        return EMPTY_SPACING;
                }
            }
            return SINGLE_LINE_BREAK;
        });

        rules.add((left, right) -> STATEMENTS.contains(left.getNode().getElementType())
                                   || STATEMENTS.contains(right.getNode().getElementType())
                                   ? SINGLE_LINE_BREAK : null);
    }

    @Nullable
    private static ASTNode getParent(ASTNode node, int number) {
        while (number != 0) {
            if (node != null)
                node = node.getTreeParent();
            number--;
        }
        return node;
    }
}
