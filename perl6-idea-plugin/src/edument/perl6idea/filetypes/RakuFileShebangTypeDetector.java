package edument.perl6idea.filetypes;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector;

@InternalIgnoreDependencyViolation
public class RakuFileShebangTypeDetector extends HashBangFileTypeDetector {
    public RakuFileShebangTypeDetector() {
        super(Perl6ScriptFileType.INSTANCE, "raku");
    }
}
