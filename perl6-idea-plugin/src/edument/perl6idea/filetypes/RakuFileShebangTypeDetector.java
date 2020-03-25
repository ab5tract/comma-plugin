package edument.perl6idea.filetypes;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector;
import org.jetbrains.annotations.NotNull;

public class RakuFileShebangTypeDetector extends HashBangFileTypeDetector {
    public RakuFileShebangTypeDetector() {
        super(Perl6ScriptFileType.INSTANCE, "raku");
    }
}
