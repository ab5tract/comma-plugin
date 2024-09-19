package org.raku.comma.metadata;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.serviceContainer.AlreadyDisposedException;
import org.raku.comma.library.RakuLibraryType;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.services.RakuBackupSDKService;
import org.raku.comma.utils.RakuCommandLine;
import org.raku.comma.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public final class RakuProjectModelSync {
    public static void syncExternalLibraries(Module module, Set<String> firstLevelDeps) {
        if (ApplicationManager.getApplication().isUnitTestMode())
            return;
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() -> {
            try {
                ModuleRootModificationUtil.updateModel(module, model -> {
                    Set<String> completeMETADependencies = ConcurrentHashMap.newKeySet();
                    completeMETADependencies.addAll(firstLevelDeps);
                    Sdk sdk = obtainSDKAndGatherLibraryDeps(module, firstLevelDeps, completeMETADependencies);
                    Map<String, RakuMetaDataComponent> projectModules = getProjectModules(module);
                    Set<String> entriesPresentInMETA = new HashSet<>();

                    for (String metaDep : completeMETADependencies) {
                        // If local, project module, attach it as dependency
                        if (projectModules.containsKey(metaDep)) {
                            Module moduleOfMetaDep = projectModules.get(metaDep).getModule();
                            if (moduleOfMetaDep != null && ModuleRootManager.getInstance(module).isDependsOn(moduleOfMetaDep)) {
                                entriesPresentInMETA.add(moduleOfMetaDep.getName());
                                removeDuplicateEntries(model, moduleOfMetaDep.getName());
                            } else if (moduleOfMetaDep != null) {
                                OrderEntry entry = model.addModuleOrderEntry(moduleOfMetaDep);
                                entriesPresentInMETA.add(entry.getPresentableName());
                            }
                        }
                        // If external library, attach as library
                        else {
                            if (sdk == null) continue;
                            Library maybeLibrary = model.getModuleLibraryTable().getLibraryByName(metaDep);
                            entriesPresentInMETA.add(metaDep);
                            if (maybeLibrary == null) {
                                // otherwise create and mark
                                LibraryEx library = (LibraryEx) model.getModuleLibraryTable().createLibrary(metaDep);
                                LibraryEx.ModifiableModelEx libraryModel = library.getModifiableModel();
                                String url = String.format("raku://%d:%s!/", sdk.getName().hashCode(), metaDep);
                                libraryModel.setKind(RakuLibraryType.LIBRARY_KIND);
                                libraryModel.addRoot(url, OrderRootType.SOURCES);
                                LibraryOrderEntry entry = model.findLibraryOrderEntry(library);
                                assert entry != null : library;
                                entry.setScope(DependencyScope.COMPILE);
                                WriteAction.run(libraryModel::commit);
                            } else {
                                removeDuplicateEntries(model, maybeLibrary.getName());
                            }
                        }
                    }
                    removeOrderEntriesNotInMETA(model, entriesPresentInMETA);
                });
            } catch (AlreadyDisposedException ignore) {
                // If the application or the project was closed while we did not finish the job,
                // just ignore it hoping we will be more lucky next time: this sync
                // algorithm should be robust enough not to leave any incurable "leftovers"
                // and if it is so it's better to fix the underlying reasons.
            }
        });
    }

    @NotNull
    private static Map<String, RakuMetaDataComponent> getProjectModules(Module module) {
        Module[] modules = ModuleManager.getInstance(module.getProject()).getModules();
        Map<String, RakuMetaDataComponent> projectModules = new HashMap<>();
        for (Module inProjectModule : modules) {
            if (!module.equals(inProjectModule)) {
                RakuMetaDataComponent service = inProjectModule.getService(RakuMetaDataComponent.class);
                if (service != null)
                    projectModules.put(service.getName(), service);
            }
        }
        return projectModules;
    }

    @Nullable
    private static Sdk obtainSDKAndGatherLibraryDeps(Module module,
                                                     Set<String> metaDependencies,
                                                     Set<String> extendedDeps) {
        Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
        if (sdk == null || !(sdk.getSdkType() instanceof RakuSdkType)) {
            sdk = ProjectRootManager.getInstance(module.getProject()).getProjectSdk();
        }
        if (sdk == null || !(sdk.getSdkType() instanceof RakuSdkType)) {
            RakuBackupSDKService backupSDK = module.getProject().getService(RakuBackupSDKService.class);
            String homePath = backupSDK.getProjectSdkPath(module.getProject().getProjectFilePath());

            if (homePath == null) return null;
            for (Sdk tempSdk : ProjectJdkTable.getInstance().getSdksOfType(RakuSdkType.getInstance())) {
                if (Objects.equals(tempSdk.getHomePath(), homePath)) {
                    sdk = tempSdk;
                    break;
                }
            }
        }
        if (sdk != null) {
            for (String dep : metaDependencies) {
                extendedDeps.addAll(collectDependenciesOfModule(module.getProject(), dep, sdk));
            }
        }
        return sdk;
    }

    private static void removeDuplicateEntries(ModifiableRootModel model, String name) {
        boolean seen = false;
        for (OrderEntry entry : model.getOrderEntries()) {
            if (entry.getPresentableName().equals(name)) {
                if (seen) {
                    model.removeOrderEntry(entry);
                } else {
                    seen = true;
                }
            }
        }
    }

    private static void removeOrderEntriesNotInMETA(ModifiableRootModel model, Set<String> presentInMeta) {
        for (OrderEntry entry : model.getOrderEntries()) {
            if (entry.isValid() && !entry.getPresentableName().contains("Module source")) {
                if (!(presentInMeta.contains(entry.getPresentableName()))) {
                    model.removeOrderEntry(entry);
                }
            }
        }
    }

    private static List<String> collectDependenciesOfModule(Project project, String metaDep, Sdk sdk) {
        try {
            File locateScript = RakuUtils.getResourceAsFile("zef/gather-deps.p6");
            if (locateScript == null)
                throw new ExecutionException("Resource bundle is corrupted: locate script is missing");
            RakuCommandLine depsCollectorScript = new RakuCommandLine(sdk);
            depsCollectorScript.addParameter(locateScript.getAbsolutePath());
            depsCollectorScript.addParameter(metaDep);
            return depsCollectorScript.executeAndRead(locateScript);
        } catch (ExecutionException e) {
            RakuSdkType.getInstance().reactToSDKIssue(project, "Cannot use current Raku SDK");
            return new ArrayList<>();
        }
    }
}
