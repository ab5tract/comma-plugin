package edument.perl6idea.psi.stub;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.testFramework.LightVirtualFile;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.stub.impl.Perl6FileStubImpl;
import edument.perl6idea.vfs.Perl6FileSystem;
import org.jetbrains.annotations.NotNull;

public class Perl6FileStubBuilder extends DefaultStubBuilder {
    @NotNull
    @Override
    protected StubElement createStubForFile(@NotNull PsiFile file) {
        if (file instanceof Perl6File && ((Perl6File)file).isReal())
            return new Perl6FileStubImpl((Perl6File)file, generateCompilationUnitName(file), ((Perl6File)file).getStatementLineMap());
        else
            return super.createStubForFile(file);
    }

    private static String generateCompilationUnitName(PsiFile file) {
        VirtualFile vf = file.getViewProvider().getVirtualFile();

        if (vf instanceof LightVirtualFile) {
            vf = ((LightVirtualFile)vf).getOriginalFile();
            if (vf == null)
                return null;
            if (vf.getFileSystem() instanceof Perl6FileSystem) {
                return vf.getNameWithoutExtension();
            }
        }

        String filePath = vf.getPath();
        if (FileTypeManager.getInstance().getFileTypeByFile(vf) instanceof Perl6ModuleFileType) {
            Module parentModule = ModuleUtilCore.findModuleForFile(vf, file.getProject());
            if (parentModule == null)
                return null;

            ContentEntry[] entries = ModuleRootManager.getInstance(parentModule).getContentEntries();
            if (entries.length != 1)
                return null;

            VirtualFile moduleDirectory = entries[0].getFile();
            if (moduleDirectory == null)
                return null;

            if (filePath.startsWith(moduleDirectory.getPath())) {
                String relPath = filePath.substring(moduleDirectory.getPath().length() + 1);
                if (relPath.startsWith("lib/") || relPath.startsWith("lib\\"))
                    relPath = relPath.substring(4);
                String[] parts = relPath.split("/|\\\\");
                int lastDot = parts[parts.length - 1].lastIndexOf('.');
                if (lastDot > 0)
                    parts[parts.length - 1] = parts[parts.length - 1].substring(0, lastDot);
                return String.join("::", parts);
            }
        }

        // Not a module, outside of project, or otherwise odd.
        return null;
    }
}
