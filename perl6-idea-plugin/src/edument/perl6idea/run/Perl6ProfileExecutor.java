package edument.perl6idea.run;

import com.intellij.execution.Executor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindowId;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Perl6ProfileExecutor extends Executor {
    public static final String EXECUTOR_ID = "Perl6Profile";

    @Override
    @NotNull
    public String getStartActionText() {
        return "Run with _Profiling";
    }

    @Override
    public @NotNull String getStartActionText(@NotNull String configurationName) {
        final String name = escapeMnemonicsInConfigurationName(configurationName);
        return "Run" + (StringUtil.isEmpty(name) ? "" : " '" + name + "'") + " with _Profiling";
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
            return Perl6Icons.CAMELIA_STOPWATCH;
	}

    @Override
    public Icon getDisabledIcon() {
            return null;
	}

    @Override
    public String getDescription() {
            return "Profile the selected run configuration";
	}

    @Override
    @NotNull
    public String getActionName() {
            return "Profile";
	}

    @Override
    @NotNull
    public String getId() {
            return EXECUTOR_ID;
	}

    @Override
    public String getContextActionId() {
        return "Perl6RunProfile";
    }

    @Override
    public String getHelpId() {
            return null;
        }
}
