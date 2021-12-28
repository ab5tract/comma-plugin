package edument.perl6idea.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectOpenProcessor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class Perl6ProjectOpenProcessor extends ProjectOpenProcessor {
    @Override
    public @NotNull @Nls String getName() {
        return "Raku";
    }

    @Override
    public boolean canOpenProject(@NotNull VirtualFile file) {
        if (file.isDirectory()) {
            if (file.findChild("META6.json") != null) {
                return true;
            }
            if (file.findChild("META.info") != null) {
                return true;
            }
        }
        String fileName = file.getName();
        return fileName.equals("META6.json") || fileName.equals("META.info");
    }

    @Override
    public @Nullable Project doOpenProject(@NotNull VirtualFile virtualFile,
                                           @Nullable Project projectToClose,
                                           boolean forceOpenInNewFrame) {
        VirtualFile projectDirectory = virtualFile.isDirectory() ? virtualFile : virtualFile.getParent();
        Path nioPath = projectDirectory.toNioPath();
        Project newProject;
        String name = nioPath.getFileName().toString();
        Perl6ProjectBuilder projectBuilder = new Perl6ProjectBuilder(null);
        projectBuilder.setFileToImport(nioPath.toString());
        newProject = projectBuilder.createProject(name, nioPath.toString());
        if (newProject == null) {
            return null;
        } else {
            projectBuilder.commit(newProject, null, ModulesProvider.EMPTY_MODULES_PROVIDER);
            ProjectManagerEx.getInstanceEx().openProject(newProject);
            return newProject;
        }
    }

    @Override
    public boolean isStrongProjectInfoHolder() {
        return true;
    }
}
