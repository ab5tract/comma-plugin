package edument.perl6idea.profiler.model;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Perl6ProfileCall {
    public int id;
    @Nullable
    public Perl6ProfileCall parent;
    public List<Perl6ProfileCall> callees = new LinkedList<>();
    // Data to navigate
    public String name;
    public String filename;
    public String line;
    public int inclusiveTime;
    public int entries;
    public int inlinedEntries;
    public int speshEntries;

    public Perl6ProfileCall(int id, int inclusiveTime, int entries, int speshEntries, int inlinedEntries,
                            String name, String filename, String line) {
        this.id = id;
        this.inclusiveTime = inclusiveTime;
        this.entries = entries;
        this.inlinedEntries = inlinedEntries;
        this.speshEntries = speshEntries;
        this.name = name;
        this.filename = filename;
        this.line = line;
    }

    public void setParent(@Nullable Perl6ProfileCall parent) {
        this.parent = parent;
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
