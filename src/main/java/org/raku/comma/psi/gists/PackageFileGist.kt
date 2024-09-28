package org.raku.comma.psi.gists

import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.gist.GistManager
import com.intellij.util.gist.PsiFileGist
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.DataInputOutputUtil
import com.intellij.util.io.EnumeratorStringDescriptor
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.raku.comma.psi.RakuPackageDecl
import java.io.DataInput
import java.io.DataOutput


data class PackageDeclarationFile(val packageName: String, val offset: Int)




fun markLocationPerModule(file: PsiFile): Int2ObjectMap<PackageDeclarationFile> {
    val resolvedPackages = Int2ObjectOpenHashMap<PackageDeclarationFile>()

    var packageIndex = 0
    PsiTreeUtil.findChildrenOfType(file, RakuPackageDecl::class.java)
        .forEach { declaration ->
            resolvedPackages.put(packageIndex++, PackageDeclarationFile(declaration.packageName, declaration.textOffset))
        }

    return resolvedPackages
}


private class PackageFileExternalizer : DataExternalizer<Int2ObjectMap<PackageDeclarationFile>> {
    override fun save(out: DataOutput, values: Int2ObjectMap<PackageDeclarationFile>) {
        DataInputOutputUtil.writeINT(out, values.size)
        for (entry in Int2ObjectMaps.fastIterable(values)) {
            DataInputOutputUtil.writeINT(out, entry.intKey)
            EnumeratorStringDescriptor.INSTANCE.save(out, entry.value.packageName)
            out.writeInt(entry.value.offset)
        }
    }

    override fun read(input: DataInput): Int2ObjectMap<PackageDeclarationFile> {
        val size = DataInputOutputUtil.readINT(input)
        if (size == 0) {
            return Int2ObjectOpenHashMap()
        }

        val values = Int2ObjectOpenHashMap<PackageDeclarationFile>(size)
        repeat(size) {
            val id = DataInputOutputUtil.readINT(input)
            val value = PackageDeclarationFile(EnumeratorStringDescriptor.INSTANCE.read(input), input.readInt())
            values[id] = value
        }
        return values
    }
}

val rakuPackageDeclarationGist: PsiFileGist<Int2ObjectMap<PackageDeclarationFile>> = GistManager.getInstance().newPsiFileGist("raku.package.location", 2, PackageFileExternalizer()) { file ->
    markLocationPerModule(file)
}