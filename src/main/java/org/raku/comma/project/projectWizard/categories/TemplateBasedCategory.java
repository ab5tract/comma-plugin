// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.raku.comma.project.projectWizard.categories;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.platform.templates.ArchivedProjectTemplate;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Dmitry Avdeev
 */
public class TemplateBasedCategory extends ProjectCategory {

  private final ArchivedProjectTemplate myTemplate;
  private final String myProjectType;

  public TemplateBasedCategory(ArchivedProjectTemplate template, String projectType) {
    myTemplate = template;
    myProjectType = projectType;
  }

  @Override
  public String getDisplayName() {
    return myProjectType;
  }

  @Override
  public Icon getIcon() {
    return myTemplate.getIcon();
  }

  @Override
  public String getDescription() {
    return myTemplate.getDescription();
  }

  @NotNull
  @Override
  public ModuleBuilder createModuleBuilder() {
    return myTemplate.createModuleBuilder();
  }
}
