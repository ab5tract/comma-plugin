package org.raku.coverage;

import com.intellij.execution.configurations.RunProfile;
import org.raku.debugger.RakuDefaultRunner;
import org.raku.run.RakuCompleteRunConfigurationType;
import org.raku.run.RakuRunConfiguration;
import org.raku.testing.RakuCompleteTestConfigurationType;
import org.raku.testing.RakuTestRunConfiguration;
import org.jetbrains.annotations.NotNull;

public class RakuCoverageRunner extends RakuDefaultRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Raku Coverage";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return "Coverage".equals(executorId) &&
               (profile instanceof RakuRunConfiguration || profile instanceof RakuTestRunConfiguration ||
                profile instanceof RakuCompleteRunConfigurationType || profile instanceof RakuCompleteTestConfigurationType);
    }
}
