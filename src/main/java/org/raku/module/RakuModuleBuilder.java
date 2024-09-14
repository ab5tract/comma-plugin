package org.raku.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.language.RakuLanguageVersion;
import org.raku.language.RakuLanguageVersionService;
import org.raku.metadata.RakuMetaDataComponent;
import org.raku.module.builder.*;
import org.raku.sdk.RakuSdkType;
import org.raku.utils.RakuProjectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RakuModuleBuilder extends ModuleBuilder {
    private static final Logger LOG = Logger.getInstance(RakuModuleBuilder.class);
    private RakuProjectType myModuleType = RakuProjectType.RAKU_SCRIPT;
    private RakuModuleBuilderGeneric myBuilder = new RakuModuleBuilderScript();

    @Override
    public String getName() {
        return "Raku Builder";
    }

    @Override
    public String getDescription() {
        return "Raku builder";
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel model) {
        updateBuilder();
        ContentEntry contentEntry = doAddContentEntry(model);
        if (contentEntry == null) return;
        updateBuilder();
        List<Pair<String, String>> sourcePaths = myBuilder.getSourcePaths(getContentEntryPath());
        for (final Pair<String, String> sourcePathPair : sourcePaths) {
            Path sourcePath = addSourceRoot(contentEntry, sourcePathPair);
            Project project = model.getProject();
            RakuLanguageVersion prefix;
            RakuLanguageVersionService langVersionService = project.getService(RakuLanguageVersionService.class);
            if (langVersionService.getIsExplicit()) {
                prefix = langVersionService.getVersion();
            } else {
                prefix = null;
            }
            myBuilder.setupRootModelOfPath(model, sourcePath, prefix);
        }
    }

    private Path addSourceRoot(ContentEntry contentEntry, Pair<String, String> sourcePathPair) {
        Path sourcePath = Paths.get(sourcePathPair.first, sourcePathPair.second);
        File directory = sourcePath.toFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Could not create directory: " + directory);
        }
        if (myBuilder.shouldBeMarkedAsRoot(sourcePathPair.second)) {
            VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(sourcePath.toFile());
            if (sourceRoot != null) {
                contentEntry.addSourceFolder(sourceRoot, sourcePathPair.second.equals("t"), sourcePathPair.second);
            }
        }
        return sourcePath;
    }

    protected void updateBuilder() {
        Map<RakuProjectType, Class<?>> typeToBuilderPairs = new HashMap<>();
        typeToBuilderPairs.put(RakuProjectType.RAKU_SCRIPT, RakuModuleBuilderScript.class);
        typeToBuilderPairs.put(RakuProjectType.RAKU_MODULE, RakuModuleBuilderModule.class);
        typeToBuilderPairs.put(RakuProjectType.RAKU_APPLICATION, RakuModuleBuilderApplication.class);
        typeToBuilderPairs.put(RakuProjectType.CRO_WEB_APPLICATION, CroModuleBuilderApplication.class);
        Class<?> currentTypeBuilder = typeToBuilderPairs.get(myModuleType);
        if (currentTypeBuilder.isInstance(myBuilder))
            return;
        try {
            Constructor<?> constructor = currentTypeBuilder.getConstructor();
            myBuilder = (RakuModuleBuilderGeneric)constructor.newInstance();
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOG.error("Could not update builder", e);
        }
    }

    @Nullable
    @Override
    public List<Module> commit(@NotNull Project project, ModifiableModuleModel model, ModulesProvider modulesProvider) {
        List<Module> modules = super.commit(project, model, modulesProvider);
        if (model != null)
            WriteAction.run(model::commit);
        if (modules != null) {
            for (Module module : modules) {
                module.getService(RakuMetaDataComponent.class).triggerMetaBuild();
            }
        }
        return modules;
    }

    @NotNull
    @Override
    public ModuleType<RakuModuleBuilder> getModuleType() {
        return RakuModuleType.getInstance();
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdk) {
        return sdk == RakuSdkType.getInstance();
    }

    @Override
    public String getGroupName() {
        return "Raku";
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{ new RakuModuleWizardStep(this) };
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        TypeWizardStepForm step = new TypeWizardStepForm(this, context.isCreatingNewProject());
        Disposer.register(parentDisposable, step);
        return step;
    }

    public RakuProjectType getRakuModuleType() {
        return myModuleType;
    }

    public void setRakuModuleType(RakuProjectType type) {
        this.myModuleType = type;
    }

    public void updateLocalBuilder(Map<String, String> formData) {
        updateBuilder();
        myBuilder.loadFromDialogData(formData);
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        myBuilder.modifySettingsStep(settingsStep);
        return super.modifySettingsStep(settingsStep);
    }
}
