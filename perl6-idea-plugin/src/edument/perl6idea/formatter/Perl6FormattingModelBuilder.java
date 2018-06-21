package edument.perl6idea.formatter;

import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.DocumentBasedFormattingModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6FormattingModelBuilder implements FormattingModelBuilder {
    @NotNull
    @Override
    public FormattingModel createModel(@NotNull PsiElement element, @NotNull CodeStyleSettings settings) {
        final PsiFile psiFile = element.getContainingFile();
        final Perl6Block block = new Perl6Block(psiFile.getNode(), null,null, settings);
        return new DocumentBasedFormattingModel(block, element.getProject(), settings, psiFile.getFileType(), psiFile);
    }

    @Nullable
    @Override
    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return null;
    }
}
