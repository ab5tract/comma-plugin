package edument.perl6idea.filetypes;

import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector;

public class RakudoFileShebangTypeDetector extends HashBangFileTypeDetector {
    public RakudoFileShebangTypeDetector() {
        super(Perl6ScriptFileType.INSTANCE, "rakudo");
    }
}
