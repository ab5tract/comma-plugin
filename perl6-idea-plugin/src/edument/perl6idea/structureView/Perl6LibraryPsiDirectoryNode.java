package edument.perl6idea.structureView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;

public class Perl6LibraryPsiDirectoryNode extends PsiDirectoryNode {
    public Perl6LibraryPsiDirectoryNode(Project project, AbstractTreeNode node, ViewSettings settings) {
        super(project, (PsiDirectory)node.getValue(), settings);
    }

    @Override
    public void update(@NotNull PresentationData data) {
        data.setPresentableText("provides");
        data.setIcon(PlatformIcons.FOLDER_ICON);
    }
}
