// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.project.structure;

import com.intellij.openapi.ui.NamedConfigurable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class RemoveConfigurableHandler<T> {
  private final Class<? extends NamedConfigurable<T>> myConfigurableClass;

  public RemoveConfigurableHandler(Class<? extends NamedConfigurable<T>> configurableClass) {
    myConfigurableClass = configurableClass;
  }

  public Class<? extends NamedConfigurable<T>> getConfigurableClass() {
    return myConfigurableClass;
  }

  public boolean canBeRemoved(@NotNull Collection<? extends T> objects) {
    return true;
  }

  public abstract boolean remove(@NotNull Collection<? extends T> objects);
}
