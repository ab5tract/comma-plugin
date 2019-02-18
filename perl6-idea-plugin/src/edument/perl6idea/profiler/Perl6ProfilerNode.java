package edument.perl6idea.profiler;

import java.nio.file.Paths;
import java.util.List;

public class Perl6ProfilerNode {
    private int id;
    private String filename;
    private int line;
    private String name;
    private int inclusiveTime;
    private int exclusiveTime;
    private int callCount;

    public Perl6ProfilerNode(int id, String filename, int line, String name,
                             int inclusiveTime, int exclusiveTime,
                             int callCount) {
        this.id = id;
        this.filename = filename;
        this.line = line;
        this.name = name;
        this.inclusiveTime = inclusiveTime;
        this.exclusiveTime = exclusiveTime;
        this.callCount = callCount;
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

    public int getCallId() {
        return id;
    }
}
