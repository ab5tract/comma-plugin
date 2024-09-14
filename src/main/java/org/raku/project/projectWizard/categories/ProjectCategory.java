// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.raku.project.projectWizard.categories;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Dmitry Avdeev
 */
public abstract class ProjectCategory {
    @NotNull
    public abstract ModuleBuilder createModuleBuilder();

    public String getId() {
        return createModuleBuilder().getBuilderId();
    }

    public String getDisplayName() {
        return createModuleBuilder().getPresentableName();
    }

    public Icon getIcon() {
        return createModuleBuilder().getNodeIcon();
    }

    public String getDescription() {
        return createModuleBuilder().getDescription();
    }

    public String getGroupName() {
        return createModuleBuilder().getGroupName();
    }

    public int getWeight() {
        return 0;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
