package org.raku.comma.project;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;

import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.comma.RakuIcons;
import org.raku.comma.metadata.RakuMetaDataComponent;
import org.raku.comma.module.RakuModuleType;
import org.raku.comma.pm.RakuPackageManagerManager;
import org.raku.comma.sdk.RakuSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.raku.comma.services.RakuBackupSDKService;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.intellij.openapi.vfs.VfsUtilCore.isEqualOrAncestor;

public class RakuProjectBuilder extends ProjectBuilder {
    private final WizardContext myContext;
    private boolean myUpdate;
    private String myFileToImport;
    private final Logger LOG = Logger.getInstance(getClass());

    public RakuProjectBuilder(@Nullable WizardContext context) {
        myContext = context;
    }

    @NotNull
    public String getName() {
        return "Raku sources";
    }

    public Icon getIcon() {
        return RakuIcons.CAMELIA;
    }

    public String getFileToImport() {
        return myFileToImport;
    }

    public void setFileToImport(String fileToImport) {
        myFileToImport = fileToImport;
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof RakuSdkType;
    }

    @Nullable
    @Override
    public List<Module> commit(@NotNull Project project,
                               ModifiableModuleModel model,
                               ModulesProvider modulesProvider)
    {
        // XXX This builder could be used when importing project from Project Structure,
        // in this case `model` parameter is not null
        final List<Module> result = new ArrayList<>();

        try {
            WriteAction.runAndWait(() -> {
                final LocalFileSystem lfs = LocalFileSystem.getInstance();
                String metaParentDirectory = Paths.get(getFileToImport()).toString();
                String path = FileUtil.toSystemIndependentName(metaParentDirectory);
                VirtualFile contentRoot = lfs.findFileByPath(path.substring(5));
                if (contentRoot == null) return;

                String projectFilePath = project.getProjectFilePath();
                if (projectFilePath == null) return;

                ModifiableModuleModel modelToPatch = model != null
                                                     ? model
                                                     : ModuleManager.getInstance(project).getModifiableModel();

                Module module = modelToPatch.newModule(projectFilePath, RakuModuleType.getInstance().getId());
                result.add(module);

                ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();
                ContentEntry entry = rootModel.addContentEntry(contentRoot);
                addSourceDirectory("lib", contentRoot, entry, false);
                addSourceDirectory("bin", contentRoot, entry, false);
                addSourceDirectory("t", contentRoot, entry, true);
                modelToPatch.commit();
                rootModel.commit();

                final PropertiesComponent properties = PropertiesComponent.getInstance(project);
                final String selectedJdkProperty = "raku.sdk.selected";
                String sdkHome = properties.getValue(selectedJdkProperty);
                if (sdkHome != null) {
                    Sdk sdk = ProjectJdkTable.getInstance().findJdk(sdkHome);
                    if (sdk != null) {
                        var service = project.getService(RakuBackupSDKService.class);
                        service.setProjectSdkPath(project, sdkHome);
                    } else {
                        throw new RuntimeException("Can't find Raku SDK for: " + sdkHome);
                    }
                }
                Path metaPath = Paths.get(getFileToImport(), "META6.json");
                if (!metaPath.toFile().exists()) {
                    metaPath = Paths.get(getFileToImport(), "META.list");
                }
                VirtualFile metaFile = lfs.findFileByPath(metaPath.toString());
                if (metaFile != null) {
                    Module firstModule = ModuleUtilCore.findModuleForFile(metaFile, project);
                    if (firstModule == null) return;
                    RakuMetaDataComponent component = firstModule.getService(RakuMetaDataComponent.class);
                    component.triggerMetaBuild(metaFile);
                }

                // Detect and set PM from path
                RakuPackageManagerManager manager = project.getService(RakuPackageManagerManager.class);
                if (manager != null) {
                    List<RakuPackageManagerManager.SuggestedItem> list = new ArrayList<>();
                    RakuPackageManagerManager.detectPMs(list);
                    if (!list.isEmpty()) {
                        manager.setPM(list.getFirst().toPM());
                    }
                }
            });
        } catch (Exception e) {
            LOG.info(e);
        }
        return result;
    }

    private static void addSourceDirectory(String name, VirtualFile contentRoot, ContentEntry entry, boolean isTest) {
        VirtualFile child = contentRoot.findChild(name);
        if (child != null && isEqualOrAncestor(entry.getUrl(), child.getUrl())) {
            entry.addSourceFolder(child, isTest);
        }
    }

    @Override
    public boolean isUpdate() {
        return myUpdate;
    }

    public void setUpdate(final boolean update) {
        myUpdate = update;
    }
}
