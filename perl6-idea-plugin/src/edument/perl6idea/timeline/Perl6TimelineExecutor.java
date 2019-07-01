package edument.perl6idea.timeline;

import com.intellij.execution.Executor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindowId;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Perl6TimelineExecutor extends Executor {
    public static final String EXECUTOR_ID = "Perl6Timeline";

    @NotNull
    public String getStartActionText() {
    return "Run and show _Timeline";
  }

    @Override
    public String getStartActionText(String configurationName) {
        final String name = configurationName != null
                            ? escapeMnemonicsInConfigurationName(configurationName)
                            : null;
        return "Run" + (StringUtil.isEmpty(name) ? "" : " '" + name + "'") + " and show _Timeline";
    }

    private static String escapeMnemonicsInConfigurationName(String configurationName) {
        return configurationName.replace("_", "__");
    }

    public String getToolWindowId() {
        return ToolWindowId.RUN;
    }

    public Icon getToolWindowIcon() {
        return AllIcons.Toolwindows.ToolWindowRun;
    }

    @NotNull
    public Icon getIcon() {
        return Perl6Icons.RUN_WITH_TIMELINE;
    }

    public Icon getDisabledIcon() {
        return null;
    }

    public String getDescription() {
        return "Run and collect live program timeline";
    }

    @NotNull
    public String getActionName() {
        return "Timeline";
    }

    @NotNull
    public String getId() {
        return EXECUTOR_ID;
    }

    public String getContextActionId() {
        return "Perl6RunTimeline";
    }

    public String getHelpId() {
        return null;
    }
}
