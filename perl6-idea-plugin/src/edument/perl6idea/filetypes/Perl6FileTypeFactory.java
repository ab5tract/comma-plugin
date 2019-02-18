package edument.perl6idea.filetypes;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class Perl6FileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(Perl6ScriptFileType.INSTANCE);
        consumer.consume(Perl6OldStyleScriptFileType.INSTANCE);
        consumer.consume(Perl6ModuleFileType.INSTANCE);
        consumer.consume(Perl6TestFileType.INSTANCE, "t6;t");
        consumer.consume(Perl6PodFileType.INSTANCE);
    }
}
