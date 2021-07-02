// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.project.structure;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.RootConfigurationAccessor;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yole
 */
public class UIRootConfigurationAccessor extends RootConfigurationAccessor {
    private final Project myProject;

    public UIRootConfigurationAccessor(final Project project) {
        myProject = project;
    }

    @Override
    @Nullable
    public Library getLibrary(Library library, final String libraryName, final String libraryLevel) {
        // TODO decide what to do with this
        //final Perl6StructureConfigurableContext context = Perl6ProjectStructureConfigurable.getInstance(myProject).getContext();
        //if (library == null) {
        //    if (libraryName != null) {
        //        library = context.getLibrary(libraryName, libraryLevel);
        //    }
        //}
        //else {
        //    final Library model = context.getLibraryModel(library);
        //    if (model != null) {
        //        library = model;
        //    }
        //    library = context.getLibrary(library.getName(), library.getTable().getTableLevel());
        //}
        return library;
    }

    @Override
    @Nullable
    public Sdk getSdk(final Sdk sdk, final String sdkName) {
        // FIXME SDK
        //final ProjectSdksModel model = Perl6ProjectStructureConfigurable.getInstance(myProject).getSdkConfig().getJdksTreeModel();
        //return sdkName != null ? model.findSdk(sdkName) : sdk;
        return sdk;
    }

    @Override
    public Module getModule(final Module module, final String moduleName) {
        if (module == null) {
            return Perl6ModuleStructureConfigurable.getInstance(myProject).getModule(moduleName);
        }
        return module;
    }

    @Override
    public Sdk getProjectSdk(final @NotNull Project project) {
        return Perl6ProjectStructureConfigurable.getInstance(project).getProjectSdksModel().getProjectSdk();
    }

    @Override
    @Nullable
    public String getProjectSdkName(final @NotNull Project project) {
        final Sdk projectJdk = getProjectSdk(project);
        if (projectJdk != null) {
            return projectJdk.getName();
        }
        final String projectJdkName = ProjectRootManager.getInstance(project).getProjectSdkName();
        final ProjectSdksModel projectJdksModel = Perl6ProjectStructureConfigurable.getInstance(project).getProjectSdksModel();
        return projectJdkName != null && projectJdksModel.findSdk(projectJdkName) == null ? projectJdkName : null;
    }
}
