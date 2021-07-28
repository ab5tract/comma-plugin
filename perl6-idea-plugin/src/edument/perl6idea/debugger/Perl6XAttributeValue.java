package edument.perl6idea.debugger;

public class Perl6XAttributeValue extends Perl6XNamedValue {
    private final Perl6DebugThread debugThread;

    public Perl6XAttributeValue(Perl6ValueDescriptor descriptor, Perl6DebugThread debugThread) {
        super(descriptor);
        this.debugThread = debugThread;
    }

    @Override
    protected Perl6DebugThread getDebugThread() {
        return debugThread;
    }
}
