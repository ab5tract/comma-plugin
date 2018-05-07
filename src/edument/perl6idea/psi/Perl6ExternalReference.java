package edument.perl6idea.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Perl6ExternalReference extends PsiReferenceBase<Perl6ExternalElement> {
    public Perl6ExternalReference(Perl6ExternalElement perl6ExternalElement) {
        super(perl6ExternalElement, new TextRange(0, perl6ExternalElement.getTextLength()));
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        String name = getElement().getModuleName();
        Project project = getElement().getProject();
        Path path = Paths.get(project.getBaseDir().getPath(),
                "lib", name.replaceAll("::", File.separator));
        path = path.resolveSibling(path.getFileName() + ".pm6");
        if (path == null) return null;
        VirtualFile vfile = LocalFileSystem.getInstance().findFileByPath(path.toString());
        if (vfile == null) return null;
        return PsiManager.getInstance(project).findFile(vfile);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
