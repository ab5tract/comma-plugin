package org.raku.comma.filetypes;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector;

@InternalIgnoreDependencyViolation
public class RakuFileShebangTypeDetector extends HashBangFileTypeDetector {
    public RakuFileShebangTypeDetector() {
        super(RakuScriptFileType.INSTANCE, "raku");
    }
}
