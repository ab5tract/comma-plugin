package edument.perl6idea.project.structure;

import com.intellij.facet.FacetModel;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.roots.impl.ModifiableModelCommitter;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.roots.ui.configuration.actions.ModuleDeleteProvider;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.actions.comma.ImportModuleAction;
import edument.perl6idea.project.projectWizard.CommaAbstractProjectWizard;
import edument.perl6idea.project.projectWizard.CommaNewProjectWizard;
import edument.perl6idea.project.structure.ui.HeaderHidingTabbedModuleEditor;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;

public class Perl6ModulesConfigurator implements ModulesProvider, ModuleEditor.ChangeListener {
    private static final Logger LOG = Logger.getInstance(Perl6ModulesConfigurator.class);
    private final Project myProject;
    private ModifiableModuleModel myModuleModel;
    private Perl6StructureConfigurableContext myContext;
    private boolean myModified = false;
    private boolean myModuleModelCommitted = false;

    private final Map<Module, ModuleEditor> myModuleEditors = new TreeMap<>((o1, o2) -> {
        String n1 = o1.getName();
        String n2 = o2.getName();
        int result = n1.compareToIgnoreCase(n2);
        if (result != 0) {
            return result;
        }
        return n1.compareTo(n2);
    });

    public Perl6ModulesConfigurator(Project project) {
        myProject = project;
        myModuleModel = ModuleManager.getInstance(myProject).getModifiableModel();
    }

    @Override
    public void moduleStateChanged(ModifiableRootModel moduleRootModel) {

    }

    @Override
    public @Nullable Module getModule(@NotNull String name) {
        final Module moduleByName = myModuleModel.findModuleByName(name);
        return moduleByName != null ? moduleByName : myModuleModel.getModuleToBeRenamed(name);
    }

    @Override
    public @NotNull FacetModel getFacetModel(@NotNull Module module) {
        return null;
    }

    @Override
    public Module @NotNull [] getModules() {
        return myModuleModel.getModules();
    }

    @Override
    public ModuleRootModel getRootModel(@NotNull Module module) {
        return getOrCreateModuleEditor(module).getRootModel();
    }

    @NotNull
    public ModuleEditor getOrCreateModuleEditor(@NotNull Module module) {
        LOG.assertTrue(getModule(module.getName()) != null, "Module has been deleted");
        ModuleEditor editor = getModuleEditor(module);
        if (editor == null) {
            editor = doCreateModuleEditor(module);
        }
        return editor;
    }

    @NotNull
    private ModuleEditor doCreateModuleEditor(@NotNull Module module) {
        final ModuleEditor moduleEditor = new HeaderHidingTabbedModuleEditor(myProject, this, module);

        myModuleEditors.put(moduleEditor.getModule(), moduleEditor);

        moduleEditor.addChangeListener(this);
        Disposer.register(moduleEditor, new Disposable() {
            @Override
            public void dispose() {
                moduleEditor.removeChangeListener(Perl6ModulesConfigurator.this);
            }
        });
        return moduleEditor;
    }

    public void resetModuleEditors() {
        myModuleModel = ModuleManager.getInstance(myProject).getModifiableModel();
        ApplicationManager.getApplication().runWriteAction(() -> {
            if (!myModuleEditors.isEmpty()) {
                LOG.error("module editors was not disposed");
                myModuleEditors.clear();
            }
            for (Module module : myModuleModel.getModules()) {
                getOrCreateModuleEditor(module);
            }
        });
        myModified = false;
    }

    public List<Module> addModule(Component parent, boolean anImport, String defaultName) {
        if (myProject.isDefault()) return null;
        ProjectBuilder builder = runModuleWizard(parent, anImport, defaultName);
        if (builder != null) {
            List<Module> modules = new ArrayList<>();
            List<Module> committedModules;
            committedModules = builder.commit(myProject, myModuleModel, this);
            myModuleModel = ModuleManager.getInstance(myProject).getModifiableModel();
            if (committedModules != null) {
                modules.addAll(committedModules);
            }
            ApplicationManager.getApplication().runWriteAction(() -> {
                for (Module module : modules) {
                    getOrCreateModuleEditor(module);
                }
            });
            return modules;
        }
        return null;
    }

    private ProjectBuilder runModuleWizard(Component dialogParent, boolean anImport, String defaultName) {
        CommaAbstractProjectWizard wizard;
        if (anImport) {
            wizard = ImportModuleAction.selectFileAndCreateWizard(myProject, dialogParent);
            if (wizard == null) return null;
            if (wizard.getStepCount() == 0) {
                ProjectBuilder builder = getProjectBuilder(wizard);
                Disposer.dispose(wizard.getDisposable());
                return builder;
            }
        }
        else {
            wizard = new CommaNewProjectWizard(myProject, dialogParent, this, defaultName);
        }
        if (!wizard.showAndGet()) {
            return null;
        }
        for (Module module : myModuleModel.getModules()) {
            if (Objects.equals(wizard.getProjectName(), module.getName())) {
                return null;
            }
        }
        return wizard.getBuilder(myProject);
    }

