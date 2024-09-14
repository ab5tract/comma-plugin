package org.raku.comma.psi.stub;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.testFramework.LightVirtualFile;
import org.raku.comma.filetypes.RakuModuleFileType;
import org.raku.comma.psi.RakuFile;
import org.raku.comma.psi.stub.impl.RakuFileStubImpl;
import org.raku.comma.vfs.RakuFileSystem;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystems;

public class RakuFileStubBuilder extends DefaultStubBuilder {
    @NotNull
    @Override
    protected StubElement<?> createStubForFile(@NotNull PsiFile file) {
        if (file instanceof RakuFile && ((RakuFile)file).isReal()) {
            return new RakuFileStubImpl((RakuFile)file, generateCompilationUnitName(file));
        }
        else
            return super.createStubForFile(file);
    }

    private static String generateCompilationUnitName(PsiFile file) {
        VirtualFile vf = file.getViewProvider().getVirtualFile();

        if (vf instanceof LightVirtualFile) {
            vf = ((LightVirtualFile)vf).getOriginalFile();
            if (vf == null)
                return null;
            if (vf.getFileSystem() instanceof RakuFileSystem) {
                return vf.getNameWithoutExtension();
            }
        }

        String filePath = vf.getPath();
        if (FileTypeManager.getInstance().getFileTypeByFile(vf) instanceof RakuModuleFileType) {
            Module parentModule = ModuleUtilCore.findModuleForFile(vf, file.getProject());
            if (parentModule == null)
                return null;

            VirtualFile[] entries = ModuleRootManager.getInstance(parentModule).getSourceRoots();
            for (VirtualFile sourceRoot : entries) {
                if (filePath.startsWith(sourceRoot.getPath() + FileSystems.getDefault().getSeparator())) {
                    String relPath = sourceRoot.toNioPath().relativize(vf.toNioPath()).toString();
                    String[] parts = relPath.split("[/\\\\]");
                    int lastDot = parts[parts.length - 1].lastIndexOf('.');
                    if (lastDot > 0)
                        parts[parts.length - 1] = parts[parts.length - 1].substring(0, lastDot);
                    return String.join("::", parts);
                }
            }
        }

        // Not a module, outside of project, or otherwise odd.
        return null;
    }
}
