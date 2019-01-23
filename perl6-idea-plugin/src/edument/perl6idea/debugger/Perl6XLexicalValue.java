package edument.perl6idea.debugger;

import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

public class Perl6XLexicalValue extends XNamedValue {
    private final Perl6ValueDescriptor myDescriptor;
    private final Perl6StackFrame myStackFrame;

    public Perl6XLexicalValue(Perl6ValueDescriptor descriptor, Perl6StackFrame StackFrame) {
        super(descriptor.getName());
        myDescriptor = descriptor;
        myStackFrame = StackFrame;
    }

    @Override
    public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
        node.setPresentation(Perl6Icons.CAMELIA, myDescriptor.getType(), myDescriptor.getValue(),
                myDescriptor.isExpandableNode());
    }
}
