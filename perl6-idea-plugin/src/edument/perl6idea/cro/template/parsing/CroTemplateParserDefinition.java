package edument.perl6idea.cro.template.parsing;

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
import edument.perl6idea.cro.template.psi.impl.*;
import edument.perl6idea.parsing.Perl6OPPElementTypes;
import org.jetbrains.annotations.NotNull;

public class CroTemplateParserDefinition implements ParserDefinition {
    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new CroTemplateLexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new CroTemplateParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return CroTemplateElementTypes.FILE;
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
    public PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();
        if (type == CroTemplateElementTypes.STRING_LITERAL)
            return new CroTemplateStringLiteralImpl(node);
        if (type == CroTemplateElementTypes.LITERAL_OPEN_TAG)
            return new CroTemplateLiteralOpenTagImpl(node);
        if (type == CroTemplateElementTypes.LITERAL_CLOSE_TAG)
            return new CroTemplateLiteralCloseTagImpl(node);
        if (type == CroTemplateElementTypes.LITERAL_TAG_ATTRIBUTE)
            return new CroTemplateLiteralTagAttributeImpl(node);
        if (type == CroTemplateElementTypes.INT_LITERAL)
            return new CroTemplateIntLiteralImpl(node);
        if (type == CroTemplateElementTypes.RAT_LITERAL)
            return new CroTemplateRatLiteralImpl(node);
        if (type == CroTemplateElementTypes.NUM_LITERAL)
            return new CroTemplateNumLiteralImpl(node);
        if (type == CroTemplateElementTypes.TOPIC_ACCESS)
            return new CroTemplateTopicAccessImpl(node);
        if (type == CroTemplateElementTypes.VARIABLE_ACCESS)
            return new CroTemplateVariableAccessImpl(node);
        if (type == CroTemplateElementTypes.ITERATION)
            return new CroTemplateIterationImpl(node);
        if (type == CroTemplateElementTypes.CONDITION)
            return new CroTemplateConditionImpl(node);
        if (type == CroTemplateElementTypes.CALL)
            return new CroTemplateCallImpl(node);
        if (type == CroTemplateElementTypes.SUB)
            return new CroTemplateSubImpl(node);
        if (type == CroTemplateElementTypes.USE)
            return new CroTemplateUseImpl(node);
        if (type == CroTemplateElementTypes.MACRO)
            return new CroTemplateMacroImpl(node);
        if (type == CroTemplateElementTypes.BODY)
            return new CroTemplateBodyImpl(node);
        if (type == CroTemplateElementTypes.APPLY)
            return new CroTemplateApplyImpl(node);
        if (type == CroTemplateElementTypes.TAG_SEQUENCE)
            return new CroTemplateTagSequenceImpl(node);
        if (type == CroTemplateElementTypes.SIGNATURE)
            return new CroTemplateSignatureImpl(node);
        if (type == CroTemplateElementTypes.PARAMETER)
            return new CroTemplateParameterImpl(node);
        if (type == CroTemplateElementTypes.ARGLIST)
            return new CroTemplateArgListImpl(node);
        if (type == CroTemplateElementTypes.DEREF_SMART)
            return new CroTemplateDerefSmartImpl(node);
        if (type == CroTemplateElementTypes.DEREF_ARRAY)
            return new CroTemplateDerefArrayImpl(node);
        if (type == CroTemplateElementTypes.DEREF_HASH)
            return new CroTemplateDerefHashImpl(node);
        if (type == CroTemplateElementTypes.DEREF_HASH_LITERAL)
            return new CroTemplateDerefHashLiteralImpl(node);
        if (type == CroTemplateElementTypes.DEREF_METHOD)
            return new CroTemplateDerefMethodImpl(node);
        if (type == Perl6OPPElementTypes.INFIX_APPLICATION)
            return new CroTemplateInfixApplicationImpl(node);
        if (type == CroTemplateElementTypes.INFIX)
            return new CroTemplateInfixImpl(node);
        if (type == CroTemplateElementTypes.PARENTHESIZED_EXPRESSION)
            return new CroTemplateParenthesizedExpressionImpl(node);
        return null;
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new CroTemplateFileImpl(viewProvider);
    }
}
