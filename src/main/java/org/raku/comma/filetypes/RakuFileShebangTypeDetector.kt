package org.raku.comma.filetypes

import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector

class RakuFileShebangTypeDetector : HashBangFileTypeDetector(RakuScriptFileType.INSTANCE, "raku")
