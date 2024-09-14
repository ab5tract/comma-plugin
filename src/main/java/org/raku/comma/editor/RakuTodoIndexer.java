package org.raku.comma.editor;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.impl.cache.impl.BaseFilterLexer;
import com.intellij.psi.impl.cache.impl.IdAndToDoScannerBasedOnFilterLexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer;
import com.intellij.psi.tree.IElementType;
import org.raku.comma.parsing.RakuLexer;
import org.raku.comma.parsing.RakuTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuTodoIndexer extends LexerBasedTodoIndexer implements IdAndToDoScannerBasedOnFilterLexer {
    @Override
    public @NotNull Lexer createLexer(@NotNull OccurrenceConsumer consumer) {
        return new RakuFilterLexer(consumer);
    }

    private static class RakuFilterLexer extends BaseFilterLexer {
        RakuFilterLexer(@NotNull OccurrenceConsumer consumer) {
            super(new RakuLexer(), consumer);
        }

        @Override
        public void advance() {
            @Nullable IElementType tokenType = myDelegate.getTokenType();
            if (tokenType == RakuTokenTypes.COMMENT)
                advanceTodoItemCountsInToken();
            myDelegate.advance();
        }
    }
}
