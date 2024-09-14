package org.raku.comma.actions.moduleRoot;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.actions.MarkRootActionBase;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.stubs.StubTreeLoader;
import org.raku.comma.module.RakuModuleType;
import org.jetbrains.annotations.NotNull;

public abstract class RakuMarkRootActionBase extends MarkRootActionBase {
    private final boolean myTestMarker;

    public RakuMarkRootActionBase(boolean isTest) {
        myTestMarker = isTest;
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(myTestMarker ? AllIcons.Modules.TestRoot : AllIcons.Modules.SourceRoot);
        presentation.setText(myTestMarker ? "Test Sources Root" : "Sources Root");
        presentation.setDescription("Mark directory as a " + (myTestMarker ? "test sources root" : "sources root"));
    }

    @Override
    protected boolean isEnabled(@NotNull RootsSelection selection, @NotNull Module module) {
        if (!ModuleType.is(module, RakuModuleType.getInstance()))
            return false;

        for (SourceFolder root : selection.mySelectedRoots) {
            if (myTestMarker && root.isTestSource())
                return false;
            if (!myTestMarker && !root.isTestSource())
                return false;
        }

        return true;
    }

    @Override
    protected void modifyRoots(VirtualFile file, ContentEntry entry) {
        entry.addSourceFolder(file, myTestMarker);
        StubTreeLoader.getInstance().rebuildStubTree(file);
    }
}
