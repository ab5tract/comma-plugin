package org.raku.comma.timeline;

import com.intellij.execution.Executor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindowId;
import org.raku.comma.RakuIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@InternalIgnoreDependencyViolation
public class RakuTimelineExecutor extends Executor {
    public static final String EXECUTOR_ID = "Perl6Timeline";

    @Override
    @NotNull
    public String getStartActionText() {
    return "Run and show _Timeline";
  }

    @Override
    public @NotNull String getStartActionText(@NotNull String configurationName) {
        final String name = escapeMnemonicsInConfigurationName(configurationName);
        return "Run" + (StringUtil.isEmpty(name) ? "" : " '" + name + "'") + " and show _Timeline";
    }

    private static String escapeMnemonicsInConfigurationName(String configurationName) {
        return configurationName.replace("_", "__");
    }

    @Override
    public @NotNull String getToolWindowId() {
        return ToolWindowId.RUN;
    }

    @Override
    public @NotNull Icon getToolWindowIcon() {
        return AllIcons.Toolwindows.ToolWindowRun;
    }

    @Override
    @NotNull
    public Icon getIcon() {
        return RakuIcons.RUN_WITH_TIMELINE;
    }

    @Override
    public Icon getDisabledIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Run and collect live program timeline";
    }

    @Override
    @NotNull
    public String getActionName() {
        return "Timeline";
    }

    @Override
    @NotNull
    public String getId() {
        return EXECUTOR_ID;
    }

    @Override
    public String getContextActionId() {
        return "Perl6RunTimeline";
    }

    @Override
    public String getHelpId() {
        return null;
    }
}
