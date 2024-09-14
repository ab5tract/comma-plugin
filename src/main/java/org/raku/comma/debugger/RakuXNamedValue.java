package org.raku.comma.debugger;

import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import org.raku.comma.RakuIcons;
import org.jetbrains.annotations.NotNull;

abstract public class RakuXNamedValue extends XNamedValue {
    private final RakuValueDescriptor myDescriptor;

    public RakuXNamedValue(RakuValueDescriptor descriptor) {
        super(descriptor.getName());
        myDescriptor = descriptor;
    }

    @Override
    public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
        node.setPresentation(RakuIcons.CAMELIA, myDescriptor.getType(),
                             myDescriptor.getPresentableDescription(getDebugThread()),
                             myDescriptor.isExpandableNode());
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        if (myDescriptor instanceof RakuObjectValueDescriptor) {
            getDebugThread().addObjectChildren(((RakuObjectValueDescriptor)myDescriptor).getHandle(), node);
        }
    }

    abstract protected RakuDebugThread getDebugThread();
}
