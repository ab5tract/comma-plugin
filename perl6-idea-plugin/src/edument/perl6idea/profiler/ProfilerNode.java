package edument.perl6idea.profiler;

import java.util.List;

public class ProfilerNode {
    private String filename;
    private String name;
    private int inclusiveTime;
    private int exclusiveTime;
    private int callCount;
    private final List<CalleeNode> callee;

    public ProfilerNode(String filename, String name,
                        int inclusiveTime, int exclusiveTime,
                        int callCount, List<CalleeNode> callee) {
        this.filename = filename;
        this.name = name;
        this.inclusiveTime = inclusiveTime;
        this.exclusiveTime = exclusiveTime;
        this.callCount = callCount;
        this.callee = callee;
    }

    public String getName() {
        return (name.isEmpty() ? "<anon>" : name) + " at " + filename;
    }

    public int getInclusiveTime() {
        return inclusiveTime;
    }

    public int getExclusiveTime() {
        return exclusiveTime;
    }

    public int getCallCount() {
        return callCount;
    }

    public CalleeNode getCalleeNode(int index) {
        return callee.get(index);
    }

    public int getCalleeSize() {
        return callee.size();
    }
}
