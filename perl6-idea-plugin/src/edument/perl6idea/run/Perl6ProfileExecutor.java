package edument.perl6idea.run;

import com.intellij.execution.executors.DefaultRunExecutor;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Perl6ProfileExecutor extends DefaultRunExecutor {
    public static final String RUN_WITH_PERL_6_PROFILER = "Profile";
    public static final String RUN_WITH_PERL_6_PROFILER1 = "RunWithPerl6Profiler";

    @NotNull
    public String getToolWindowId() {
            return getId();
	}

    @NotNull
    public Icon getIcon() {
            return Perl6Icons.CAMELIA;
	}

    public Icon getDisabledIcon() {
            return null;
	}

    public String getDescription() {
            return RUN_WITH_PERL_6_PROFILER;
	}

    @NotNull
    public String getActionName() {
            return RUN_WITH_PERL_6_PROFILER1;
	}

    @NotNull
    public String getId() {
            return RUN_WITH_PERL_6_PROFILER;
	}

    @NotNull
    public String getStartActionText() {
            return RUN_WITH_PERL_6_PROFILER;
        }

    public String getContextActionId() {
        // HACK: ExecutorRegistryImpl expects this to be non-null, but we don't want any context actions for every file
        return getId() + " context-action-does-not-exist";
    }

    public String getHelpId() {
            return null;
        }
}
