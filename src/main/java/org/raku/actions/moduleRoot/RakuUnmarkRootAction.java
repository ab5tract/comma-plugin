package org.raku.actions.moduleRoot;

import com.intellij.ide.projectView.actions.UnmarkRootAction;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.stubs.StubTreeLoader;
import org.jetbrains.annotations.NotNull;

public class RakuUnmarkRootAction extends UnmarkRootAction {
    @Override
    protected void modifyRoots(@NotNull VirtualFile file, @NotNull ContentEntry entry) {
        super.modifyRoots(file, entry);
        StubTreeLoader.getInstance().rebuildStubTree(file);
    }
}
