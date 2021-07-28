package edument.perl6idea.debugger;

public class Perl6ObjectValueDescriptor extends Perl6ValueDescriptor {
    private static final String LAL = "Rakudo::Internals::LoweredAwayLexical";

    private final String type;
    private final boolean concrete;
    private final int handle;
    private final String presentableDescription;

    Perl6ObjectValueDescriptor(String name, String type, boolean concrete, int handle,
                               String presentableDescription) {
        super(name);
        this.type = type;
        this.concrete = concrete;
        this.handle = handle;
        this.presentableDescription = presentableDescription;
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
    public String getPresentableDescription(Perl6DebugThread debugThread) {
        return presentableDescription != null ? presentableDescription : getValue();
    }

    public int getHandle() {
        return handle;
    }

    @Override
    public boolean isExpandableNode() {
        return concrete;
    }
}
