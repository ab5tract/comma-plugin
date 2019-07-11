package edument.perl6idea.profiler.model;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Perl6ProfileCall {
    @Nullable
    public Perl6ProfileCall parent;
    public String name;
    public int time;
    public List<Perl6ProfileCall> callees;
}
