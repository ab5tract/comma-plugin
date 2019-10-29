package edument.perl6idea.filetypes;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class Perl6FileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(Perl6ScriptFileType.INSTANCE, "p6;pl6;raku");
        consumer.consume(Perl6ModuleFileType.INSTANCE, "pm6;rakumod");
        consumer.consume(Perl6TestFileType.INSTANCE, "t;t6;rakutest");
        consumer.consume(Perl6PodFileType.INSTANCE, "pod6;rakudoc");
    }
}
