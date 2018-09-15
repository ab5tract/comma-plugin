package edument.perl6idea.profiler;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import java.util.List;

public class Perl6ProfileModel extends AbstractTreeTableModel {
    private final List<ProfilerNode> routines;

    public Perl6ProfileModel(List<ProfilerNode> routines) {
        super(routines);
        this.routines = routines;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof List) {
            return column == 0 ? "Foo": 0;
        }
        else {
            switch (column) {
                case 0:
                    return ((ProfilerNode)node).getName();
                case 1:
                    return ((ProfilerNode)node).getInclusiveTime();
                case 2:
                    return ((ProfilerNode)node).getExclusiveTime();
                default:
                    return ((ProfilerNode)node).getCallCount();
            }
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof List) {
            return ((List)parent).get(index);
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof List) {
            return ((List)parent).size();
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
                return "Inclusive time";
            case 2:
                return "Exclusive time";
            default:
                return "Call count";
        }
    }
}
