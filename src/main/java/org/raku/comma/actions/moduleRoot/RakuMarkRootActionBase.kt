package org.raku.comma.actions.moduleRoot

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.actions.MarkRootActionBase
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.stubs.StubTreeLoader
import org.raku.comma.module.RakuModuleType

abstract class RakuMarkRootActionBase(private val testMarker: Boolean) : MarkRootActionBase() {
    init {
        val presentation = templatePresentation
        presentation.icon = if (testMarker) AllIcons.Modules.TestRoot else AllIcons.Modules.SourceRoot
        presentation.text = if (testMarker) "Test Sources Root" else "Sources Root"
        presentation.description = "Mark directory as a " + (if (testMarker) "test sources root" else "sources root")
    }

    override fun isEnabled(selection: RootsSelection, module: Module): Boolean {
        if (!ModuleType.`is`(module, RakuModuleType.getInstance())) return false

        for (root in selection.mySelectedRoots) {
            if (testMarker && root.isTestSource()) return false
            if (!testMarker && !root.isTestSource()) return false
        }

        return true
    }

    override fun modifyRoots(file: VirtualFile, entry: ContentEntry) {
        entry.addSourceFolder(file, testMarker)
        StubTreeLoader.getInstance().rebuildStubTree(file)
    }
}
