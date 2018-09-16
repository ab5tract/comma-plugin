package edument.perl6idea.profiler;

public class CalleeNode {
    private String name;

    public CalleeNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name.isEmpty() ? "<anon>" : name;
    }
}
