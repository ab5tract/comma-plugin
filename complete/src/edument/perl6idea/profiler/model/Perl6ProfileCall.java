package edument.perl6idea.profiler.model;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Perl6ProfileCall {
    private int id;
    private int routineID;
    @Nullable
    private Perl6ProfileCall parent;
    private List<Perl6ProfileCall> callees = new LinkedList<>();
    // Data to navigate
    private String name;
    private String filename;
    private int line;
    private int inclusiveTime;
    private int exclusiveTime;
    private int entries;
    private int inlinedEntries;
    private int speshEntries;

    public Perl6ProfileCall(int id, int routineID,
                            int inclusiveTime, int exclusiveTime,
                            int entries, int speshEntries, int inlinedEntries,
                            String name, String filename, int line) {
        this.id = id;
        this.routineID = routineID;
        this.inclusiveTime = inclusiveTime;
        this.exclusiveTime = exclusiveTime;
        this.entries = entries;
        this.speshEntries = speshEntries;
        this.inlinedEntries = inlinedEntries;
        this.name = name;
        this.filename = filename;
        this.line = line;
    }

    public void setParent(@Nullable Perl6ProfileCall parent) {
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public int getRoutineID() {
        return routineID;
    }

    public String getName() {
        return name.isEmpty() ? "<anon>" : name;
    }

    public String getOriginalFile() {
        return filename;
    }

    public int getLine() {
        return line;
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

    public int getEntriesCount() {
        return entries;
    }

    @Nullable
    public Perl6ProfileCall getParent() {
        return parent;
    }

    public void addCallee(Perl6ProfileCall callee) {
        callees.add(callee);
    }

    public List<Perl6ProfileCall> getCallees() {
        return callees;
    }

    public List<String> calleesPercentageInfo() {
        List<String> lines = new ArrayList<>();
        if (callees == null)
            return null;
        for (Perl6ProfileCall callee : callees) {
            float percent = 100 * (float)callee.inclusiveTime / (float)inclusiveTime;
            String name = callee.name.isEmpty() ? "<anon>" : callee.name;
            lines.add(String.format("%s: %.3f%%", name, percent));
        }
        return lines;
    }

    public float averageCallTime() {
        return (float)inclusiveTime / (float)entries;
    }

    public float inlined() {
        return inlinedEntries / entries;
    }

    public float spesh() {
        return speshEntries / entries;
    }
}
