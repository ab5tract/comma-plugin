package edument.perl6idea.module;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packaging.artifacts.ModifiableArtifactModel;
import com.intellij.projectImport.ProjectImportBuilder;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.vfs.VfsUtilCore.isEqualOrAncestor;

public class Perl6ProjectBuilder extends ProjectImportBuilder {
    private final Logger LOG = Logger.getInstance(getClass());

    public Perl6ProjectBuilder() {}

    @NotNull
    @Override
    public String getName() {
        return "Perl 6 Module";
    }

    @Override
    public Icon getIcon() {
        return Perl6Icons.CAMELIA;
    }

    @Override
    public List getList() {
        return null;
    }

    @Override
    public boolean isMarked(Object element) {
        return false;
    }

    @Override
    public void setList(List list) throws ConfigurationException {

    }

    @Override
    public void setOpenProjectSettingsAfter(boolean on) {

    }

    @Nullable
    @Override
    public List<Module> commit(Project project, // Project
                               ModifiableModuleModel model, // null
                               ModulesProvider modulesProvider, // ModulesProvider
                               ModifiableArtifactModel artifactModel) { // null
        // XXX This builder could be used when importing project from Project Structure,
        // in this case `model` parameter is not null
        final List<Module> result = new ArrayList<>();
        try {
            WriteAction.run(() -> {
                final LocalFileSystem lfs = LocalFileSystem.getInstance();
                VirtualFile contentRoot = lfs.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(getFileToImport()));
                if (contentRoot == null) return;
                ModifiableModuleModel manager = ModuleManager.getInstance(project).getModifiableModel();
                Module module = manager.newModule(getFileToImport(), Perl6ModuleType.getInstance().getId());

                ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();
                File directory = new File(contentRoot.findChild("lib").getPath());
                if (!directory.exists())
                    directory.mkdirs();
                VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(directory);
                ContentEntry e = getContentRootFor(virtualFile, rootModel);
                if (virtualFile == null) return;
                if (e == null) return;
                e.addSourceFolder(virtualFile, false);
                rootModel.commit();
                result.add(module);
                manager.commit();
            });
        }
        catch (Exception e) {
            LOG.info(e);
        }
        return result;
    }

    private static ContentEntry getContentRootFor(VirtualFile file, ModifiableRootModel model) {
        for (ContentEntry e : model.getContentEntries()) {
            if (isEqualOrAncestor(file.getUrl(), e.getUrl())) return e;
        }
        return null;
    }
}
