package edument.perl6idea.filetypes;

import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector;

public class Perl6FileShebangTypeDetector extends HashBangFileTypeDetector {
    public Perl6FileShebangTypeDetector() {
        super(Perl6ScriptFileType.INSTANCE, "perl6");
    }
}
