package org.raku.filetypes;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector;

@InternalIgnoreDependencyViolation
public class Perl6FileShebangTypeDetector extends HashBangFileTypeDetector {
    public Perl6FileShebangTypeDetector() {
        super(RakuScriptFileType.INSTANCE, "raku");
    }
}
