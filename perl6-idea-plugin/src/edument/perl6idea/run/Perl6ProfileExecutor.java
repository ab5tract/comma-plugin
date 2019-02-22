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

    @NotNull
    public String getStartActionText() {
        return "Run with _Profiling";
    }

    @Override
    public String getStartActionText(String configurationName) {
        final String name = configurationName != null
                            ? escapeMnemonicsInConfigurationName(configurationName)
                            : null;
        return "Run" + (StringUtil.isEmpty(name) ? "" : " '" + name + "'") + " with _Profiling";
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
            return Perl6Icons.CAMELIA;
	}

    public Icon getDisabledIcon() {
            return null;
	}

    public String getDescription() {
            return "Profile the selected run configuration";
	}

    @NotNull
    public String getActionName() {
            return "Profile";
	}

    @NotNull
    public String getId() {
            return EXECUTOR_ID;
	}

    public String getContextActionId() {
        return "Perl6RunProfile";
    }

    public String getHelpId() {
            return null;
        }
}
