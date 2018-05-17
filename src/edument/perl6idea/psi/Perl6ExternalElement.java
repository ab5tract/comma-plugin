package edument.perl6idea.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import edument.perl6idea.component.Perl6NameCache;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Perl6ExternalElement extends ASTWrapperPsiElement implements Perl6PsiElement {
    public Perl6ExternalElement(@NotNull ASTNode node) {
        super(node);
    }

    List<Perl6PsiElement> getLocalDeclarations(Project project) {
        if (getModuleName() == null) return new ArrayList<>();
        VirtualFile basePath = project.getBaseDir().findChild("lib");
        if (basePath == null) return new ArrayList<>();
        String path = getModuleName().replaceAll("::", File.separator) + ".pm6";
        VirtualFile virtualFile = basePath.findFileByRelativePath(path);
        if (virtualFile == null) return new ArrayList<>();
        Perl6File file = (Perl6File) PsiManager.getInstance(project).findFile(virtualFile);
        if (file != null) return file.getDeclarations();
        return new ArrayList<>();
    }

    List<String> getExternallyDeclaredNames(Project project) {
        String name = getModuleName();
        if (name == null) return new ArrayList<>();
        return new ArrayList<>(project.getComponent(Perl6NameCache.class).getNames(project, name));
    }

    String getModuleName() {
        PsiElement[] children = getChildren();
        for (PsiElement el : children) {
            if (el instanceof Perl6ModuleName)
                return el.getText();
        }
        return null;
    }

    @Override
    public PsiReference getReference() {
        return new Perl6ExternalReference(this);
    }
}
