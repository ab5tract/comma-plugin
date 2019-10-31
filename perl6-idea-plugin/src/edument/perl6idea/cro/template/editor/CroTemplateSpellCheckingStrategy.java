package edument.perl6idea.cro.template.editor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.spellchecker.inspections.CommentSplitter;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import org.jetbrains.annotations.NotNull;

public class CroTemplateSpellCheckingStrategy extends SpellcheckingStrategy {
    private Tokenizer tokenizer = new Tokenizer() {
        @Override
        public void tokenize(@NotNull PsiElement element, TokenConsumer consumer) {
            consumer.consumeToken(element, CommentSplitter.getInstance());
        }
    };

    @NotNull
    @Override
    public Tokenizer getTokenizer(PsiElement element) {
        IElementType type = element.getNode().getElementType();
        return type == CroTemplateTokenTypes.LITERAL_TEXT ? tokenizer : EMPTY_TOKENIZER;
    }
}
