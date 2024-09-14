package org.raku.comma.editor;

import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.inspections.CommentSplitter;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.raku.comma.parsing.RakuTokenTypes;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuSpellCheckingStrategy extends SpellcheckingStrategy {
    private final Tokenizer<?> perl6CommentTokenizer = new Tokenizer<>() {
        @Override
        public void tokenize(@NotNull PsiElement element, TokenConsumer consumer) {
            consumer.consumeToken(element, CommentSplitter.getInstance());
        }
    };

    @NotNull
    @Override
    public Tokenizer<?> getTokenizer(PsiElement element) {
        if (element.getNode().getElementType() == RakuTokenTypes.COMMENT ||
            element.getNode().getElementType() == RakuTokenTypes.POD_FINISH_TEXT ||
            element.getNode().getElementType() == RakuTokenTypes.POD_TEXT) {
            if (SuppressionUtil.isSuppressionComment(element)) {
                return EMPTY_TOKENIZER;
            }
            return perl6CommentTokenizer;
        }
        return EMPTY_TOKENIZER;
    }
}
