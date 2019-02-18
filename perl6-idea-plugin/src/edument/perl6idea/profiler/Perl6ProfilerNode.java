package edument.perl6idea.profiler;

import java.nio.file.Paths;
import java.util.List;

public class Perl6ProfilerNode {
    private String filename;
    private int line;
    private String name;
    private int inclusiveTime;
    private int exclusiveTime;
    private int callCount;
    private final List<CalleeNode> callee;

    public Perl6ProfilerNode(String filename, int line, String name,
                             int inclusiveTime, int exclusiveTime,
                             int callCount, List<CalleeNode> callee) {
        this.filename = filename;
        this.line = line;
        this.name = name;
        this.inclusiveTime = inclusiveTime;
        this.exclusiveTime = exclusiveTime;
        this.callCount = callCount;
        this.callee = callee;
    }

    public String getName() {
        return name.isEmpty() ? "<anon>" : name;
    }

    public String getOriginalFile() {
        return filename;
    }

    public String getFilename() {
        return renderFilename() + ":" + line;
    }

    private String renderFilename() {
        if (filename.endsWith(".nqp")) {
            return "<nqp>";
        } else if (filename.startsWith("SETTING:")) {
            return "<CORE SETTING>";
        } else {
            return Paths.get(filename).getFileName().toString();
        }
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

    public int getLine() {
        return line;
    }

    public CalleeNode getCalleeNode(int index) {
        return callee.get(index);
    }

    public int getCalleeSize() {
        return callee.size();
    }
}
