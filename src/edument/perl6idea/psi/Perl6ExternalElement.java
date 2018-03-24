package edument.perl6idea.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.component.Perl6NameCache;

import java.util.ArrayList;
import java.util.List;

public interface Perl6ExternalElement extends Perl6PsiElement {
    default List<String> getDeclarations(Project project) {
        String name = getModuleName();
        if (name == null) return new ArrayList<>();
        return new ArrayList<>(project.getComponent(Perl6NameCache.class).getNames(name));
    }

    default String getModuleName() {
        PsiElement[] children = getChildren();
        for (PsiElement el : children) {
            if (el instanceof Perl6LongName)
                return el.getText();
        }
        return null;
    }
}
