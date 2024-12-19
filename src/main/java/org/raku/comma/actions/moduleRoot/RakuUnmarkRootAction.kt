package org.raku.comma.actions.moduleRoot

import com.intellij.ide.projectView.actions.UnmarkRootAction
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.stubs.StubTreeLoader

class RakuUnmarkRootAction : UnmarkRootAction() {
    override fun modifyRoots(file: VirtualFile, entry: ContentEntry) {
        super.modifyRoots(file, entry)
        StubTreeLoader.getInstance().rebuildStubTree(file)
    }
}
