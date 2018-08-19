package edument.perl6idea.parsing;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.psi.impl.*;
import org.jetbrains.annotations.NotNull;

public class Perl6ParserDefinition implements ParserDefinition {
    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new Perl6Lexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new Perl6Parser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return Perl6ElementTypes.FILE;
    }

    // Both whitespace and comment tokens are empty, as we want to
    // match it in our parser
    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode astNode) {
        IElementType type = astNode.getElementType();
        if (type == Perl6ElementTypes.STATEMENT_LIST)
            return new Perl6StatementListImpl(astNode);
        if (type == Perl6ElementTypes.UNTERMINATED_STATEMENT)
            return new Perl6UnterminatedStatementImpl(astNode);
        if (type == Perl6ElementTypes.SEMI_LIST)
            return new Perl6SemiListImpl(astNode);
        if (type == Perl6ElementTypes.STATEMENT)
            return new Perl6StatementImpl(astNode);
        if (type == Perl6ElementTypes.LABEL)
            return new Perl6LabelImpl(astNode);
        if (type == Perl6ElementTypes.TERM_DEFINITION)
            return new Perl6TermDefinitionImpl(astNode);
        if (type == Perl6ElementTypes.BLOCK)
            return new Perl6BlockImpl(astNode);
        if (type == Perl6ElementTypes.BLOCKOID)
            return new Perl6BlockoidImpl(astNode);
        if (type == Perl6ElementTypes.IF_STATEMENT)
            return new Perl6IfStatementImpl(astNode);
        if (type == Perl6ElementTypes.UNLESS_STATEMENT)
            return new Perl6UnlessStatementImpl(astNode);
        if (type == Perl6ElementTypes.WITHOUT_STATEMENT)
            return new Perl6WithoutStatementImpl(astNode);
        if (type == Perl6ElementTypes.WHILE_STATEMENT)
            return new Perl6WhileStatementImpl(astNode);
        if (type == Perl6ElementTypes.UNTIL_STATEMENT)
            return new Perl6UntilStatementImpl(astNode);
        if (type == Perl6ElementTypes.REPEAT_STATEMENT)
            return new Perl6RepeatStatementImpl(astNode);
        if (type == Perl6ElementTypes.FOR_STATEMENT)
            return new Perl6ForStatementImpl(astNode);
        if (type == Perl6ElementTypes.WHENEVER_STATEMENT)
            return new Perl6WheneverStatementImpl(astNode);
        if (type == Perl6ElementTypes.LOOP_STATEMENT)
            return new Perl6LoopStatementImpl(astNode);
        if (type == Perl6ElementTypes.NEED_STATEMENT)
            return new Perl6NeedStatementImpl(astNode);
        if (type == Perl6ElementTypes.IMPORT_STATEMENT)
            return new Perl6ImportStatementImpl(astNode);
        if (type == Perl6ElementTypes.NO_STATEMENT)
            return new Perl6NoStatementImpl(astNode);
        if (type == Perl6ElementTypes.USE_STATEMENT)
            return new Perl6UseStatementImpl(astNode);
        if (type == Perl6ElementTypes.REQUIRE_STATEMENT)
            return new Perl6RequireStatementImpl(astNode);
        if (type == Perl6ElementTypes.GIVEN_STATEMENT)
            return new Perl6GivenStatementImpl(astNode);
        if (type == Perl6ElementTypes.WHEN_STATEMENT)
            return new Perl6WheneverStatementImpl(astNode);
        if (type == Perl6ElementTypes.DEFAULT_STATEMENT)
            return new Perl6DefaultStatementImpl(astNode);
        if (type == Perl6ElementTypes.CATCH_STATEMENT)
            return new Perl6CatchStatementImpl(astNode);
        if (type == Perl6ElementTypes.CONTROL_STATEMENT)
            return new Perl6ControlStatementImpl(astNode);
        if (type == Perl6ElementTypes.QUIT_STATEMENT)
            return new Perl6QuitStatementImpl(astNode);
        if (type == Perl6ElementTypes.PHASER)
            return new Perl6PhaserImpl(astNode);
        if (type == Perl6ElementTypes.RACE)
            return new Perl6RaceImpl(astNode);
        if (type == Perl6ElementTypes.HYPER)
            return new Perl6HyperImpl(astNode);
        if (type == Perl6ElementTypes.LAZY)
            return new Perl6LazyImpl(astNode);
        if (type == Perl6ElementTypes.EAGER)
            return new Perl6EagerImpl(astNode);
        if (type == Perl6ElementTypes.SINK)
            return new Perl6SinkImpl(astNode);
        if (type == Perl6ElementTypes.TRY)
            return new Perl6TryImpl(astNode);
        if (type == Perl6ElementTypes.QUIETLY)
            return new Perl6QuietlyImpl(astNode);
        if (type == Perl6ElementTypes.GATHER)
            return new Perl6GatherImpl(astNode);
        if (type == Perl6ElementTypes.ONCE)
            return new Perl6OnceImpl(astNode);
        if (type == Perl6ElementTypes.START)
            return new Perl6StartImpl(astNode);
        if (type == Perl6ElementTypes.SUPPLY)
            return new Perl6SupplyImpl(astNode);
        if (type == Perl6ElementTypes.REACT)
            return new Perl6ReactImpl(astNode);
        if (type == Perl6ElementTypes.DO)
            return new Perl6DoImpl(astNode);
        if (type == Perl6ElementTypes.STATEMENT_MOD_COND)
            return new Perl6StatementModCondImpl(astNode);
        if (type == Perl6ElementTypes.STATEMENT_MOD_LOOP)
            return new Perl6StatementModLoopImpl(astNode);
        if (type == Perl6ElementTypes.SCOPED_DECLARATION)
            return new Perl6ScopedDeclImpl(astNode);
        if (type == Perl6ElementTypes.VARIABLE_DECLARATION)
            return new Perl6VariableDeclImpl(astNode);
        if (type == Perl6ElementTypes.PACKAGE_DECLARATION)
            return new Perl6PackageDeclImpl(astNode);
        if (type == Perl6ElementTypes.ROLE_SIGNATURE)
            return new Perl6RoleSignatureImpl(astNode);
        if (type == Perl6ElementTypes.POINTY_BLOCK)
            return new Perl6PointyBlockImpl(astNode);
        if (type == Perl6ElementTypes.PREFIX)
            return new Perl6PrefixImpl(astNode);
        if (type == Perl6ElementTypes.INFIX)
            return new Perl6InfixImpl(astNode);
        if (type == Perl6ElementTypes.POSTFIX)
            return new Perl6PostfixImpl(astNode);
        if (type == Perl6ElementTypes.OPERATOR_ADVERB)
            return new Perl6OperatorAdverbImpl(astNode);
        if (type == Perl6OPPElementTypes.PREFIX_APPLICATION)
            return new Perl6PrefixApplicationImpl(astNode);
        if (type == Perl6OPPElementTypes.POSTFIX_APPLICATION)
            return new Perl6PostfixApplicationImpl(astNode);
        if (type == Perl6OPPElementTypes.INFIX_APPLICATION)
            return new Perl6InfixApplicationImpl(astNode);
        if (type == Perl6OPPElementTypes.ADVERB_APPLICATION)
            return new Perl6OperatorAdverbApplicationImpl(astNode);
        if (type == Perl6ElementTypes.REDUCE_METAOP)
            return new Perl6ReduceMetaOpImpl(astNode);
        if (type == Perl6ElementTypes.CROSS_METAOP)
            return new Perl6CrossMetaOpImpl(astNode);
        if (type == Perl6ElementTypes.NEGATION_METAOP)
            return new Perl6NegationMetaOpImpl(astNode);
        if (type == Perl6ElementTypes.REVERSE_METAOP)
            return new Perl6ReverseMetaOpImpl(astNode);
        if (type == Perl6ElementTypes.SEQUENTIAL_METAOP)
            return new Perl6SequentialMetaOpImpl(astNode);
        if (type == Perl6ElementTypes.ZIP_METAOP)
            return new Perl6ZipMetaOpImpl(astNode);
        if (type == Perl6ElementTypes.HYPER_METAOP)
            return new Perl6HyperMetaOpImpl(astNode);
        if (type == Perl6ElementTypes.ASSIGN_METAOP)
            return new Perl6AssignMetaOpImpl(astNode);
        if (type == Perl6ElementTypes.BRACKETED_INFIX)
            return new Perl6BracketedInfixImpl(astNode);
        if (type == Perl6ElementTypes.ARRAY_INDEX)
            return new Perl6ArrayIndexImpl(astNode);
        if (type == Perl6ElementTypes.HASH_INDEX)
            return new Perl6HashIndexImpl(astNode);
        if (type == Perl6ElementTypes.CALL)
            return new Perl6CallImpl(astNode);
        if (type == Perl6ElementTypes.VARIABLE)
            return new Perl6VariableImpl(astNode);
        if (type == Perl6ElementTypes.CONTEXTUALIZER)
            return new Perl6ContextualizerImpl(astNode);
        if (type == Perl6ElementTypes.INTEGER_LITERAL)
            return new Perl6IntLiteralImpl(astNode);
        if (type == Perl6ElementTypes.NUMBER_LITERAL)
            return new Perl6NumLiteralImpl(astNode);
        if (type == Perl6ElementTypes.RAT_LITERAL)
            return new Perl6RatLiteralImpl(astNode);
        if (type == Perl6ElementTypes.COMPLEX_LITERAL)
            return new Perl6ComplexLiteralImpl(astNode);
        if (type == Perl6ElementTypes.RADIX_NUMBER)
            return new Perl6RadixNumberImpl(astNode);
        if (type == Perl6ElementTypes.STRING_LITERAL)
            return new Perl6StrLiteralImpl(astNode);
        if (type == Perl6ElementTypes.HEREDOC)
            return new Perl6HeredocImpl(astNode);
        if (type == Perl6ElementTypes.QUOTE_REGEX)
            return new Perl6QuoteRegexImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_CALL)
            return new Perl6RegexCallImpl(astNode);
        if (type == Perl6ElementTypes.QUOTE_PAIR)
            return new Perl6QuotePairImpl(astNode);
        if (type == Perl6ElementTypes.VERSION)
            return new Perl6VersionImpl(astNode);
        if (type == Perl6ElementTypes.SUB_CALL)
            return new Perl6SubCallImpl(astNode);
        if (type == Perl6ElementTypes.SUB_CALL_NAME)
            return new Perl6SubCallNameImpl(astNode);
        if (type == Perl6ElementTypes.METHOD_CALL)
            return new Perl6MethodCallImpl(astNode);
        if (type == Perl6ElementTypes.TYPE_NAME)
            return new Perl6TypeNameImpl(astNode);
        if (type == Perl6ElementTypes.LONG_NAME)
            return new Perl6LongNameImpl(astNode);
        if (type == Perl6ElementTypes.MODULE_NAME)
            return new Perl6ModuleNameImpl(astNode);
        if (type == Perl6ElementTypes.IS_TRAIT_NAME)
            return new Perl6IsTraitNameImpl(astNode);
        if (type == Perl6ElementTypes.SELF)
            return new Perl6SelfImpl(astNode);
        if (type == Perl6ElementTypes.WHATEVER)
            return new Perl6WhateverImpl(astNode);
        if (type == Perl6ElementTypes.HYPER_WHATEVER)
            return new Perl6HyperWhateverImpl(astNode);
        if (type == Perl6ElementTypes.TERM)
            return new Perl6TermImpl(astNode);
        if (type == Perl6ElementTypes.NULL_TERM)
            return new Perl6NullTermImpl(astNode);
        if (type == Perl6ElementTypes.STUB_CODE)
            return new Perl6StubCodeImpl(astNode);
        if (type == Perl6ElementTypes.CAPTURE)
            return new Perl6CaptureImpl(astNode);
        if (type == Perl6ElementTypes.ONLY_STAR)
            return new Perl6OnlyStarImpl(astNode);
        if (type == Perl6ElementTypes.FATARROW)
            return new Perl6FatArrowImpl(astNode);
        if (type == Perl6ElementTypes.COLON_PAIR)
            return new Perl6ColonPairImpl(astNode);
        if (type == Perl6ElementTypes.ROUTINE_DECLARATION)
            return new Perl6RoutineDeclImpl(astNode);
        if (type == Perl6ElementTypes.MULTI_DECLARATION)
            return new Perl6MultiDeclImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_DECLARATION)
            return new Perl6RegexDeclImpl(astNode);
        if (type == Perl6ElementTypes.SIGNATURE)
            return new Perl6SignatureImpl(astNode);
        if (type == Perl6ElementTypes.PARAMETER)
            return new Perl6ParameterImpl(astNode);
        if (type == Perl6ElementTypes.PARAMETER_VARIABLE)
            return new Perl6ParameterVariableImpl(astNode);
        if (type == Perl6ElementTypes.NAMED_PARAMETER)
            return new Perl6NamedParameterImpl(astNode);
        if (type == Perl6ElementTypes.ARRAY_SHAPE)
            return new Perl6ArrayShapeImpl(astNode);
        if (type == Perl6ElementTypes.WHERE_CONSTRAINT)
            return new Perl6WhereConstraintImpl(astNode);
        if (type == Perl6ElementTypes.VALUE_CONSTRAINT)
            return new Perl6ValueConstraintImpl(astNode);
        if (type == Perl6ElementTypes.PARAMETER_DEFAULT)
            return new Perl6ParameterDefaultImpl(astNode);
        if (type == Perl6ElementTypes.RETURN_CONSTRAINT)
            return new Perl6ReturnConstraintImpl(astNode);
        if (type == Perl6ElementTypes.TRAIT)
            return new Perl6TraitImpl(astNode);
        if (type == Perl6ElementTypes.PARENTHESIZED_EXPRESSION)
            return new Perl6ParenthesizedExprImpl(astNode);
        if (type == Perl6ElementTypes.ARRAY_COMPOSER)
            return new Perl6ArrayComposerImpl(astNode);
        if (type == Perl6ElementTypes.BLOCK_OR_HASH)
            return new Perl6BlockOrHashImpl(astNode);
        if (type == Perl6ElementTypes.ENUM)
            return new Perl6EnumImpl(astNode);
        if (type == Perl6ElementTypes.SUBSET)
            return new Perl6SubsetImpl(astNode);
        if (type == Perl6ElementTypes.CONSTANT)
            return new Perl6ConstantImpl(astNode);
        if (type == Perl6ElementTypes.REGEX)
            return new Perl6RegexImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_ATOM)
            return new Perl6RegexAtomImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_SIGSPACE)
            return new Perl6RegexSigspaceImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_LITERAL)
            return new Perl6RegexLiteralImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_ANCHOR)
            return new Perl6RegexAnchorImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_GROUP)
            return new Perl6RegexGroupImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_CAPTURE_POSITIONAL)
            return new Perl6RegexCapturePositionalImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_QUANTIFIER)
            return new Perl6RegexQuantifierImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_SEPARATOR)
            return new Perl6RegexSeparatorImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_BUILTIN_CCLASS)
            return new Perl6RegexBuiltinCClassImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_ASSERTION)
            return new Perl6RegexAssertionImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_GOAL)
            return new Perl6RegexGoalImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_CCLASS)
            return new Perl6RegexCClassImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_CCLASS_ELEM)
            return new Perl6RegexCClassElemImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_MOD_INTERNAL)
            return new Perl6RegexModInternalImpl(astNode);
        if (type == Perl6ElementTypes.REGEX_VARIABLE)
            return new Perl6RegexVariableImpl(astNode);
        if (type == Perl6ElementTypes.TRANSLITERATION)
            return new Perl6TransliterationImpl(astNode);
        if (type == Perl6ElementTypes.POD_BLOCK_FINISH)
            return new PodBlockFinishImpl(astNode);
        if (type == Perl6ElementTypes.POD_BLOCK_DELIMITED)
            return new PodBlockDelimitedImpl(astNode);
        if (type == Perl6ElementTypes.POD_BLOCK_PARAGRAPH)
            return new PodBlockParagraphImpl(astNode);
        if (type == Perl6ElementTypes.POD_BLOCK_ABBREVIATED)
            return new PodBlockAbbreviatedImpl(astNode);
        if (type == Perl6ElementTypes.POD_CONFIGURATION)
            return new PodConfigurationImpl(astNode);
        if (type == Perl6ElementTypes.POD_FORMATTED)
            return new PodFormattedImpl(astNode);
        if (type == Perl6ElementTypes.POD_TEXT)
            return new PodTextImpl(astNode);
        if (type == Perl6ElementTypes.QUASI)
            return new Perl6QuasiImpl(astNode);
        return null;
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new Perl6FileImpl(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
        return SpaceRequirements.MAY;
    }
}
