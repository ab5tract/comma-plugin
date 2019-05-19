package edument.perl6idea.actions.newActions;

import org.jetbrains.annotations.NotNull;

public class NewProjectAction extends com.intellij.ide.actions.NewProjectAction {
  @NotNull
  @Override
  public String getActionText(boolean isInNewSubmenu, boolean isInJavaIde) {
    return super.getActionText(isInNewSubmenu, true);
  }
}
