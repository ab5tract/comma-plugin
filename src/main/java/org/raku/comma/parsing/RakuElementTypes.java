package org.raku.comma.parsing;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.stubs.IStubElementType;
import org.raku.comma.psi.*;
import org.raku.comma.psi.stub.*;

public interface RakuElementTypes {
    IStubFileElementType<RakuFileStub> FILE = new RakuFileElementType();
    IStubElementType<RakuPackageDeclStub, RakuPackageDecl> PACKAGE_DECLARATION = new RakuPackageDeclStubElementType();
    IStubElementType<RakuRoutineDeclStub, RakuRoutineDecl> ROUTINE_DECLARATION = new RakuRoutineDeclStubElementType();
    IStubElementType<RakuEnumStub, RakuEnum> ENUM = new RakuEnumStubElementType();
    IStubElementType<RakuSubsetStub, RakuSubset> SUBSET = new RakuSubsetStubElementType();
    IStubElementType<RakuConstantStub, RakuConstant> CONSTANT = new RakuConstantStubElementType();
    IStubElementType<RakuRegexDeclStub, RakuRegexDecl> REGEX_DECLARATION = new RakuRegexDeclStubElementType();
    IStubElementType<RakuVariableDeclStub, RakuVariableDecl> VARIABLE_DECLARATION = new RakuVariableDeclStubElementType();
    IStubElementType<RakuScopedDeclStub, RakuScopedDecl> SCOPED_DECLARATION = new RakuScopedDeclStubElementType();
    IStubElementType<RakuUseStatementStub, RakuUseStatement> USE_STATEMENT = new RakuUseStatementStubElementType();
    IStubElementType<RakuNeedStatementStub, RakuNeedStatement> NEED_STATEMENT = new RakuNeedStatementStubElementType();
    IStubElementType<RakuTypeNameStub, RakuTypeName> TYPE_NAME = new RakuTypeNameStubElementType();
    IStubElementType<RakuTraitStub, RakuTrait> TRAIT = new RakuTraitStubElementType();
    IStubElementType<RakuSubCallStub, RakuSubCall> SUB_CALL = new RakuSubCallStubElementType();
    IElementType ALSO = new RakuElementType("ALSO");
    IElementType ARRAY_COMPOSER = new RakuElementType("ARRAY_COMPOSER");
    IElementType ARRAY_INDEX = new RakuElementType("ARRAY_INDEX");
    IElementType ARRAY_SHAPE = new RakuElementType("ARRAY_SHAPE");
    IElementType ASSIGN_METAOP = new RakuElementType("ASSIGN_METAOP");
    IElementType BLOCK = new RakuElementType("BLOCK");
    IElementType BLOCKOID = new RakuElementType("BLOCKOID");
    IElementType BLOCK_OR_HASH = new RakuElementType("BLOCK_OR_HASH");
    IElementType BRACKETED_INFIX = new RakuElementType("BRACKETED_INFIX");
    IElementType CALL = new RakuElementType("CALL");
    IElementType CAPTURE = new RakuElementType("CAPTURE");
    IElementType CATCH_STATEMENT = new RakuElementType("CATCH_STATEMENT");
    IElementType COLON_PAIR = new RakuElementType("COLON_PAIR");
    IElementType COMMENT = new RakuElementType("COMMENT");
    IElementType COMPLEX_LITERAL = new RakuElementType("COMPLEX_LITERAL");
    IElementType CONTEXTUALIZER = new RakuElementType("CONTEXTUALIZER");
    IElementType CONTROL_STATEMENT = new RakuElementType("CONTROL_STATEMENT");
    IElementType CROSS_METAOP = new RakuElementType("CROSS_METAOP");
    IElementType DEFAULT_STATEMENT = new RakuElementType("DEFAULT_STATEMENT");
    IElementType DO = new RakuElementType("DO");
    IElementType EAGER = new RakuElementType("EAGER");
    IElementType FATARROW = new RakuElementType("FATARROW");
    IElementType FOR_STATEMENT = new RakuElementType("FOR_STATEMENT");
    IElementType GATHER = new RakuElementType("GATHER");
    IElementType GIVEN_STATEMENT = new RakuElementType("GIVEN_STATEMENT");
    IElementType HASH_INDEX = new RakuElementType("HASH_INDEX");
    IElementType HEREDOC = new RakuElementType("HEREDOC");
    IElementType HYPER = new RakuElementType("HYPER");
    IElementType HYPER_METAOP = new RakuElementType("HYPER_METAOP");
    IElementType HYPER_WHATEVER = new RakuElementType("HYPER_WHATEVER");
    IElementType IF_STATEMENT = new RakuElementType("IF_STATEMENT");
    IElementType IMPORT_STATEMENT = new RakuElementType("IMPORT_STATEMENT");
    IElementType INFIX = new RakuElementType("INFIX");
    IElementType INTEGER_LITERAL = new RakuElementType("INTEGER_LITERAL");
    IElementType IS_TRAIT_NAME = new RakuElementType("IS_TRAIT_NAME");
    IElementType LABEL = new RakuElementType("LABEL");
    IElementType LAZY = new RakuElementType("LAZY");
    IElementType LONG_NAME = new RakuElementType("LONG_NAME");
    IElementType LOOP_STATEMENT = new RakuElementType("LOOP_STATEMENT");
    IElementType METHOD_CALL = new RakuElementType("METHOD_CALL");
    IElementType MODULE_NAME = new RakuElementType("MODULE_NAME");
    IElementType MULTI_DECLARATION = new RakuElementType("MULTI_DECLARATION");
    IElementType NAMED_PARAMETER = new RakuElementType("NAMED_PARAMETER");
    IElementType NEGATION_METAOP = new RakuElementType("NEGATION_METAOP");
    IElementType NO_STATEMENT = new RakuElementType("NO_STATEMENT");
    IElementType NULL_TERM = new RakuElementType("NULL_TERM");
    IElementType NUMBER_LITERAL = new RakuElementType("NUMBER_LITERAL");
    IElementType ONCE = new RakuElementType("ONCE");
    IElementType ONLY_STAR = new RakuElementType("ONLY_STAR");
    IElementType OPERATOR_ADVERB = new RakuElementType("OPERATOR_ADVERB");
    IElementType PARAMETER = new RakuElementType("PARAMETER");
    IElementType PARAMETER_DEFAULT = new RakuElementType("PARAMETER_DEFAULT");
    IElementType PARAMETER_VARIABLE = new RakuElementType("PARAMETER_VARIABLE");
    IElementType PARENTHESIZED_EXPRESSION = new RakuElementType("PARENTHESIZED_EXPRESSION");
    IElementType PHASER = new RakuElementType("PHASER");
    IElementType POD_BLOCK_ABBREVIATED = new RakuElementType("POD_BLOCK_ABBREVIATED");
    IElementType POD_BLOCK_DELIMITED = new RakuElementType("POD_BLOCK_DELIMITED");
    IElementType POD_BLOCK_FINISH = new RakuElementType("POD_BLOCK_FINISH");
    IElementType POD_BLOCK_PARAGRAPH = new RakuElementType("POD_BLOCK_PARAGRAPH");
    IElementType POD_CONFIGURATION = new RakuElementType("POD_CONFIGURATION");
    IElementType POD_FORMATTED = new RakuElementType("POD_FORMATTED");
    IElementType POD_POST_COMMENT = new RakuElementType("POD_POST_COMMENT");
    IElementType POD_PRE_COMMENT = new RakuElementType("POD_PRE_COMMENT");
    IElementType POD_TEXT = new RakuElementType("POD_TEXT");
    IElementType POINTY_BLOCK = new RakuElementType("POINTY_BLOCK");
    IElementType POSTFIX = new RakuElementType("POSTFIX");
    IElementType POSTFIX_APPLICATION = new RakuElementType("POSTFIX_APPLICATION");
    IElementType PREFIX = new RakuElementType("PREFIX");
    IElementType QUASI = new RakuElementType("QUASI");
    IElementType QUIETLY = new RakuElementType("QUIETLY");
    IElementType QUIT_STATEMENT = new RakuElementType("QUIT_STATEMENT");
    IElementType QUOTE_PAIR = new RakuElementType("QUOTE_PAIR");
    IElementType QUOTE_REGEX = new RakuElementType("QUOTE_REGEX");
    IElementType RACE = new RakuElementType("RACE");
    IElementType RADIX_NUMBER = new RakuElementType("RADIX_NUMBER");
    IElementType RAT_LITERAL = new RakuElementType("RAT_LITERAL");
    IElementType REACT = new RakuElementType("REACT");
    IElementType REDUCE_METAOP = new RakuElementType("REDUCE_METAOP");
    IElementType REGEX = new RakuElementType("REGEX");
    IElementType REGEX_ANCHOR = new RakuElementType("REGEX_ANCHOR");
    IElementType REGEX_ASSERTION = new RakuElementType("REGEX_ASSERTION");
    IElementType REGEX_ATOM = new RakuElementType("REGEX_ATOM");
    IElementType REGEX_BUILTIN_CCLASS = new RakuElementType("REGEX_BUILTIN_CCLASS");
    IElementType REGEX_CALL = new RakuElementType("REGEX_CALL");
    IElementType REGEX_CAPTURE_POSITIONAL = new RakuElementType("REGEX_CAPTURE_POSITIONAL");
    IElementType REGEX_CCLASS = new RakuElementType("REGEX_CCLASS");
    IElementType REGEX_CCLASS_ELEM = new RakuElementType("REGEX_CCLASS_ELEM");
    IElementType REGEX_GOAL = new RakuElementType("REGEX_GOAL");
    IElementType REGEX_GROUP = new RakuElementType("REGEX_GROUP");
    IElementType REGEX_LITERAL = new RakuElementType("REGEX_LITERAL");
    IElementType REGEX_MOD_INTERNAL = new RakuElementType("REGEX_MOD_INTERNAL");
    IElementType REGEX_QUANTIFIER = new RakuElementType("REGEX_QUANTIFIER");
    IElementType REGEX_SEPARATOR = new RakuElementType("REGEX_SEPARATOR");
    IElementType REGEX_SIGSPACE = new RakuElementType("REGEX_SIGSPACE");
    IElementType REGEX_VARIABLE = new RakuElementType("REGEX_VARIABLE");
    IElementType REPEAT_STATEMENT = new RakuElementType("REPEAT_STATEMENT");
    IElementType REQUIRE_STATEMENT = new RakuElementType("REQUIRE_STATEMENT");
    IElementType RETURN_CONSTRAINT = new RakuElementType("RETURN_CONSTRAINT");
    IElementType REVERSE_METAOP = new RakuElementType("REVERSE_METAOP");
    IElementType ROLE_SIGNATURE = new RakuElementType("ROLE_SIGNATURE");
    IElementType SELF = new RakuElementType("SELF");
    IElementType SEMI_LIST = new RakuElementType("SEMI_LIST");
    IElementType SEQUENTIAL_METAOP = new RakuElementType("SEQUENTIAL_METAOP");
    IElementType SIGNATURE = new RakuElementType("SIGNATURE");
    IElementType SINK = new RakuElementType("SINK");
    IElementType START = new RakuElementType("START");
    IElementType STATEMENT = new RakuElementType("STATEMENT");
    IElementType STATEMENT_LIST = new RakuElementType("STATEMENT_LIST");
    IElementType STATEMENT_MOD_COND = new RakuElementType("STATEMENT_MOD_COND");
    IElementType STATEMENT_MOD_LOOP = new RakuElementType("STATEMENT_MOD_LOOP");
    IElementType STRING_LITERAL = new RakuElementType("STRING_LITERAL");
    IElementType STUB_CODE = new RakuElementType("STUB_CODE");
    IElementType SUB_CALL_NAME = new RakuElementType("SUB_CALL_NAME");
    IElementType SUPPLY = new RakuElementType("SUPPLY");
    IElementType TERM = new RakuElementType("TERM");
    IElementType TERM_DEFINITION = new RakuElementType("TERM_DEFINITION");
    IElementType TRANSLITERATION = new RakuElementType("TRANSLITERATION");
    IElementType TRUSTS = new RakuElementType("TRUSTS");
    IElementType TRY = new RakuElementType("TRY");
    IElementType UNLESS_STATEMENT = new RakuElementType("UNLESS_STATEMENT");
    IElementType UNTERMINATED_STATEMENT = new RakuElementType("UNTERMINATED_STATEMENT");
    IElementType UNTIL_STATEMENT = new RakuElementType("UNTIL_STATEMENT");
    IElementType VALUE_CONSTRAINT = new RakuElementType("VALUE_CONSTRAINT");
    IElementType VARIABLE = new RakuElementType("VARIABLE");
    IElementType VERSION = new RakuElementType("VERSION");
    IElementType WHATEVER = new RakuElementType("WHATEVER");
    IElementType WHENEVER_STATEMENT = new RakuElementType("WHENEVER_STATEMENT");
    IElementType WHEN_STATEMENT = new RakuElementType("WHEN_STATEMENT");
    IElementType WHERE_CONSTRAINT = new RakuElementType("WHERE_CONSTRAINT");
    IElementType WHILE_STATEMENT = new RakuElementType("WHILE_STATEMENT");
    IElementType WITHOUT_STATEMENT = new RakuElementType("WITHOUT_STATEMENT");
    IElementType ZIP_METAOP = new RakuElementType("ZIP_METAOP");
}