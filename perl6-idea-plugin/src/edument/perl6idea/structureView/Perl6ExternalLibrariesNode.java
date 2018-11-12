package edument.perl6idea.structureView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.ExternalLibrariesNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.PlatformIcons;
import edument.perl6idea.module.Perl6MetaDataComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

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

        Collection<AbstractTreeNode<String>> c = new ArrayList<>();
        Module[] projectModules = ModuleManager.getInstance(myProject).getModules();
        for (Module module : projectModules) {
            Perl6MetaDataComponent metaData = module.getComponent(Perl6MetaDataComponent.class);
            metaData.getDepends().forEach(name -> addName(name, c));
            metaData.getBuildDepends().forEach(name -> addName(name, c));
            metaData.getTestDepends().forEach(name -> addName(name, c));
        }
        return c;
    }

    private void addName(String name, Collection<AbstractTreeNode<String>> c) {
        c.add(new AbstractTreeNode<String>(myProject, name) {
            @Override
            protected void update(PresentationData presentation) {
                presentation.setPresentableText(name);
                presentation.setIcon(PlatformIcons.LIBRARY_ICON);
            }

            @NotNull
            @Override
            public Collection<? extends AbstractTreeNode> getChildren() {
                return new ArrayList<>();
            }
        });
    }
}
