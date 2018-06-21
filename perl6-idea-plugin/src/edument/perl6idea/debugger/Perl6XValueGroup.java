package edument.perl6idea.debugger;

import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueGroup;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Perl6XValueGroup extends XValueGroup {
    private final Perl6StackFrame myStackFrame;
    private final Perl6ValueDescriptor[] lexicals;

    public Perl6XValueGroup(String name, Perl6ValueDescriptor[] lexicals, Perl6StackFrame stackFrame) {
        super(name);
        this.lexicals = lexicals;
        myStackFrame = stackFrame;
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        if (getSize() == 0) {
            super.computeChildren(node);
        } else {
            XValueChildrenList list = new XValueChildrenList();
            for (Perl6ValueDescriptor descriptor : lexicals) {
                list.add(new Perl6XNamedValue(descriptor, myStackFrame));
            }
            node.setAlreadySorted(true);
            node.addChildren(list, true);
        }
    }

    @Override
    public boolean isAutoExpand() {
        return true;
    }

    @Override
    public Icon getIcon() {
        return Perl6Icons.CAMELIA;
    }

    public Perl6StackFrame getStackFrame() {
        return myStackFrame;
    }

    protected int getSize() {
        return lexicals.length;
    }

    @NotNull
    @Override
    public String getName() {
        return super.getName() + "(" + getSize() + ")";
    }
}
