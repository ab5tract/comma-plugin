package org.raku.comma.debugger;

import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueGroup;
import org.raku.comma.RakuIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RakuXLexicalGroup extends XValueGroup {
    private final RakuStackFrame myStackFrame;
    private final RakuValueDescriptor[] lexicals;

    public RakuXLexicalGroup(String name, RakuValueDescriptor[] lexicals, RakuStackFrame stackFrame) {
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
            for (RakuValueDescriptor descriptor : lexicals) {
                list.add(new RakuXLexicalValue(descriptor, myStackFrame));
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
        return RakuIcons.CAMELIA;
    }

    public RakuStackFrame getStackFrame() {
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
