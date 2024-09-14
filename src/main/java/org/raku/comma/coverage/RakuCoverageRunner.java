package org.raku.comma.coverage;

import com.intellij.execution.configurations.RunProfile;
import org.raku.comma.debugger.RakuDefaultRunner;
import org.raku.comma.run.RakuCompleteRunConfigurationType;
import org.raku.comma.run.RakuRunConfiguration;
import org.raku.comma.testing.RakuCompleteTestConfigurationType;
import org.raku.comma.testing.RakuTestRunConfiguration;
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
