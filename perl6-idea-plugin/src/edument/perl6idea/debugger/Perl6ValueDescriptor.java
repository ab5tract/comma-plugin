package edument.perl6idea.debugger;

public abstract class Perl6ValueDescriptor {
    private String name;

    Perl6ValueDescriptor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String getKind();
    public abstract String getType();
    public abstract String getValue();
    public abstract boolean isExpandableNode();
}
