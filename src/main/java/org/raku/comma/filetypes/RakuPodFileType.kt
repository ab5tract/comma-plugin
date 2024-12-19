package org.raku.comma.filetypes

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import org.raku.comma.RakuIcons
import org.raku.comma.RakuLanguage
import javax.swing.Icon

@InternalIgnoreDependencyViolation
class RakuPodFileType : LanguageFileType(RakuLanguage.INSTANCE), RakuMultiExtensionFileType {
    override fun getName(): String {
        return "Raku Pod"
    }

    override fun getDescription(): String {
        return "Raku Pod (Plain Old Documentation)"
    }

    override fun getDefaultExtension(): String {
        return "rakudoc"
    }

    override fun getIcon(): Icon {
        return RakuIcons.CAMELIA
    }

    override fun isReadOnly(): Boolean {
        return false
    }

    override fun getCharset(file: VirtualFile, content: ByteArray): String {
        return "UTF-8"
    }

    override fun extensions(): Array<String> = arrayOf<String>("pod6", "rakudoc")

    companion object {
        @JvmField
        val INSTANCE: RakuPodFileType = RakuPodFileType()
    }
}
