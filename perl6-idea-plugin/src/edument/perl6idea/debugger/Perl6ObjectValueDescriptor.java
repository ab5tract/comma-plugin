package edument.perl6idea.debugger;

public class Perl6ObjectValueDescriptor extends Perl6ValueDescriptor {
    private static final String LAL = "Rakudo::Internals::LoweredAwayLexical";

    private String type;
    private boolean concrete;

    Perl6ObjectValueDescriptor(String name, String type, boolean concrete) {
        super(name);
        this.type = type;
        this.concrete = concrete;
    }

    @Override
    public String getKind() {
        return "obj";
    }

    @Override
    public String getType() {
        return type.equals(LAL) ? "<unknown>" : type;
    }

    @Override
    public String getValue() {
        if (type.equals(LAL))
            return "<optimized out>";
        return concrete ? type + ".new" : "(" + type + ")";
    }

    @Override
    public boolean isExpandableNode() {
        return true;
    }
}
