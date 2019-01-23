package edument.perl6idea.debugger;

public class Perl6NativeValueDescriptor extends Perl6ValueDescriptor {
    private String kind;
    private String value;

    Perl6NativeValueDescriptor(String name, String kind, String value) {
        super(name);
        this.kind = kind;
        this.value = value;
    }

    @Override
    public String getKind() {
        return kind;
    }

    @Override
    public String getType() {
        return kind;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean isExpandableNode() {
        return false;
    }
}
