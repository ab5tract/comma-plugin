// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.raku.project.structure;

import com.intellij.openapi.ui.NamedConfigurable;

import java.util.List;

public abstract class RemoveConfigurableHandler<T> {
  private final Class<? extends NamedConfigurable<T>> myConfigurableClass;

  public RemoveConfigurableHandler(Class<? extends NamedConfigurable<T>> configurableClass) {
    myConfigurableClass = configurableClass;
  }

  public Class<? extends NamedConfigurable<T>> getConfigurableClass() {
    return myConfigurableClass;
  }

  public boolean canBeRemoved(List<?> objects) {
    return true;
  }

  public abstract boolean remove(List<?> objects);
}
