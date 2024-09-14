package org.raku.actions;

import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewActionGroup extends ActionGroup {
    @NonNls private static final String PROJECT_OR_MODULE_GROUP_ID = "NewProjectOrModuleGroup";

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        AnAction[] actions = ((ActionGroup)ActionManager.getInstance().getAction(IdeActions.GROUP_WEIGHING_NEW)).getChildren(e);
        if (e == null || ActionPlaces.isMainMenuOrActionSearch(e.getPlace())) {
            AnAction newGroup = ActionManager.getInstance().getAction(PROJECT_OR_MODULE_GROUP_ID);
            if (newGroup != null) {
                AnAction[] newProjectActions = ((ActionGroup)newGroup).getChildren(e);
                if (newProjectActions.length > 0) {
                    List<AnAction> mergedActions = new ArrayList<>(newProjectActions.length + 1 + actions.length);
                    Collections.addAll(mergedActions, newProjectActions);
                    mergedActions.add(Separator.getInstance());
                    Collections.addAll(mergedActions, actions);
                    return mergedActions.toArray(AnAction.EMPTY_ARRAY);
                }
            }
        }
        return actions;
    }
}
