package org.raku.comma.structureView

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.util.PlatformIcons
import org.raku.comma.psi.RakuFile
import org.raku.comma.services.project.RakuDependencyDetailsService
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern

class RakuLibraryPsiDirectoryNode(project: Project, node: AbstractTreeNode<*>, settings: ViewSettings) :
    PsiDirectoryNode(project, (node.value as PsiDirectory), settings) {
    private val children: MutableList<AbstractTreeNode<*>> = ArrayList()
    private val presentableText: String

    init {
        val path = Objects.requireNonNull(Objects.requireNonNull((node as PsiDirectoryNode).virtualFile)!!.path)
        val pathPart = path.substring(1, path.length - 2)
        val matcher = pathPattern.matcher(pathPart)

        if (matcher.matches()) {
            val name = matcher.group(2)
            if (name != null) {
                project.service<RakuDependencyDetailsService>()
                    .moduleToRakuFiles(name).join()
                    .forEach(Consumer { rakuFile: RakuFile? ->
                        children.add(RakuLibraryFileEntryNode(project, rakuFile!!, settings))
                    })
            }
        }

        presentableText = if (children.isEmpty()) "<Module Source Unavailable>" else "Provides"
    }

    override fun update(data: PresentationData) {
        data.presentableText = presentableText
        data.setIcon(PlatformIcons.FOLDER_ICON)
        if (children.isEmpty()) {
            data.tooltip = "No source found. Is the dependency actually installed?"
        }
    }

    override fun getChildrenImpl(): Collection<AbstractTreeNode<*>> {
        return children
    }

    override fun canNavigate(): Boolean {
        return true
    }

    override fun canNavigateToSource(): Boolean {
        return true
    }

    companion object {
        private val pathPattern: Pattern = Pattern.compile("^(-\\d+):(.+)$")
    }
}
