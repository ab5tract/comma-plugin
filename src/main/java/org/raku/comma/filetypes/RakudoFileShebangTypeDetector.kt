package org.raku.comma.filetypes

import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector

class RakudoFileShebangTypeDetector : HashBangFileTypeDetector(RakuScriptFileType.INSTANCE, "rakudo")
