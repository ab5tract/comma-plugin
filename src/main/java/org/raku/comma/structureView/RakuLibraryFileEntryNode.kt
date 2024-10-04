package org.raku.comma.structureView

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.BasePsiNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.RakuIcons
import org.raku.comma.psi.RakuFile
import org.raku.comma.psi.RakuPackageDecl
import javax.swing.Icon

class RakuLibraryFileEntryNode(
    project: Project,
    val rakuFile: RakuFile,
    settings: ViewSettings
): BasePsiNode<RakuFile>(project, rakuFile, settings) {

    private var icon: Icon? = null

    override fun update(data: PresentationData) {
        data.presentableText = rakuFile.moduleName
        data.setIcon(produceIcon())
        data.tooltip = rakuFile.originalPath
    }

    override fun contains(checkFile: VirtualFile): Boolean {
        return rakuFile.virtualFile == checkFile
    }

    override fun getChildrenImpl(): MutableCollection<AbstractTreeNode<*>> {
        return mutableListOf()
    }

    override fun updateImpl(data: PresentationData) {
        data.presentableText = rakuFile.moduleName
        data.setIcon(produceIcon())
        data.tooltip = rakuFile.originalPath
    }

    override fun getVirtualFile(): VirtualFile? {
        return rakuFile.virtualFile
    }

    private fun produceIcon(): Icon? {
        if (icon != null) return icon
        val declarator = PsiTreeUtil.findChildOfType(rakuFile, RakuPackageDecl::class.java) ?: return null
        icon = RakuIcons.iconForPackageDeclarator(declarator.packageKind)
        return icon
    }
}