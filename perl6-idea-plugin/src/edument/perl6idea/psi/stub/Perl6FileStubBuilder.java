package edument.perl6idea.psi.stub;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.testFramework.LightVirtualFile;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.stub.impl.Perl6FileStubImpl;
import org.jetbrains.annotations.NotNull;

public class Perl6FileStubBuilder extends DefaultStubBuilder {
    @NotNull
    @Override
    protected StubElement createStubForFile(@NotNull PsiFile file) {
        if (file instanceof Perl6File)
            return new Perl6FileStubImpl((Perl6File)file, generateCompilationUnitName(file));
        else
            return super.createStubForFile(file);
    }

    private static String generateCompilationUnitName(PsiFile file) {
        VirtualFile vf = file.getViewProvider().getVirtualFile();
        if (vf instanceof LightVirtualFile)
            vf = ((LightVirtualFile)vf).getOriginalFile();
        if (vf != null) {
            String filePath = vf.getPath();
            if (filePath.endsWith(".pm6")) {
                String basePath = file.getProject().getBaseDir().getPath();
                if (filePath.startsWith(basePath)) {
                    String relPath = filePath.substring(basePath.length() + 1);
                    if (relPath.startsWith("lib/") || relPath.startsWith("lib\\"))
                        relPath = relPath.substring(4);
                    String[] parts = relPath.split("/|\\\\");
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
