package edument.perl6idea.structureView;

import com.intellij.ide.favoritesTreeView.FavoritesRootNode;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class Perl6ProjectStructureProvider implements TreeStructureProvider {
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                               @NotNull Collection<AbstractTreeNode> children,
                                               ViewSettings settings) {
        if (parent instanceof FavoritesRootNode)
            return children;
        ArrayList<AbstractTreeNode> list = new ArrayList<>(children);

        // Remove .precomp directories
        list.removeIf(f -> {
            if (!(f instanceof ProjectViewNode)) return false;
            VirtualFile file = ((ProjectViewNode) f).getVirtualFile();
            return file != null && file.getName().equals(".precomp");
        });
        return list;
    }
}
