package edument.perl6idea.profiler;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Perl6ProfileModel extends AbstractTreeTableModel {
    protected boolean internalCallsAreVisible = false;

    public Perl6ProfileModel(List<ProfilerNode> routines, JBCheckBox showInternals) {
        super(routines);
        showInternals.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                internalCallsAreVisible = showInternals.isSelected();
            }
        });
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof ProfilerNode) {
            ProfilerNode profilerNode = (ProfilerNode)node;
            switch (column) {
                case 0:
                    return profilerNode.getName();
                case 1:
                    return profilerNode.getInclusiveTime();
                case 2:
                    return profilerNode.getExclusiveTime();
                default:
                    return profilerNode.getCallCount();
            }
        } else if (node instanceof CalleeNode && column == 0) {
            return ((CalleeNode)node).getName();
        }
        return null;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof List) {
            return ((List)parent).get(index);
        } else if (parent instanceof ProfilerNode) {
            return ((ProfilerNode)parent).getCalleeNode(index);
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof List) {
            return ((List)parent).size();
        } else if (parent instanceof ProfilerNode) {
            return ((ProfilerNode)parent).getCalleeSize();
        } else {
            return 0;
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof List) {
            ((List)parent).indexOf(child);
        }
        return 0;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            default:
                return Integer.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Routine";
            case 1:
                return "Inclusive time (μs)";
            case 2:
                return "Exclusive time (μs)";
            default:
                return "Call count";
        }
    }

    public ProfilerNode getCall(int row) {
        return (ProfilerNode)((List)root).get(row);
    }

    public void sortRoutines(int index, boolean reverse) {
        if (reverse && root instanceof List) {
            Collections.reverse((List)root);
            modelSupport.fireNewRoot();
            return;
        }

        if (!(root instanceof List)) return;
        Collections.sort((List)root, (Comparator<ProfilerNode>)(o1, o2) -> {
            switch (index) {
                case 0:
                    return o2.getName().compareTo(o1.getName());
                case 1:
                    return Integer.compare(o2.getInclusiveTime(), o1.getInclusiveTime());
                case 2:
                    return Integer.compare(o2.getExclusiveTime(), o1.getExclusiveTime());
                default:
                    return Integer.compare(o2.getCallCount(), o1.getCallCount());
            }
        });
        modelSupport.fireNewRoot();
    }
}
