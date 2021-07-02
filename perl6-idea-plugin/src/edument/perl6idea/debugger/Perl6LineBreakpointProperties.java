package edument.perl6idea.debugger;

import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6LineBreakpointProperties extends XBreakpointProperties<Perl6LineBreakpointProperties> {
    @Nullable
    @Override
    public Perl6LineBreakpointProperties getState() {
        return null;
    }

    @Override
    public void loadState(@NotNull Perl6LineBreakpointProperties state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