    private ProjectBuilder getProjectBuilder(@NotNull CommaAbstractProjectWizard wizard) {
        ProjectBuilder builder = wizard.getProjectBuilder();
        if (!builder.validate(myProject, myProject)) return null;
        return builder;
    }

    public static boolean showDialog(@NotNull Project project, @Nullable final String moduleToSelect, @Nullable final String editorNameToSelect) {
        final Perl6ProjectStructureConfigurable config = Perl6ProjectStructureConfigurable.getInstance(project);
        return ShowSettingsUtil.getInstance().editConfigurable(project, config, () -> config.select(moduleToSelect, editorNameToSelect, true));
    }

    public void moduleRenamed(@NotNull Module module, String oldName, @NotNull String name) {
        ModuleEditor editor = getModuleEditor(module);
        if (editor != null) {
            editor.setModuleName(name);
        }
    }

    public boolean isModified() {
        if (myModuleModel.isChanged() || myModified)
            return true;
        for (ModuleEditor moduleEditor : myModuleEditors.values()) {
            if (moduleEditor.isModified()) {
                return true;
            }
        }
        return false;
    }

    public void apply() throws ConfigurationException {
        // validate content and source roots
        final Map<VirtualFile, String> contentRootToModuleNameMap = new HashMap<>();
        final Map<VirtualFile, VirtualFile> srcRootsToContentRootMap = new HashMap<>();
        for (final ModuleEditor moduleEditor : myModuleEditors.values()) {
            final ModuleRootModel rootModel = moduleEditor.getRootModel();
            final ContentEntry[] contents = rootModel.getContentEntries();
            final String moduleName = moduleEditor.getName();
            Set<VirtualFile> sourceRoots = new HashSet<>();
            for (ContentEntry content : contents) {
                for (VirtualFile root : content.getSourceFolderFiles()) {
                    if (!sourceRoots.add(root)) {
                        throw new ConfigurationException(
                            String.format("Source root %s is duplicated in module %s", root.getPresentableUrl(), moduleName));
                    }
                }
            }

            for (ContentEntry contentEntry : contents) {
                final VirtualFile contentRoot = contentEntry.getFile();
                if (contentRoot == null) {
                    continue;
                }
                final String previousName = contentRootToModuleNameMap.put(contentRoot, moduleName);
                if (previousName != null && !previousName.equals(moduleName)) {
                    throw new ConfigurationException(
                        String.format("Content root %s is defined for modules %s and %s",
                                      contentRoot.getPresentableUrl(), previousName, moduleName)
                    );
                }
                for (VirtualFile srcRoot : contentEntry.getSourceFolderFiles()) {
                    final VirtualFile anotherContentRoot = srcRootsToContentRootMap.put(srcRoot, contentRoot);
                    if (anotherContentRoot != null) {
                        final String problematicModule;
                        final String correctModule;
                        if (VfsUtilCore.isAncestor(anotherContentRoot, contentRoot, true)) {
                            problematicModule = contentRootToModuleNameMap.get(anotherContentRoot);
                            correctModule = contentRootToModuleNameMap.get(contentRoot);
                        }
                        else {
                            problematicModule = contentRootToModuleNameMap.get(contentRoot);
                            correctModule = contentRootToModuleNameMap.get(anotherContentRoot);
                        }
                        throw new ConfigurationException(
                            String.format("Module %s must not contain source root %s, it already belongs to %s",
                                          problematicModule, srcRoot.getPresentableUrl(), correctModule)
                        );
                    }
                }
            }
        }
        // additional validation: directories marked as src roots must belong to the same module as their corresponding content root
        for (Map.Entry<VirtualFile, VirtualFile> entry : srcRootsToContentRootMap.entrySet()) {
            final VirtualFile srcRoot = entry.getKey();
            final VirtualFile correspondingContent = entry.getValue();
            final String expectedModuleName = contentRootToModuleNameMap.get(correspondingContent);

            for (VirtualFile candidateContent = srcRoot;
                 candidateContent != null && !candidateContent.equals(correspondingContent);
                 candidateContent = candidateContent.getParent()) {
                final String moduleName = contentRootToModuleNameMap.get(candidateContent);
                if (moduleName != null && !moduleName.equals(expectedModuleName)) {
                    throw new ConfigurationException(
                        String.format("Source root %s cannot be defined in module %s because it belongs to content of nested module %s",
                                      srcRoot.getPresentableUrl(), expectedModuleName, moduleName)
                    );
                }
            }
        }

        for (ModuleEditor moduleEditor : myModuleEditors.values()) {
            moduleEditor.canApply();
        }

        final Map<Sdk, Sdk> modifiedToOriginalMap = new THashMap<>();
        final ProjectSdksModel projectJdksModel = Perl6ProjectStructureConfigurable.getInstance(myProject).getProjectSdksModel();
        for (Map.Entry<Sdk, Sdk> entry : projectJdksModel.getProjectSdks().entrySet()) {
            modifiedToOriginalMap.put(entry.getValue(), entry.getKey());
        }

        final Ref<ConfigurationException> exceptionRef = Ref.create();
        ApplicationManager.getApplication().runWriteAction(() -> {
            final List<ModifiableRootModel> models = new ArrayList<>(myModuleEditors.size());
            try {
                for (final ModuleEditor moduleEditor : myModuleEditors.values()) {
                    final ModifiableRootModel model = moduleEditor.apply();
                    if (model != null) {
                        if (!model.isSdkInherited()) {
                            // make sure the sdk is set to original SDK stored in the JDK Table
                            final Sdk modelSdk = model.getSdk();
                            if (modelSdk != null) {
                                final Sdk original = modifiedToOriginalMap.get(modelSdk);
                                if (original != null) {
                                    model.setSdk(original);
                                }
                            }
                        }
                        models.add(model);
                    }
                }
            }
            catch (ConfigurationException e) {
                exceptionRef.set(e);
                return;
            }

            try {
                for (ModuleEditor editor : myModuleEditors.values()) {
                    editor.resetModifiableModel();
                }
                ModifiableModelCommitter.multiCommit(models, myModuleModel);
                myModuleModelCommitted = true;
            }
            finally {
                myModuleModel = ModuleManager.getInstance(myProject).getModifiableModel();
                myModuleModelCommitted = false;
            }
        });

        if (!exceptionRef.isNull()) {
            throw exceptionRef.get();
        }

        myModified = false;
    }

