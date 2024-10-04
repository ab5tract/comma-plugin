package org.raku.comma.structureView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.raku.comma.services.project.RakuDependencyDetailsService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RakuLibraryPsiDirectoryNode extends PsiDirectoryNode {
    private static final Pattern pathPattern = Pattern.compile("^(-\\d+):(.+)$");

    private final List<AbstractTreeNode<?>> children = new ArrayList<>();
    private final String presentableText;

    public RakuLibraryPsiDirectoryNode(Project project, AbstractTreeNode<?> node, ViewSettings settings) {
        super(project, (PsiDirectory) node.getValue(), settings);

        var path = Objects.requireNonNull(Objects.requireNonNull(((PsiDirectoryNode) node).getVirtualFile()).getPath());
        var pathPart = path.substring(1, path.length() - 2);
        Matcher matcher = pathPattern.matcher(pathPart);

        if (matcher.matches()) {
            var name = matcher.group(2);
            if (name != null) {
                project.getService(RakuDependencyDetailsService.class)
                       .moduleToRakuFiles(name)
                       .forEach(rakuFile -> children.add(new RakuLibraryFileEntryNode(project, rakuFile, settings)));
            }
        }

        presentableText = children.isEmpty() ? "<Module Source Unavailable>" : "Provides";
    }

    @Override
    public void update(@NotNull PresentationData data) {
        data.setPresentableText(presentableText);
        data.setIcon(PlatformIcons.FOLDER_ICON);
        if (children.isEmpty()) {
            data.setTooltip("No source found. Is the dependency actually installed?");
        }
    }

    @Override
    public Collection<AbstractTreeNode<?>> getChildrenImpl() {
        return children;
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }
}
