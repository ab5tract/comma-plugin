package edument.perl6idea.filetypes;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector;

@InternalIgnoreDependencyViolation
public class RakudoFileShebangTypeDetector extends HashBangFileTypeDetector {
    public RakudoFileShebangTypeDetector() {
        super(Perl6ScriptFileType.INSTANCE, "rakudo");
    }
}
