package edument.perl6idea.structureView;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.ExternalLibrariesNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PlatformIcons;
import edument.perl6idea.module.Perl6ModuleBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
        Collection c = new ArrayList<>();
        if (myProject == null) return c;
        Path metaPath = Perl6ModuleBuilder.getMETAFilePath(myProject);
        if (metaPath == null) return c;
        JSONObject meta = null;
        try {
            String jsonString = new String(Files.readAllBytes(metaPath), StandardCharsets.UTF_8);
            meta = new JSONObject(jsonString);
        } catch (IOException ignored) {
        }
        if (meta == null)
            return c;
        List<String> names = new ArrayList<>();
        if (meta.has("depends")) {
            JSONArray providesSection = (JSONArray)meta.get("depends");
            for (Object name : providesSection) {
                names.add((String)name);
            }
        }

        for (String name : names)
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
        return c;
    }
}
