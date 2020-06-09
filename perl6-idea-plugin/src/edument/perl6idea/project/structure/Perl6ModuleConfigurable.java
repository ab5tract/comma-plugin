package edument.perl6idea.project.structure;

import com.intellij.openapi.module.*;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.NamedConfigurable;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.navigation.Place;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class Perl6ModuleConfigurable extends NamedConfigurable<Module> implements Place.Navigator {
    private final Module myModule;
    private final ModuleGrouper myModuleGrouper;
    private final Perl6ModulesConfigurator myConfigurator;
    private String myModuleName;
    //private final ModuleProjectStructureElement myProjectStructureElement;
    //private final StructureConfigurableContext myContext;

    public Perl6ModuleConfigurable(Perl6ModulesConfigurator modulesConfigurator,
                                   @NotNull Module module,
                                   Runnable updateTree,
                                   ModuleGrouper moduleGrouper) {
        super(true, updateTree);
        myModule = module;
        myModuleGrouper = moduleGrouper;
        myConfigurator = modulesConfigurator;
        myModuleName = myConfigurator.getModuleModel().getActualName(myModule);
        //myContext = Perl6ModuleStructureConfigurable.getInstance(myModule.getProject()).getContext();
        //myProjectStructureElement = new ModuleProjectStructureElement(myContext, myModule);
    }

    @Override
    public void setDisplayName(String name) {
        final ModifiableModuleModel modifiableModuleModel = myConfigurator.getModuleModel();
        if (StringUtil.isEmpty(name)) return; //empty string comes on double click on module node
        if (Comparing.strEqual(name, myModuleName)) return; //nothing changed
        try {
            modifiableModuleModel.renameModule(myModule, name);
        }
        catch (ModuleWithNameAlreadyExists moduleWithNameAlreadyExists) {
            //do nothing
        }
        myConfigurator.moduleRenamed(myModule, myModuleName, name);
        myModuleName = name;
        myConfigurator.setModified(!Comparing.strEqual(myModuleName, myModule.getName()));
    }

    @Override
    protected void checkName(@NotNull String name) throws ConfigurationException {
        super.checkName(name);
        if (myModuleGrouper.getShortenedNameByFullModuleName(name).isEmpty()) {
            throw new ConfigurationException("Short name of a module cannot be empty");
        }
        List<String> list = myModuleGrouper.getGroupPathByModuleName(name);
        if (list.stream().anyMatch(s -> s.isEmpty())) {
            throw new ConfigurationException("Names of parent groups for a module cannot be empty");
        }
    }

    @Override
    @NotNull
    public Module getEditableObject() {
        return myModule;
    }

    @Override
    public String getBannerSlogan() {
        return "Module " + myModuleName;
    }

    @Override
    public String getDisplayName() {
        return myModuleName;
    }

    @Override
    public Icon getIcon(final boolean open) {
        return myModule.isDisposed() ? null : ModuleType.get(myModule).getIcon();
    }

    @NotNull
    public Module getModule() {
        return myModule;
    }

    @Override
    @Nullable
    @NonNls
    public String getHelpTopic() {
        ModuleEditor editor = getModuleEditor();
        return editor == null ? null : editor.getHelpTopic();
    }


    @Override
    public JComponent createOptionsPanel() {
        ModuleEditor editor = getModuleEditor();
        return editor == null ? null : editor.getPanel();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        //do nothing
    }

    public ModuleEditor getModuleEditor() {
        return myConfigurator.getModuleEditor(myModule);
    }

    @Override
    public ActionCallback navigateTo(@Nullable Place place, final boolean requestFocus) {
        ModuleEditor editor = getModuleEditor();
        return editor == null ? ActionCallback.REJECTED : editor.navigateTo(place, requestFocus);
    }

    @Override
    public void queryPlace(@NotNull Place place) {
        final ModuleEditor editor = getModuleEditor();
        if (editor != null) {
            editor.queryPlace(place);
        }
    }
}
