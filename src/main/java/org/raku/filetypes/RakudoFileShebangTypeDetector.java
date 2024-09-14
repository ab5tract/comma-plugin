package org.raku.filetypes;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector;

@InternalIgnoreDependencyViolation
public class RakudoFileShebangTypeDetector extends HashBangFileTypeDetector {
    public RakudoFileShebangTypeDetector() {
        super(RakuScriptFileType.INSTANCE, "rakudo");
    }
}
