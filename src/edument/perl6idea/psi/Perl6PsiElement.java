package edument.perl6idea.psi;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.NavigatablePsiElement;

public interface Perl6PsiElement extends NavigatablePsiElement {
    /* Name-manages the enclosing file name into a module name, if possible.
     * Returns null if that's not possible or this doesn't seem to be a module. */
    default String getEnclosingPerl6ModuleName() {
        // Make sure it's a .pm6 file, and trim the extension.
        String path = getContainingFile().getVirtualFile().getPath();
        if (!path.endsWith(".pm6"))
            return null;
        path = path.substring(0, path.length() - 4);

        // Make sure it's within the project and trim the project path
        // off the start.
        String projectPath = getProject().getBasePath();
        if (projectPath == null)
            return null;
        if (!path.startsWith(projectPath))
            return null;
        path = path.substring(projectPath.length() + 1);

        // Mangle it, removing a leading lib:: since lib/ is the standard place
        // for libraries.
        String libraryName = path.replaceAll("[/\\\\]", "::");
        return StringUtil.trimStart(libraryName, "lib::");
    }
}
