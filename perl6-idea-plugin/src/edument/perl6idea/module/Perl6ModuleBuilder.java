package edument.perl6idea.module;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.Disposable;
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
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.module.builder.*;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6ProjectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Perl6ModuleBuilder extends ModuleBuilder implements SourcePathsBuilder {
    private static Logger LOG = Logger.getInstance(Perl6ModuleBuilder.class);
    private Perl6ProjectType myModuleType = Perl6ProjectType.PERL6_SCRIPT;
    private Perl6ModuleBuilderGeneric myBuilder = new Perl6ModuleBuilderScript();
    private List<Pair<String, String>> mySourcePaths = new ArrayList<>();

    @Override
    public String getName() {
        return "Perl 6 Builder";
    }

    @Override
    public String getDescription() {
        return "Perl 6 builder";
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel model) {
        updateBuilder();
        ContentEntry contentEntry = doAddContentEntry(model);
        if (contentEntry == null) return;
        List<Pair<String, String>> sourcePaths = getSourcePaths();
        for (final Pair<String, String> sourcePathPair : sourcePaths) {
            Path sourcePath = addSourceRoot(contentEntry, sourcePathPair);
            myBuilder.setupRootModelOfPath(model, sourcePath);
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
            if (sourceRoot != null)
                contentEntry.addSourceFolder(sourceRoot, sourcePathPair.second.equals("t"), sourcePathPair.second);
        }
        return sourcePath;
    }

    protected void updateBuilder() {
        Map<Perl6ProjectType, Class<?>> typeToBuilderPairs = new HashMap<>();
        typeToBuilderPairs.put(Perl6ProjectType.PERL6_SCRIPT, Perl6ModuleBuilderScript.class);
        typeToBuilderPairs.put(Perl6ProjectType.PERL6_MODULE, Perl6ModuleBuilderModule.class);
        typeToBuilderPairs.put(Perl6ProjectType.PERL6_APPLICATION, Perl6ModuleBuilderApplication.class);
        typeToBuilderPairs.put(Perl6ProjectType.CRO_WEB_APPLICATION, CroModuleBuilderApplication.class);
        Class<?> currentTypeBuilder = typeToBuilderPairs.get(myModuleType);
        if (currentTypeBuilder.isInstance(myBuilder))
            return;
        try {
            Constructor<?> constructor = currentTypeBuilder.getConstructor();
            myBuilder = (Perl6ModuleBuilderGeneric)constructor.newInstance();
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOG.error("Could not update builder", e);
        }
    }

    @Nullable
    @Override
    public List<Module> commit(@NotNull Project project, ModifiableModuleModel model, ModulesProvider modulesProvider) {
        List<Module> modules = super.commit(project, model, modulesProvider);
        if (modules != null) {
            for (Module module : modules) {
                module.getComponent(Perl6MetaDataComponent.class).triggerMetaBuild();
            }
        }
        return modules;
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return Perl6ModuleType.getInstance();
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdk) {
        return sdk == Perl6SdkType.getInstance();
    }

    @Override
    public String getGroupName() {
        return "Perl 6";
    }

    @Override
    public List<Pair<String, String>> getSourcePaths() {
        updateBuilder();
        return myBuilder.getSourcePaths(getContentEntryPath());
    }

    @Override
    public void setSourcePaths(List<Pair<String, String>> sourcePaths) {
        mySourcePaths = sourcePaths;
    }

    @Override
    public void addSourcePath(Pair<String, String> sourcePathInfo) {
        mySourcePaths.add(sourcePathInfo);
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{ new Perl6ModuleWizardStep(this) };
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        TypeWizardStepForm step = new TypeWizardStepForm(this);
        Disposer.register(parentDisposable, step);
        return step;
    }

    public Perl6ProjectType getPerl6ModuleType() {
        return myModuleType;
    }

    public void setPerl6ModuleType(Perl6ProjectType type) {
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
