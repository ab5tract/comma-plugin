package edument.perl6idea.editor;

import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.inspections.CommentSplitter;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import edument.perl6idea.parsing.Perl6TokenTypes;
import org.jetbrains.annotations.NotNull;

public class Perl6SpellCheckingStrategy extends SpellcheckingStrategy {
    private final Tokenizer perl6CommentTokenizer = new Tokenizer() {
        @Override
        public void tokenize(@NotNull PsiElement element, TokenConsumer consumer) {
            consumer.consumeToken(element, CommentSplitter.getInstance());
        }
    };

    @NotNull
    @Override
    public Tokenizer getTokenizer(PsiElement element) {
        if (element.getNode().getElementType() == Perl6TokenTypes.COMMENT ||
            element.getNode().getElementType() == Perl6TokenTypes.POD_FINISH_TEXT ||
            element.getNode().getElementType() == Perl6TokenTypes.POD_TEXT) {
            if (SuppressionUtil.isSuppressionComment(element)) {
                return EMPTY_TOKENIZER;
            }
            return perl6CommentTokenizer;
        }
        return EMPTY_TOKENIZER;
    }
}
