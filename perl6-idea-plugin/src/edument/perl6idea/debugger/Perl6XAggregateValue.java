package edument.perl6idea.debugger;

public class Perl6XAggregateValue extends Perl6XNamedValue {
    private Perl6DebugThread debugThread;

    public Perl6XAggregateValue(Perl6ValueDescriptor descriptor, Perl6DebugThread debugThread) {
        super(descriptor);
        this.debugThread = debugThread;
    }

    @Override
    protected Perl6DebugThread getDebugThread() {
        return debugThread;
    }
}
