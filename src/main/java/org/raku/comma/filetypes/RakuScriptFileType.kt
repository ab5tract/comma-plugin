package org.raku.comma.filetypes

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.Nls
import org.raku.comma.RakuIcons
import org.raku.comma.RakuLanguage
import javax.swing.Icon

@InternalIgnoreDependencyViolation
class RakuScriptFileType private constructor() : LanguageFileType(RakuLanguage.INSTANCE), RakuMultiExtensionFileType {
    override fun getName(): String {
        return "Raku Script"
    }

    override fun getDisplayName(): @Nls String {
        return getName()
    }

    override fun getDescription(): String {
        return "Raku script"
    }

    override fun getDefaultExtension(): String {
        return "raku"
    }

    override fun getIcon(): Icon? {
        return RakuIcons.CAMELIA
    }

    override fun isReadOnly(): Boolean {
        return false
    }

    override fun getCharset(file: VirtualFile, content: ByteArray): String {
        return "UTF-8"
    }

    override fun extensions(): Array<String> = arrayOf<String>("p6", "pl6", "raku")

    companion object {
        @JvmField
        val INSTANCE: RakuScriptFileType = RakuScriptFileType()
    }
}
