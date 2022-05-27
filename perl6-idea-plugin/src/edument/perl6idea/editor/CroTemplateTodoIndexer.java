package edument.perl6idea.editor;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.impl.cache.impl.BaseFilterLexer;
import com.intellij.psi.impl.cache.impl.IdAndToDoScannerBasedOnFilterLexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer;
import com.intellij.psi.tree.IElementType;
import edument.perl6idea.cro.template.parsing.CroTemplateLexer;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class CroTemplateTodoIndexer extends LexerBasedTodoIndexer implements IdAndToDoScannerBasedOnFilterLexer {
    @Override
    public @NotNull Lexer createLexer(@NotNull OccurrenceConsumer consumer) {
        return new RakuFilterLexer(consumer);
    }

    private static class RakuFilterLexer extends BaseFilterLexer {
        RakuFilterLexer(@NotNull OccurrenceConsumer consumer) {
            super(new CroTemplateLexer(), consumer);
        }

        @Override
        public void advance() {
            @Nullable IElementType tokenType = myDelegate.getTokenType();
            if (tokenType == CroTemplateTokenTypes.COMMENT) {
                advanceTodoItemCountsInToken();
            }
            myDelegate.advance();
        }
    }
}
