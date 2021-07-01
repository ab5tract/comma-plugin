package edument.perl6idea.debugger;

public class Perl6XAttributeValue extends Perl6XNamedValue {
    private final String className;
    private final Perl6DebugThread debugThread;

    public Perl6XAttributeValue(Perl6ValueDescriptor descriptor, Perl6DebugThread debugThread, String className) {
        super(descriptor);
        this.debugThread = debugThread;
        this.className = className;
    }

    @Override
    protected Perl6DebugThread getDebugThread() {
        return debugThread;
    }
}
