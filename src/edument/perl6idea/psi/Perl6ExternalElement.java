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
}
