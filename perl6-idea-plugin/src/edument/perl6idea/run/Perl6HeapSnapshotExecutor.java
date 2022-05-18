package edument.perl6idea.run;

import com.intellij.execution.Executor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindowId;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@InternalIgnoreDependencyViolation
public class Perl6HeapSnapshotExecutor extends Executor {
    public static final String EXECUTOR_ID = "Perl6HeapSnapshot";

    @NotNull
    public String getStartActionText() {
        return "Run and collect _Heap Snapshots";
    }

    @Override
    public String getStartActionText(String configurationName) {
        final String name = configurationName != null
                            ? escapeMnemonicsInConfigurationName(configurationName)
                            : null;
        return "Run" + (StringUtil.isEmpty(name) ? "" : " '" + name + "'") + " and collect _Heap Snapshots";
    }

    private static String escapeMnemonicsInConfigurationName(String configurationName) {
        return configurationName.replace("_", "__");
    }

    public String getToolWindowId() {
        return ToolWindowId.RUN;
    }

    @Override
    public Icon getToolWindowIcon() {
        return AllIcons.Toolwindows.ToolWindowRun;
    }

    @NotNull
    public Icon getIcon() {
        return AllIcons.Actions.Dump;
    }

    public Icon getDisabledIcon() {
        return null;
    }

    public String getDescription() {
        return "Run the selected run configuration and collect heap snapshots";
    }

    @NotNull
    public String getActionName() {
        return "Collect Heap Snapshots";
    }

    @NotNull
    public String getId() {
        return EXECUTOR_ID;
    }

    public String getContextActionId() {
        return "Perl6RunHeapSnapshot";
    }

    public String getHelpId() {
        return null;
    }
}
