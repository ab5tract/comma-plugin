package edument.perl6idea.repl;

import com.intellij.execution.console.BasicGutterContentProvider;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

public class Perl6ReplGutter extends BasicGutterContentProvider {
    @Override
    public void beforeEvaluate(@NotNull Editor editor) {
        // Removed to prevent annoying zero-width chars getting stuck into the
        // copied code and screwing things up if it's pasted.
    }
}
