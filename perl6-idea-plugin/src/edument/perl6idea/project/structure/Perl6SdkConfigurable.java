// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.project.structure;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.NamedConfigurable;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.ui.navigation.History;
import com.intellij.ui.navigation.Place;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6SdkConfigurable extends NamedConfigurable<Sdk> implements Place.Navigator {
    private final ProjectJdkImpl myProjectJdk;
    private final Perl6SdkEditor mySdkEditor;

    public Perl6SdkConfigurable(ProjectJdkImpl projectJdk,
                                @NotNull ProjectSdksModel model,
                                Runnable updateTree,
                                History history,
                                Project project) {
        super(true, updateTree);
        myProjectJdk = projectJdk;
        mySdkEditor = createSdkEditor(project, model, history, myProjectJdk);
    }

    @NotNull
    protected Perl6SdkEditor createSdkEditor(@NotNull Project project,
                                        @NotNull ProjectSdksModel sdksModel,
                                        @NotNull History history,
                                        @NotNull ProjectJdkImpl projectJdk) {
        return new Perl6SdkEditor(project, sdksModel, history, projectJdk);
    }

    @Override
    public void setDisplayName(String name) {
        myProjectJdk.setName(name);
    }

    @Override
    public Sdk getEditableObject() {
        return myProjectJdk;
    }

    @Override
    public String getBannerSlogan() {
        return myProjectJdk.getName();
    }

    @Override
    public JComponent createOptionsPanel() {
        return mySdkEditor.createComponent();
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return myProjectJdk.getName();
    }

    @Override
    public boolean isModified() {
        return mySdkEditor.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        mySdkEditor.apply();
    }

    @Override
    public void reset() {
        mySdkEditor.reset();
    }

    @Override
    public void disposeUIResources() {
        mySdkEditor.disposeUIResources();
    }

    @Override
    public ActionCallback navigateTo(@Nullable final Place place, final boolean requestFocus) {
        return mySdkEditor.navigateTo(place, requestFocus);
    }

    @Override
    public void queryPlace(@NotNull final Place place) {
        mySdkEditor.queryPlace(place);
    }

    @Override
    public Icon getIcon(boolean open) {
        return ((SdkType) myProjectJdk.getSdkType()).getIcon();
    }
}
