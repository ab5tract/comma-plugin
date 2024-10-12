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
import kotlin.collections.ArrayList

class RakuLibraryPsiDirectoryNode(
    project: Project,
    val node: AbstractTreeNode<*>,
    settings: ViewSettings
) : PsiDirectoryNode(project, (node.value as PsiDirectory), settings) {

    private var children: MutableList<AbstractTreeNode<*>>? = null
    private var nameTag: String = "<Loading Module Sources...>"

    override fun update(data: PresentationData) {
        data.presentableText =  if (children == null)
                                    "<Loading Module Sources...>"
                                else if (children!!.isEmpty())
                                        "<Module Source Unavailable>"
                                     else "Provides"

        data.setIcon(PlatformIcons.FOLDER_ICON)
        if (children == null) {
            getChildrenImpl()
        }

        if (children?.isEmpty() == true) {
            data.tooltip = "No source found. Is the dependency actually installed?"
        }
    }

    override fun getChildrenImpl(): Collection<AbstractTreeNode<*>>? {
        val dependencyService = project.service<RakuDependencyDetailsService>()

        if (dependencyService.preloadFinished.isDone && children == null) {
            val path = Objects.requireNonNull(Objects.requireNonNull(virtualFile)!!.path)
            val pathPart = path.substring(1, path.length - 2)
            val matcher = pathPattern.matcher(pathPart)
            children = ArrayList()

            if (matcher.matches()) {
                val name = matcher.group(2)
                if (name != null) {
                    project.service<RakuDependencyDetailsService>()
                        .moduleToRakuFiles(name)
                        .forEach(Consumer { rakuFile: RakuFile? ->
                            children!!.add(RakuLibraryFileEntryNode(project, rakuFile!!, settings))
                        })
                }
            }
            nameTag = if (children!!.isEmpty()) "<Module Source Unavailable>" else "Provides"
            presentation.presentableText = nameTag
            presentation.isChanged = true
            update(presentation)
        }

        return children
    }

    override fun canNavigate(): Boolean = children != null
    override fun canNavigateToSource(): Boolean = true

    companion object {
        private val pathPattern: Pattern = Pattern.compile("^(-\\d+):(.+)$")
    }
}
