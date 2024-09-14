package org.raku.comma.debugger;

import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuLineBreakpointProperties extends XBreakpointProperties<RakuLineBreakpointProperties> {
    @Nullable
    @Override
    public RakuLineBreakpointProperties getState() {
        return null;
    }

    @Override
    public void loadState(@NotNull RakuLineBreakpointProperties state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
