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
import edument.perl6idea.cro.template.psi.impl.CroTemplateFileImpl;
import edument.perl6idea.cro.template.psi.impl.CroTemplateUseImpl;
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
        if (type == CroTemplateElementTypes.USE)
            return new CroTemplateUseImpl(node);
        return null;
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new CroTemplateFileImpl(viewProvider);
    }
}
