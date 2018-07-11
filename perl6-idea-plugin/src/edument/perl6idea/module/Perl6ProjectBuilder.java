package edument.perl6idea.module;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
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
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.vfs.VfsUtilCore.isEqualOrAncestor;
import static java.io.File.separator;

public class Perl6ProjectBuilder extends ProjectImportBuilder {
    private final Logger LOG = Logger.getInstance(getClass());

    public Perl6ProjectBuilder() {}

    @NotNull
    @Override
    public String getName() {
        return "Perl 6 sources";
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

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof Perl6SdkType;
    }

    @Nullable
    @Override
    public List<Module> commit(Project project,
                               ModifiableModuleModel model,
                               ModulesProvider modulesProvider,
                               ModifiableArtifactModel artifactModel) {
        // XXX This builder could be used when importing project from Project Structure,
        // in this case `model` parameter is not null
        final List<Module> result = new ArrayList<>();
        try {
            WriteAction.run(() -> {
                final LocalFileSystem lfs = LocalFileSystem.getInstance();
                VirtualFile contentRoot = lfs.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(getFileToImport()));
                if (contentRoot == null) return;
                ModifiableModuleModel manager = ModuleManager.getInstance(project).getModifiableModel();
                String name = contentRoot.getPath() + separator + project.getName() + ".iml";
                Module module = manager.newModule(name, Perl6ModuleType.getInstance().getId());
                ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();
                ContentEntry entry = rootModel.addContentEntry(contentRoot);
                addSourceDirectory("lib", contentRoot, entry);
                addSourceDirectory("t", contentRoot, entry);
                manager.commit();
                rootModel.commit();
            });
        }
        catch (Exception e) {
            LOG.info(e);
        }
        return result;
    }

    private static void addSourceDirectory(String name, VirtualFile contentRoot, ContentEntry entry) {
        VirtualFile child = contentRoot.findChild(name);
        if (child != null && isEqualOrAncestor(entry.getUrl(), child.getUrl()))
            entry.addSourceFolder(child, false);
    }
}
