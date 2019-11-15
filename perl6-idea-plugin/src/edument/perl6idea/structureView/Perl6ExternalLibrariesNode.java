package edument.perl6idea.structureView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.ExternalLibrariesNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.libraries.Library;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Perl6ExternalLibrariesNode extends ExternalLibrariesNode {
    public Perl6ExternalLibrariesNode(@NotNull Project project,
                                      ViewSettings viewSettings) {
        super(project, viewSettings);
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        if (myProject == null)
            return new ArrayList<>();
        List<AbstractTreeNode> children = new ArrayList<>();

        Module[] modules = ModuleManager.getInstance(myProject).getModules();
        for (Module module : modules) {
            final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            final OrderEntry[] orderEntries = moduleRootManager.getOrderEntries();
            for (final OrderEntry orderEntry : orderEntries) {
                if (orderEntry instanceof LibraryOrderEntry) {
                    final LibraryOrderEntry libraryOrderEntry = (LibraryOrderEntry)orderEntry;
                    final Library library = libraryOrderEntry.getLibrary();
                    if (library == null) continue;
                    children.add(new Perl6LibraryNode(myProject, library, getSettings()));
                }
            }
        }

        return children;
    }
}
