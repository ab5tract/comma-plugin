package edument.perl6idea.profiler;

public class ProfilerNode {
    private String name;
    private int inclusiveTime;
    private int exclusiveTime;
    private int callCount;

    public ProfilerNode(String name, int inclusiveTime,
                        int exclusiveTime, int callCount) {
        this.name = name;
        this.inclusiveTime = inclusiveTime;
        this.exclusiveTime = exclusiveTime;
        this.callCount = callCount;
    }

    public String getName() {
        return name;
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
}
