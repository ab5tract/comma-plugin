package edument.perl6idea.profiler.model;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Perl6ProfileCall {
    public int id;
    @Nullable
    public Perl6ProfileCall parent;
    @Nullable
    public List<Perl6ProfileCall> callees;
    // Data to navigate
    public String name;
    public String filename;
    public String line;
    public int inclusiveTime;
    public int entries;
    public int inlinedEntries;
    public int speshEntries;

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
