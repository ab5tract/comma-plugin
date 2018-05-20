package edument.perl6idea.module;

import com.intellij.ide.util.importProject.ModuleDescriptor;
import com.intellij.ide.util.importProject.ProjectDescriptor;
import com.intellij.ide.util.projectWizard.ExistingModuleLoader;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.importSources.DetectedSourceRoot;
import com.intellij.ide.util.projectWizard.importSources.ProjectStructureDetector;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.*;
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
import java.util.*;

import static java.io.File.separator;

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
                manager.commit();
                result.add(module);
            });
        }
        catch (Exception e) {
            LOG.info(e);
        }
        return result;
    }
}
