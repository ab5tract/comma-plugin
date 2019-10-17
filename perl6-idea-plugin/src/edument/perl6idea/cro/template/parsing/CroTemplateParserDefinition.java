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
        if (type == CroTemplateElementTypes.USE)
            return new CroTemplateUseImpl(node);
        if (type == CroTemplateElementTypes.APPLY)
            return new CroTemplateApplyImpl(node);
        if (type == CroTemplateElementTypes.ARGLIST)
            return new CroTemplateArgListImpl(node);
        if (type == CroTemplateElementTypes.DEREF_SMART)
            return new CroTemplateDerefSmartImpl(node);
        return null;
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new CroTemplateFileImpl(viewProvider);
    }
}
