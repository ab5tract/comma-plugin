package edument.perl6idea.filetypes;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector;
import org.jetbrains.annotations.NotNull;

public class RakudoFileShebangTypeDetector extends HashBangFileTypeDetector {
    public RakudoFileShebangTypeDetector() {
        super(Perl6ScriptFileType.INSTANCE, "rakudo");
    }
}
