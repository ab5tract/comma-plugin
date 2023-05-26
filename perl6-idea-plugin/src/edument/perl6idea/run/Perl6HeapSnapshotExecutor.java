package edument.perl6idea.run;

import com.intellij.execution.Executor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@InternalIgnoreDependencyViolation
public class Perl6HeapSnapshotExecutor extends Executor {
    public static final String EXECUTOR_ID = "Perl6HeapSnapshot";

    @Override
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

    @Override
    public String getToolWindowId() {
        return ToolWindowId.RUN;
    }

    @Override
    public Icon getToolWindowIcon() {
        return AllIcons.Toolwindows.ToolWindowRun;
    }

    @Override
    @NotNull
    public Icon getIcon() {
        return AllIcons.Actions.Dump;
    }

    @Override
    public Icon getDisabledIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Run the selected run configuration and collect heap snapshots";
    }

    @Override
    @NotNull
    public String getActionName() {
        return "Collect Heap Snapshots";
    }

    @Override
    @NotNull
    public String getId() {
        return EXECUTOR_ID;
    }

    @Override
    public String getContextActionId() {
        return "Perl6RunHeapSnapshot";
    }

    @Override
    public String getHelpId() {
        return null;
    }
}
