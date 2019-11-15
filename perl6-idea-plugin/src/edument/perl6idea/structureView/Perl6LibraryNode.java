package edument.perl6idea.structureView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.PlatformIcons;
import edument.perl6idea.vfs.Perl6FileSystem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Perl6LibraryNode extends AbstractTreeNode<Library> {
    private final Library myLibrary;
    private final ViewSettings myViewSettings;

    protected Perl6LibraryNode(@NotNull Project project, Library library, ViewSettings viewSettings) {
        super(project, library);
        myLibrary = library;
        myViewSettings = viewSettings;
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        List<AbstractTreeNode> providesSection = new ArrayList<>();
        PsiManager instance = PsiManager.getInstance(myProject);

        Perl6FileSystem vfs = Perl6FileSystem.getInstance();

        for (String libraryURL : myLibrary.getUrls(OrderRootType.SOURCES)) {
            for (VirtualFile compUnitFile : vfs.findFilesByPath(myProject, libraryURL)) {
                PsiFile psiFile = instance.findFile(compUnitFile);
                if (psiFile == null)
                    System.out.println("Psi file for " + compUnitFile.getName() + " returns null...");
                else
                    providesSection.add(new PsiFileNode(myProject, psiFile, myViewSettings));
            }
        }
        return providesSection;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(myLibrary.getName());
        presentation.setIcon(PlatformIcons.LIBRARY_ICON);
    }
}
