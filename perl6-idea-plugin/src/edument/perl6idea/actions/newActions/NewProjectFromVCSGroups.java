package edument.perl6idea.actions.newActions;

import com.intellij.openapi.vcs.checkout.NewProjectFromVCSGroup;
import org.jetbrains.annotations.NotNull;

public class NewProjectFromVCSGroups extends NewProjectFromVCSGroup {
  @NotNull
  @Override
  public String getActionText(boolean isInNewSubmenu, boolean isInJavaIde) {
    return super.getActionText(isInNewSubmenu, true);
  }
}