    public void setModified(final boolean modified) {
        myModified = modified;
    }

    public void setContext(Perl6StructureConfigurableContext context) {
        myContext = context;
    }

    public void disposeUIResources() {
        ApplicationManager.getApplication().runWriteAction(() -> {
            for (final ModuleEditor moduleEditor : myModuleEditors.values()) {
                Disposer.dispose(moduleEditor);
            }
            myModuleEditors.clear();
            myModuleModel.dispose();
        });
    }

    @Nullable
    public ModuleEditor getModuleEditor(Module module) {
        return myModuleEditors.get(module);
    }

    public ModifiableModuleModel getModuleModel() {
        return myModuleModel;
    }

    public List<Module> deleteModules(Collection<? extends Module> modules) {
        List<Module> deleted = new ArrayList<>();
        List<ModuleEditor> moduleEditors = new ArrayList<>();
        for (Module module : modules) {
            ModuleEditor moduleEditor = getModuleEditor(module);
            if (moduleEditor != null) {
                deleted.add(module);
                moduleEditors.add(moduleEditor);
            }
        }
        if (doRemoveModules(moduleEditors)) {
            return deleted;
        }
        return Collections.emptyList();
    }

    private boolean doRemoveModules(List<ModuleEditor> selectedEditors) {
        if (selectedEditors.isEmpty()) return true;

        String question;
        if (myModuleEditors.size() == selectedEditors.size()) {
            question = String.format("Are you sure you want to remove %s from this project? No files will be removed from disk?",
                                     selectedEditors.size() == 1 ? "the single module" : "all moduels");
        }
        else {
            question = String.format("Remove %s module%s from the project?", selectedEditors.size(),
                                     selectedEditors.size() == 1 ? "" : "s");
        }
        int result =
            Messages.showYesNoDialog(myProject, question, "Remove Modules", Messages.getQuestionIcon());
        if (result != Messages.YES) {
            return false;
        }
        WriteAction.run(() -> {
            for (ModuleEditor editor : selectedEditors) {
                myModuleEditors.remove(editor.getModule());

                final Module moduleToRemove = editor.getModule();
                // remove all dependencies on the module which is about to be removed
                List<ModifiableRootModel> modifiableRootModels = new ArrayList<>();
                for (final ModuleEditor moduleEditor : myModuleEditors.values()) {
                    final ModifiableRootModel modifiableRootModel = moduleEditor.getModifiableRootModelProxy();
                    ContainerUtil.addIfNotNull(modifiableRootModels, modifiableRootModel);
                }

                if (moduleToRemove != null) {
                    ModuleDeleteProvider.removeModule(moduleToRemove, modifiableRootModels, myModuleModel);
                }
                Disposer.dispose(editor);
            }
        });
        return true;
    }
}
