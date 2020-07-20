package edument.perl6idea.metadata;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.Library;
import edument.perl6idea.library.Perl6LibraryType;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6CommandLine;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Perl6ProjectModelSync {
    public static void syncExternalLibraries(Module module, Set<String> firstLevelDeps) {
        if (ApplicationManager.getApplication().isUnitTestMode())
            return;
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() -> ModuleRootModificationUtil.updateModel(module, model -> {
            Set<String> completeMETADependencies = ConcurrentHashMap.newKeySet();
            completeMETADependencies.addAll(firstLevelDeps);
            Sdk sdk = obtainSDKAndGatherLibraryDeps(module, firstLevelDeps, model, completeMETADependencies);
            Map<String, Perl6MetaDataComponent> projectModules = getProjectModules(module);
            Set<String> entriesPresentInMETA = new HashSet<>();

            for (String metaDep : completeMETADependencies) {
                // If local, project module, attach it as dependency
                if (projectModules.containsKey(metaDep)) {
                    Module moduleOfMetaDep = projectModules.get(metaDep).module;
                    if (ModuleRootManager.getInstance(module).isDependsOn(moduleOfMetaDep)) {
                        entriesPresentInMETA.add(moduleOfMetaDep.getName());
                        removeDuplicateEntries(model, moduleOfMetaDep.getName());
                    }
                    else {
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
                        LibraryEx library = (LibraryEx)model.getModuleLibraryTable().createLibrary(metaDep);
                        LibraryEx.ModifiableModelEx libraryModel = library.getModifiableModel();
                        String url = String.format("raku://%d:%s!/", sdk.getName().hashCode(), metaDep);
                        libraryModel.setKind(Perl6LibraryType.LIBRARY_KIND);
                        libraryModel.addRoot(url, OrderRootType.SOURCES);
                        LibraryOrderEntry entry = model.findLibraryOrderEntry(library);
                        assert entry != null : library;
                        entry.setScope(DependencyScope.COMPILE);
                        WriteAction.run(libraryModel::commit);
                    }
                    else {
                        removeDuplicateEntries(model, maybeLibrary.getName());
                    }
                }
            }
            removeOrderEntriesNotInMETA(model, entriesPresentInMETA);
        }));
    }

    @NotNull
    private static Map<String, Perl6MetaDataComponent> getProjectModules(Module module) {
        Module[] modules = ModuleManager.getInstance(module.getProject()).getModules();
        Map<String, Perl6MetaDataComponent> projectModules = new HashMap<>();
        for (Module inProjectModule : modules) {
            if (!module.equals(inProjectModule)) {
                Perl6MetaDataComponent service = inProjectModule.getService(Perl6MetaDataComponent.class);
                if (service != null)
                    projectModules.put(service.getName(), service);
            }
        }
        return projectModules;
    }

    @Nullable
    private static Sdk obtainSDKAndGatherLibraryDeps(Module module,
                                                     Set<String> metaDependencies,
                                                     ModifiableRootModel model,
                                                     Set<String> extendedDeps) {
        Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
        if (sdk == null)
            sdk = ProjectRootManager.getInstance(model.getProject()).getProjectSdk();
        if (sdk != null)
            for (String dep : metaDependencies)
                extendedDeps.addAll(collectDependenciesOfModule(module.getProject(), dep, sdk));
        return sdk;
    }

    private static void removeDuplicateEntries(ModifiableRootModel model, String name) {
        boolean seen = false;
        for (OrderEntry entry : model.getOrderEntries()) {
            if (entry.getPresentableName().equals(name)) {
                if (seen) model.removeOrderEntry(entry);
                else seen = true;
            }
        }
    }

    private static void removeOrderEntriesNotInMETA(ModifiableRootModel model, Set<String> presentInMeta) {
        for (OrderEntry entry : model.getOrderEntries())
            if (entry.isValid() && !entry.getPresentableName().contains("Module source"))
                if (!(presentInMeta.contains(entry.getPresentableName())))
                    model.removeOrderEntry(entry);
    }

    private static List<String> collectDependenciesOfModule(Project project, String metaDep, Sdk sdk) {
        try {
            File locateScript = Perl6Utils.getResourceAsFile("zef/gather-deps.p6");
            if (locateScript == null)
                throw new ExecutionException("Resource bundle is corrupted: locate script is missing");
            Perl6CommandLine depsCollectorScript = new Perl6CommandLine(sdk);
            depsCollectorScript.addParameter(locateScript.getAbsolutePath());
            depsCollectorScript.addParameter(metaDep);
            return depsCollectorScript.executeAndRead();
        }
        catch (ExecutionException e) {
            Perl6SdkType.getInstance().reactToSDKIssue(project, "Cannot use current Raku SDK");
            return new ArrayList<>();
        }
    }
}
