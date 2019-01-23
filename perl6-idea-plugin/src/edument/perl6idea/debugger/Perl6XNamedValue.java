package edument.perl6idea.debugger;

import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

abstract public class Perl6XNamedValue extends XNamedValue {
    private final Perl6ValueDescriptor myDescriptor;

    public Perl6XNamedValue(Perl6ValueDescriptor descriptor) {
        super(descriptor.getName());
        myDescriptor = descriptor;
    }

    @Override
    public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
        node.setPresentation(Perl6Icons.CAMELIA, myDescriptor.getType(), myDescriptor.getValue(),
                             myDescriptor.isExpandableNode());
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        if (myDescriptor instanceof Perl6ObjectValueDescriptor) {
            getDebugThread().addObjectChildren(((Perl6ObjectValueDescriptor)myDescriptor).getHandle(), node);
        }
    }

    abstract protected Perl6DebugThread getDebugThread();
}
