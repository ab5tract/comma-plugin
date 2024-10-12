package org.raku.comma.structureView;

import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.NamedLibraryElementNode;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class RakuProjectStructureProvider implements TreeStructureProvider {
    @NotNull
    @Override
    public Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent,
                                                  @NotNull Collection<AbstractTreeNode<?>> children,
                                                  ViewSettings settings)
    {
        ArrayList<AbstractTreeNode<?>> list = new ArrayList<>(children);

//        if (parent instanceof NamedLibraryElementNode) {
//            parent.getChildren().forEach(node -> {
//                if (node instanceof PsiDirectoryNode) {
//                    node.update();
//                }
//            });
//        }

        // Remove .precomp directories
        list.removeIf(f -> {
            if (!(f instanceof ProjectViewNode)) return false;
            VirtualFile file = ((ProjectViewNode<?>) f).getVirtualFile();
            return file != null && file.getName().equals(".precomp");
        });

        for (int i = 0; i < list.size(); i++) {
            AbstractTreeNode<?> node = list.get(i);
            if (node instanceof PsiDirectoryNode) {
                VirtualFile file = ((PsiDirectoryNode) node).getVirtualFile();
                if (file != null && file.getUrl().startsWith("raku:///")) {
                    list.set(i, new RakuLibraryPsiDirectoryNode(parent.getProject(), node, settings));
                }
            }
        }

        return list;
    }
}
